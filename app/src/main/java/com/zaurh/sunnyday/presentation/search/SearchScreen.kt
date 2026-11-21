package com.zaurh.sunnyday.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.TravelExplore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaurh.sunnyday.R
import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.presentation.common.ObserveEffects
import com.zaurh.sunnyday.presentation.common.SystemBarsAppearance
import com.zaurh.sunnyday.presentation.common.message

@Composable
fun SearchRoute(
    onClose: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val keyboard = LocalSoftwareKeyboardController.current

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            SearchEffect.Close -> {
                keyboard?.hide()
                onClose()
            }
        }
    }

    SearchScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun SearchScreen(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
) {
    SystemBarsAppearance(useDarkIcons = !isSystemInDarkTheme())
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            SearchField(
                query = state.query,
                onQueryChange = { onIntent(SearchIntent.QueryChanged(it)) },
                onClear = { onIntent(SearchIntent.ClearQuery) },
                onBack = { onIntent(SearchIntent.Back) },
                onSubmit = { onIntent(SearchIntent.Submit) },
                modifier = Modifier.focusRequester(focusRequester),
            )
            Box(Modifier.fillMaxWidth().height(4.dp)) {
                if (state.isSearching) LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            when {
                state.showRecent && state.recentPlaces.isEmpty() -> EmptyState(
                    icon = Icons.Rounded.TravelExplore,
                    text = stringResource(R.string.search_hint_empty),
                )
                state.showRecent -> PlaceList(
                    header = stringResource(R.string.recent_searches),
                    places = state.recentPlaces,
                    leadingIcon = Icons.Rounded.History,
                    onPlaceClick = { onIntent(SearchIntent.PlaceSelected(it)) },
                    onRemove = { onIntent(SearchIntent.RemoveRecent(it)) },
                )
                state.error != null -> EmptyState(
                    icon = Icons.Rounded.SearchOff,
                    text = state.error.message(),
                )
                state.showNoResults -> EmptyState(
                    icon = Icons.Rounded.SearchOff,
                    text = stringResource(R.string.no_results, state.query.trim()),
                )
                else -> PlaceList(
                    header = null,
                    places = state.suggestions,
                    leadingIcon = Icons.Rounded.LocationOn,
                    onPlaceClick = { onIntent(SearchIntent.PlaceSelected(it)) },
                    onRemove = null,
                )
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        leadingIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.clear))
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}

@Composable
private fun PlaceList(
    header: String?,
    places: List<Place>,
    leadingIcon: ImageVector,
    onPlaceClick: (Place) -> Unit,
    onRemove: ((Place) -> Unit)?,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().imePadding(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        if (header != null) {
            item(key = "header") {
                Text(
                    text = header,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                )
            }
        }
        items(places, key = { it.query }) { place ->
            ListItem(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onPlaceClick(place) },
                headlineContent = { Text(place.name, style = MaterialTheme.typography.titleMedium) },
                supportingContent = place.subtitle.takeIf { it.isNotBlank() }?.let { { Text(it) } },
                leadingContent = {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                leadingIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                },
                trailingContent = onRemove?.let { remove ->
                    {
                        IconButton(onClick = { remove(place) }) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.remove_recent, place.name),
                            )
                        }
                    }
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            )
        }
        item(key = "bottom-inset") { Box(Modifier.navigationBarsPadding()) }
    }
}

@Composable
private fun EmptyState(icon: ImageVector, text: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp).imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}
