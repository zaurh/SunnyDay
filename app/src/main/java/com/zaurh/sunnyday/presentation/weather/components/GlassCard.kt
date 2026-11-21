package com.zaurh.sunnyday.presentation.weather.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zaurh.sunnyday.presentation.common.DayPeriod
import com.zaurh.sunnyday.presentation.common.WeatherPalette
import com.zaurh.sunnyday.presentation.common.palette

val LocalWeatherPalette = staticCompositionLocalOf<WeatherPalette> { DayPeriod.MORNING.palette() }

/** Translucent card that lets the illustrated background show through. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector? = null,
    contentPadding: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = LocalWeatherPalette.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = palette.card,
        contentColor = palette.content,
        border = BorderStroke(1.dp, palette.cardBorder),
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            if (title != null) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = palette.contentSecondary,
                        )
                    }
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.contentSecondary,
                    )
                }
            }
            Column(
                modifier = if (contentPadding) Modifier.padding(horizontal = 16.dp) else Modifier,
                content = content,
            )
        }
    }
}
