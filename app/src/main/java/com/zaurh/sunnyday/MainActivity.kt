package com.zaurh.sunnyday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.zaurh.sunnyday.presentation.navigation.SunnyDayNavHost
import com.zaurh.sunnyday.ui.theme.SunnyDayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            SunnyDayTheme {
                SunnyDayNavHost()
            }
        }
    }
}
