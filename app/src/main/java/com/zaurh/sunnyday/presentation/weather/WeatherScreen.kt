package com.zaurh.sunnyday.presentation.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaurh.sunnyday.R
import com.zaurh.sunnyday.domain.model.DataError
import com.zaurh.sunnyday.domain.model.Weather
import com.zaurh.sunnyday.presentation.common.DayPeriod
import com.zaurh.sunnyday.presentation.common.ObserveEffects
import com.zaurh.sunnyday.presentation.common.SystemBarsAppearance
import com.zaurh.sunnyday.presentation.common.backgroundRes
import com.zaurh.sunnyday.presentation.common.dayPeriod
import com.zaurh.sunnyday.presentation.common.message
import com.zaurh.sunnyday.presentation.common.messageRes
import com.zaurh.sunnyday.presentation.common.palette
import com.zaurh.sunnyday.presentation.common.rememberTimeFormatter
import com.zaurh.sunnyday.presentation.common.shortName
import com.zaurh.sunnyday.presentation.weather.components.CurrentWeatherHero
import com.zaurh.sunnyday.presentation.weather.components.DailyForecastCard
import com.zaurh.sunnyday.presentation.weather.components.HourlyForecastCard
import com.zaurh.sunnyday.presentation.weather.components.LocalWeatherPalette
import com.zaurh.sunnyday.presentation.weather.components.WeatherDetailsGrid
import com.zaurh.sunnyday.presentation.weather.components.WeatherTopBar

/** Stateful entry point: wires the ViewModel to the stateless [WeatherScreen]. */
@Composable
fun WeatherRoute(
    onNavigateToSearch: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onIntent(WeatherIntent.ScreenResumed)
    }

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            WeatherEffect.NavigateToSearch -> onNavigateToSearch()
            is WeatherEffect.ShowError -> snackbarHostState.showSnackbar(
                context.getString(effect.error.messageRes()),
            )
        }
    }

    WeatherScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    state: WeatherState,
    snackbarHostState: SnackbarHostState,
    onIntent: (WeatherIntent) -> Unit,
) {
    val period = state.weather?.dayPeriod() ?: DayPeriod.MORNING
    val palette = period.palette()
    SystemBarsAppearance(useDarkIcons = palette.useDarkSystemIcons)

    CompositionLocalProvider(LocalWeatherPalette provides palette) {
        Scaffold(
            containerColor = palette.sky,
            contentWindowInsets = WindowInsets(0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { innerPadding ->
            Box(Modifier.fillMaxSize().padding(innerPadding)) {
                WeatherBackground(period)

                AnimatedContent(
                    targetState = when {
                        state.weather != null -> ContentKind.DATA
                        state.error != null -> ContentKind.ERROR
                        else -> ContentKind.LOADING
                    },
                    transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
                    label = "weather-content",
                ) { kind ->
                    when (kind) {
                        ContentKind.LOADING -> LoadingContent()
                        ContentKind.ERROR -> ErrorContent(
                            error = state.error ?: DataError.UNKNOWN,
                            onRetry = { onIntent(WeatherIntent.Retry) },
                            onChangeCity = { onIntent(WeatherIntent.OpenSearch) },
                        )
                        ContentKind.DATA -> state.weather?.let { weather ->
                            PullToRefreshBox(
                                isRefreshing = state.isRefreshing,
                                onRefresh = { onIntent(WeatherIntent.Refresh) },
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                WeatherContent(state = state, weather = weather, onIntent = onIntent)
                            }
                        }
                    }
                }

                // Keeps the status bar legible while content scrolls underneath it.
                Box(
                    Modifier
                        .fillMaxWidth()
                        .windowInsetsTopHeight(WindowInsets.statusBars)
                        .background(
                            Brush.verticalGradient(listOf(palette.sky, palette.sky.copy(alpha = 0.7f))),
                        ),
                )
            }
        }
    }
}

private enum class ContentKind { LOADING, ERROR, DATA }

@Composable
private fun WeatherBackground(period: DayPeriod) {
    val palette = LocalWeatherPalette.current
    Crossfade(targetState = period, animationSpec = tween(600), label = "background") { target ->
        Box(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(target.backgroundRes()),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.BottomCenter,
                modifier = Modifier.fillMaxSize(),
            )
            // Darken the mountains slightly so the cards on top stay readable.
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.45f to Color.Transparent,
                            1f to Color.Black.copy(alpha = if (palette.useDarkSystemIcons) 0.25f else 0.4f),
                        ),
                    ),
            )
        }
    }
}

