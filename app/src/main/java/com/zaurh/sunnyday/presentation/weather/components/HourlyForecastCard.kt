package com.zaurh.sunnyday.presentation.weather.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zaurh.sunnyday.R
import com.zaurh.sunnyday.domain.model.HourForecast
import com.zaurh.sunnyday.domain.model.TemperatureUnit
import com.zaurh.sunnyday.presentation.common.format
import com.zaurh.sunnyday.presentation.common.iconRes
import com.zaurh.sunnyday.presentation.common.rememberHourFormatter

private val RainBlue = Color(0xFF4FA3E0)

@Composable
fun HourlyForecastCard(
    hours: List<HourForecast>,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalWeatherPalette.current
    val formatter = rememberHourFormatter()

    GlassCard(
        modifier = modifier,
        title = stringResource(R.string.hourly_forecast),
        icon = Icons.Rounded.Schedule,
        contentPadding = false,
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            itemsIndexed(hours, key = { _, hour -> hour.time.toString() }) { index, hour ->
                Column(
                    modifier = Modifier.width(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = if (index == 0) stringResource(R.string.now) else formatter.format(hour.time),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (index == 0) palette.content else palette.contentSecondary,
                    )
                    Spacer(Modifier.height(8.dp))
                    Image(
                        painter = painterResource(hour.condition.iconRes(hour.isDay)),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                    Text(
                        text = if (hour.chanceOfPrecipitation >= 20) "${hour.chanceOfPrecipitation}%" else "",
                        style = MaterialTheme.typography.labelSmall,
                        color = RainBlue,
                        modifier = Modifier.height(16.dp),
                    )
                    Text(
                        text = hour.temperature.format(unit),
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.content,
                    )
                }
            }
        }
    }
}
