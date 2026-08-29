package com.daysleft.navigation

import androidx.compose.runtime.Composable
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
object AddEventRoute

@Serializable
data class EditEventRoute(val eventId: Long)

@Serializable
data class EventDetailsRoute(val eventId: Long)

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            HomeScreen(
                onAddEvent = { navController.navigate(AddEventRoute) },
                onEventClick = { eventId ->
                    navController.navigate(EventDetailsRoute(eventId))
                }
            )
        }

        composable<AddEventRoute> {
            AddEventScreen(
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