@Composable
private fun WeatherContent(
    state: WeatherState,
    weather: Weather,
    onIntent: (WeatherIntent) -> Unit,
) {
    val timeFormatter = rememberTimeFormatter()
    val localTime = weather.location.localTime
    val subtitle = listOf(
        listOf(weather.location.region, weather.location.country)
            .filter { it.isNotBlank() }.distinct().joinToString(", "),
        "${localTime.dayOfWeek.shortName()} ${timeFormatter.format(localTime)}",
    ).filter { it.isNotBlank() }.joinToString("  ·  ")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item(key = "top-bar") {
            WeatherTopBar(
                title = weather.location.name,
                subtitle = subtitle,
                unit = state.unit,
                onPlaceClick = { onIntent(WeatherIntent.OpenSearch) },
                onToggleUnit = { onIntent(WeatherIntent.ToggleUnit) },
                onSearchClick = { onIntent(WeatherIntent.OpenSearch) },
                modifier = Modifier.statusBarsPadding().padding(top = 8.dp).widthIn(max = 640.dp),
            )
        }
        item(key = "hero") {
            CurrentWeatherHero(
                weather = weather,
                unit = state.unit,
                modifier = Modifier.padding(top = 8.dp, bottom = 120.dp),
            )
        }
        val cardModifier = Modifier
            .widthIn(max = 640.dp)
            .padding(horizontal = 16.dp, vertical = 6.dp)
        if (weather.hourly.isNotEmpty()) {
            item(key = "hourly") {
                HourlyForecastCard(hours = weather.hourly, unit = state.unit, modifier = cardModifier)
            }
        }
        if (weather.daily.isNotEmpty()) {
            item(key = "daily") {
                DailyForecastCard(
                    days = weather.daily,
                    today = localTime.toLocalDate(),
                    unit = state.unit,
                    modifier = cardModifier,
                )
            }
        }
        item(key = "details") {
            WeatherDetailsGrid(weather = weather, unit = state.unit, modifier = cardModifier)
        }
        item(key = "footer") {
            Footer(
                lastUpdated = timeFormatter.format(weather.current.lastUpdated),
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing).padding(top = 8.dp),
            )
        }
    }
}

/** WeatherAPI's free plan requires a visible link back to weatherapi.com. */
@Composable
private fun Footer(lastUpdated: String, modifier: Modifier = Modifier) {
    val palette = LocalWeatherPalette.current
    val poweredBy = stringResource(R.string.powered_by)
    val text = buildAnnotatedString {
        append(stringResource(R.string.last_updated, lastUpdated))
        append("  ·  ")
        withLink(
            LinkAnnotation.Url(
                url = "https://www.weatherapi.com/",
                styles = TextLinkStyles(SpanStyle(textDecoration = TextDecoration.Underline)),
            ),
        ) { append(poweredBy) }
    }
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = Color.White.copy(alpha = 0.85f),
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
    )
}

@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = LocalWeatherPalette.current.content)
    }
}

@Composable
private fun ErrorContent(
    error: DataError,
    onRetry: () -> Unit,
    onChangeCity: () -> Unit,
) {
    val palette = LocalWeatherPalette.current
    Box(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier.padding(top = 96.dp).widthIn(max = 420.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.CloudOff,
                contentDescription = null,
                tint = palette.content,
                modifier = Modifier.size(56.dp),
            )
            Text(
                text = error.message(),
                style = MaterialTheme.typography.titleMedium,
                color = palette.content,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.content,
                    contentColor = palette.sky,
                ),
            ) { Text(stringResource(R.string.retry)) }
            OutlinedButton(onClick = onChangeCity) {
                Text(stringResource(R.string.change_city), color = palette.content)
            }
        }
    }
}

