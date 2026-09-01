package com.daysleft.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.daysleft.ui.add.AddEventScreen
import com.daysleft.ui.details.EventDetailsScreen
import com.daysleft.ui.edit.EditEventScreen
import com.daysleft.ui.home.HomeScreen
import kotlinx.serialization.Serializable

// Type-safe route definitions
@Serializable
object HomeRoute

@Serializable
data class AddEventRoute(val initialTitle: String = "")

@Serializable
data class EditEventRoute(val eventId: Long)

@Serializable
data class EventDetailsRoute(val eventId: Long)

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    targetEventId: Long? = null,
    onTargetEventHandled: () -> Unit = {}
) {
    LaunchedEffect(targetEventId) {
        if (targetEventId != null && targetEventId > 0) {
            navController.navigate(EventDetailsRoute(targetEventId))
            onTargetEventHandled()
        }
    }

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            HomeScreen(
                onAddEvent = { templateTitle ->
                    navController.navigate(AddEventRoute(initialTitle = templateTitle))
                },
                onEventClick = { eventId ->
                    navController.navigate(EventDetailsRoute(eventId))
                }
            )
        }

        composable<AddEventRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<AddEventRoute>()
            AddEventScreen(
                initialTitle = route.initialTitle,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<EditEventRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<EditEventRoute>()
            EditEventScreen(
                eventId = route.eventId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<EventDetailsRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<EventDetailsRoute>()
            EventDetailsScreen(
                eventId = route.eventId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { eventId ->
                    navController.navigate(EditEventRoute(eventId))
                },
                onEventDeleted = {
                    navController.popBackStack(HomeRoute, inclusive = false)
                }
            )
        }
    }
}
