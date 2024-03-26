package com.example.sunnyday.common

import androidx.compose.runtime.Composable
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun formatDate(date: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val outputFormat = SimpleDateFormat("MMMM d", Locale.getDefault())

    val parsedDate = inputFormat.parse(date)
    return outputFormat.format(parsedDate!!)
}