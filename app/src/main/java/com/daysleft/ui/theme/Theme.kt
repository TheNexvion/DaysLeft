package com.daysleft.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandOnPrimary,
    primaryContainer = BrandPrimaryLight,
    onPrimaryContainer = BrandPrimary,
    secondary = LightTextSecondary,
    onSecondary = BrandOnPrimary,
    secondaryContainer = LightSurfaceContainer,
    onSecondaryContainer = LightTextPrimary,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    surfaceContainerLowest = LightSurface,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceVariant,
    surfaceDim = LightSurfaceContainerLow,
    error = LightError,
    onError = BrandOnPrimary,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    outline = LightBorder,
    outlineVariant = LightBorderSubtle
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkBrandPrimary,
    onPrimary = DarkOnBrandPrimary,
    primaryContainer = DarkBrandPrimaryContainer,
    onPrimaryContainer = BrandPrimaryFixed,
    secondary = DarkTextSecondary,
    onSecondary = DarkTextPrimary,
    secondaryContainer = DarkSurfaceContainer,
    onSecondaryContainer = DarkTextPrimary,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    surfaceContainerLowest = DarkBg,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceVariant,
    surfaceDim = DarkSurfaceContainerLow,
    error = DarkError,
    onError = DarkErrorContainer,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    outline = DarkBorder,
    outlineVariant = DarkBorderSubtle
)

@Composable
fun DaysLeftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val statusColors = if (darkTheme) DarkStatusColors else LightStatusColors
    val spacing = Spacing()

    CompositionLocalProvider(
        LocalSpacing provides spacing,
        LocalStatusColors provides statusColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = AppShapes,
            typography = Typography,
            content = content
        )
    }
}

object AppTheme {
    val spacing: Spacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current

    val statusColors: StatusColorTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalStatusColors.current

    val shapes: ComponentShapes
        get() = ComponentShapes
}
