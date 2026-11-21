package com.zaurh.sunnyday.presentation.common

import android.text.format.DateFormat
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.zaurh.sunnyday.R
import com.zaurh.sunnyday.domain.model.DataError
import com.zaurh.sunnyday.domain.model.Temperature
import com.zaurh.sunnyday.domain.model.TemperatureUnit
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

fun Temperature.format(unit: TemperatureUnit): String = "${valueIn(unit).roundToInt()}°"

/** Formats clock times following the device's 12/24-hour preference. */
@Composable
fun rememberTimeFormatter(): DateTimeFormatter {
    val context = LocalContext.current
    val is24h = DateFormat.is24HourFormat(context)
    return remember(is24h) {
        DateTimeFormatter.ofPattern(if (is24h) "HH:mm" else "h:mm a", Locale.getDefault())
    }
}

@Composable
fun rememberHourFormatter(): DateTimeFormatter {
    val context = LocalContext.current
    val is24h = DateFormat.is24HourFormat(context)
    return remember(is24h) {
        DateTimeFormatter.ofPattern(if (is24h) "HH:mm" else "h a", Locale.getDefault())
    }
}

fun LocalTime.format(formatter: DateTimeFormatter): String = formatter.format(this)

fun java.time.DayOfWeek.shortName(): String =
    getDisplayName(TextStyle.SHORT, Locale.getDefault())

@StringRes
fun DataError.messageRes(): Int = when (this) {
    DataError.NO_INTERNET -> R.string.error_no_internet
    DataError.LOCATION_NOT_FOUND -> R.string.error_location_not_found
    DataError.SERVER -> R.string.error_server
    DataError.UNKNOWN -> R.string.error_unknown
}

@Composable
fun DataError.message(): String = stringResource(messageRes())
