package com.zaurh.sunnyday.presentation.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaurh.sunnyday.R
import com.zaurh.sunnyday.domain.model.TemperatureUnit
import com.zaurh.sunnyday.domain.model.Weather
import com.zaurh.sunnyday.presentation.common.format
import com.zaurh.sunnyday.presentation.common.rememberTimeFormatter
import kotlin.math.roundToInt

private data class Detail(
    val icon: ImageVector,
    val label: String,
    val value: String,
    val caption: String? = null,
)

@Composable
fun WeatherDetailsGrid(
    weather: Weather,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier,
) {
    val current = weather.current
    val today = weather.daily.firstOrNull()
    val timeFormatter = rememberTimeFormatter()
    val imperial = unit == TemperatureUnit.FAHRENHEIT
    val notAvailable = stringResource(R.string.not_available)

    val details = listOf(
        Detail(
            Icons.Rounded.Thermostat,
            stringResource(R.string.feels_like),
            current.feelsLike.format(unit),
            caption = feelsLikeCaption(current.feelsLike.valueIn(unit) - current.temperature.valueIn(unit)),
        ),
        Detail(
            Icons.Rounded.WaterDrop,
            stringResource(R.string.humidity),
            "${current.humidityPercent}%",
        ),
        Detail(
            Icons.Rounded.Air,
            stringResource(R.string.wind),
            if (imperial) {
                stringResource(R.string.wind_mph, current.windMph.roundToInt())
            } else {
                stringResource(R.string.wind_kph, current.windKph.roundToInt())
            },
            caption = current.windDirection.takeIf { it.isNotBlank() },
        ),
        Detail(
            Icons.Rounded.WbSunny,
            stringResource(R.string.uv_index),
            current.uvIndex.roundToInt().toString(),
            caption = uvCaption(current.uvIndex),
        ),
        Detail(
            Icons.Rounded.Compress,
            stringResource(R.string.pressure),
            stringResource(R.string.pressure_hpa, current.pressureMb.roundToInt()),
        ),
        Detail(
            Icons.Rounded.Visibility,
            stringResource(R.string.visibility),
            if (imperial) {
                stringResource(R.string.distance_mi, current.visibilityMiles.roundToInt())
            } else {
                stringResource(R.string.distance_km, current.visibilityKm.roundToInt())
            },
        ),
        Detail(
            Icons.Rounded.WbTwilight,
            stringResource(R.string.sunrise),
            today?.sunrise?.let(timeFormatter::format) ?: notAvailable,
        ),
        Detail(
            Icons.Rounded.NightsStay,
            stringResource(R.string.sunset),
            today?.sunset?.let(timeFormatter::format) ?: notAvailable,
        ),
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        details.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { detail -> DetailTile(detail, Modifier.weight(1f).fillMaxHeight()) }
            }
        }
    }
}

@Composable
private fun DetailTile(detail: Detail, modifier: Modifier = Modifier) {
    val palette = LocalWeatherPalette.current
    GlassCard(modifier = modifier, title = detail.label, icon = detail.icon) {
        Text(
            text = detail.value,
            style = MaterialTheme.typography.headlineSmall,
            color = palette.content,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = detail.caption.orEmpty(),
            style = MaterialTheme.typography.bodySmall,
            color = palette.contentSecondary,
            minLines = 1,
        )
    }
}

@Composable
private fun uvCaption(uv: Double): String = stringResource(
    when {
        uv < 3 -> R.string.uv_low
        uv < 6 -> R.string.uv_moderate
        uv < 8 -> R.string.uv_high
        uv < 11 -> R.string.uv_very_high
        else -> R.string.uv_extreme
    },
)

@Composable
private fun feelsLikeCaption(delta: Double): String = stringResource(
    when {
        delta <= -2 -> R.string.feels_colder
        delta >= 2 -> R.string.feels_warmer
        else -> R.string.feels_similar
    },
)
