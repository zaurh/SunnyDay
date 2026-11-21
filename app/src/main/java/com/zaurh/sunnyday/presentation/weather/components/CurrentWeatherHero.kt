package com.zaurh.sunnyday.presentation.weather.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaurh.sunnyday.R
import com.zaurh.sunnyday.domain.model.TemperatureUnit
import com.zaurh.sunnyday.domain.model.Weather
import com.zaurh.sunnyday.presentation.common.format
import com.zaurh.sunnyday.presentation.common.iconRes

@Composable
fun CurrentWeatherHero(
    weather: Weather,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalWeatherPalette.current
    val current = weather.current
    val today = weather.daily.firstOrNull()

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(
                current.condition.iconRes(
                    isDay = current.isDay,
                    fullMoon = (today?.moonIllumination ?: 0) >= FULL_MOON_THRESHOLD,
                ),
            ),
            contentDescription = current.conditionText,
            modifier = Modifier.size(120.dp),
        )
        Text(
            text = current.temperature.format(unit),
            style = MaterialTheme.typography.displayLarge,
            color = palette.content,
            modifier = Modifier.padding(start = 24.dp), // optically centre the number despite the "°"
        )
        Text(
            text = current.conditionText,
            style = MaterialTheme.typography.titleLarge,
            color = palette.content,
            textAlign = TextAlign.Center,
        )
        val summary = buildList {
            if (today != null) {
                add(stringResource(R.string.high_low, today.max.format(unit), today.min.format(unit)))
            }
            add(stringResource(R.string.feels_like_short, current.feelsLike.format(unit)))
        }.joinToString("  ·  ")
        Text(
            text = summary,
            style = MaterialTheme.typography.bodyLarge,
            color = palette.contentSecondary,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

private const val FULL_MOON_THRESHOLD = 85
