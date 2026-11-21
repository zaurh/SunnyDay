package com.zaurh.sunnyday.presentation.weather.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaurh.sunnyday.R
import com.zaurh.sunnyday.domain.model.DayForecast
import com.zaurh.sunnyday.domain.model.TemperatureUnit
import com.zaurh.sunnyday.presentation.common.format
import com.zaurh.sunnyday.presentation.common.iconRes
import com.zaurh.sunnyday.presentation.common.shortName
import java.time.LocalDate

private val RangeGradient = Brush.horizontalGradient(
    listOf(Color(0xFF6EC6FF), Color(0xFFFFD54F), Color(0xFFFF8A50)),
)

@Composable
fun DailyForecastCard(
    days: List<DayForecast>,
    today: LocalDate,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalWeatherPalette.current
    val weekMin = days.minOfOrNull { it.min.valueIn(unit) } ?: 0.0
    val weekMax = days.maxOfOrNull { it.max.valueIn(unit) } ?: 0.0

    GlassCard(
        modifier = modifier,
        title = stringResource(R.string.daily_forecast, days.size),
        icon = Icons.Rounded.CalendarMonth,
    ) {
        days.forEachIndexed { index, day ->
            if (index > 0) HorizontalDivider(color = palette.cardBorder)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = if (day.date == today) stringResource(R.string.today) else day.date.dayOfWeek.shortName(),
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.content,
                    modifier = Modifier.width(64.dp),
                )
                Column(
                    modifier = Modifier.width(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(day.condition.iconRes(isDay = true)),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                    )
                    if (day.chanceOfPrecipitation >= 20) {
                        Text(
                            text = "${day.chanceOfPrecipitation}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4FA3E0),
                        )
                    }
                }
                Text(
                    text = day.min.format(unit),
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.contentSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(40.dp),
                )
                TemperatureRangeBar(
                    min = day.min.valueIn(unit),
                    max = day.max.valueIn(unit),
                    rangeMin = weekMin,
                    rangeMax = weekMax,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = day.max.format(unit),
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.content,
                    modifier = Modifier.width(40.dp),
                )
            }
        }
    }
}

/** A track showing where this day's min..max sits within the whole forecast's range. */
@Composable
private fun TemperatureRangeBar(
    min: Double,
    max: Double,
    rangeMin: Double,
    rangeMax: Double,
    modifier: Modifier = Modifier,
) {
    val palette = LocalWeatherPalette.current
    val span = (rangeMax - rangeMin).takeIf { it > 0 } ?: 1.0
    val start = ((min - rangeMin) / span).toFloat().coerceIn(0f, 1f)
    val end = ((max - rangeMin) / span).toFloat().coerceIn(start, 1f)

    BoxWithConstraints(
        modifier = modifier
            .height(6.dp)
            .clip(CircleShape)
            .background(palette.contentSecondary.copy(alpha = 0.2f)),
    ) {
        Box(
            modifier = Modifier
                .offset(x = maxWidth * start)
                .width(maxWidth * (end - start).coerceAtLeast(0.04f))
                .fillMaxHeight()
                .clip(CircleShape)
                .background(RangeGradient),
        )
    }
}
