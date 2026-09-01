package com.daysleft

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.daysleft.navigation.AppNavigation
import com.daysleft.ui.theme.DaysLeftTheme

/**
 * Main entry activity with Edge-to-Edge support, splash screen, and notification deep link routing.
 */
class MainActivity : ComponentActivity() {

    private val targetEventIdState = mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        enableEdgeToEdge()
        setContent {
            DaysLeftTheme {
                val targetEventId by targetEventIdState
                AppNavigation(
                    targetEventId = targetEventId,
                    onTargetEventHandled = { targetEventIdState.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val eventId = intent?.getLongExtra(EXTRA_EVENT_ID, -1L) ?: -1L
        if (eventId > 0) {
            targetEventIdState.value = eventId
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "com.daysleft.EXTRA_EVENT_ID"
    }
}
