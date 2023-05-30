package com.example.sunnyday.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ConditionImage(condition: Int, modifier: Modifier = Modifier) {
    Image(
        contentScale = ContentScale.Crop,
        modifier = modifier.size(100.dp),
        painter = painterResource(condition),
        contentDescription = ""
    )
}