package com.daysleft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.daysleft.navigation.AppNavigation
import com.daysleft.ui.theme.DaysLeftTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            DaysLeftTheme {
                AppNavigation()
            }
        }
    }
}
