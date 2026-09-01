package com.daysleft.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ==========================================================================
// Days Left — Official Color System
// Aligned with https://thenexvion.github.io/DaysLeftWebsite/
// ==========================================================================

// --- Light Theme Brand & Surface Tokens (from website style.css) ---
val BrandPrimary = Color(0xFF2D52C8)           // --color-primary
val BrandPrimaryHover = Color(0xFF2242A6)      // --color-primary-hover
val BrandPrimaryLight = Color(0xFFEBF0FF)      // --color-primary-light
val BrandPrimaryContainer = Color(0xFF4A6CE2)  // --color-primary-container
val BrandPrimaryFixed = Color(0xFFDCE1FF)      // --color-primary-fixed
val BrandOnPrimary = Color(0xFFFFFFFF)         // --color-on-primary

val LightBg = Color(0xFFFBF8FE)                // --color-bg
val LightSurface = Color(0xFFFFFFFF)           // --color-surface
val LightSurfaceContainer = Color(0xFFF3EFF7)  // --color-surface-container
val LightSurfaceContainerLow = Color(0xFFF8F5FA) // --color-surface-container-low
val LightSurfaceContainerHigh = Color(0xFFEAE5EF) // --color-surface-container-high
val LightSurfaceVariant = Color(0xFFE4E1E7)    // --color-surface-variant

val LightTextPrimary = Color(0xFF1B1B1F)       // --color-text-primary
val LightTextSecondary = Color(0xFF5B5E68)     // --color-text-secondary
val LightTextMuted = Color(0xFF747685)         // --color-text-muted
val LightBorder = Color(0xFFE4E1E7)            // --color-border
val LightBorderSubtle = Color(0xFFEDEBF0)      // --color-border-subtle

// Semantic Light Tokens
val LightSuccess = Color(0xFF1E8E3E)           // --color-success
val LightSuccessBg = Color(0xFFE6F4EA)         // --color-success-bg
val LightSuccessBorder = Color(0xFFCEEAD6)

val LightUrgent = Color(0xFFC5221F)            // Vibrant warning/urgent
val LightUrgentBg = Color(0xFFFCE8E6)
val LightUrgentBorder = Color(0xFFF5C2B8)

val LightError = Color(0xFFB3261E)
val LightErrorContainer = Color(0xFFF9DEDC)
val LightOnErrorContainer = Color(0xFF410E0B)

// --- Dark Theme Brand & Surface Tokens (Harmonious & Professional) ---
val DarkBrandPrimary = Color(0xFFADC6FF)       // Luminous readable primary
val DarkBrandPrimaryHover = Color(0xFF85A5FF)
val DarkBrandPrimaryContainer = Color(0xFF2D52C8) // Anchored to brand blue
val DarkOnBrandPrimary = Color(0xFF00287D)

val DarkBg = Color(0xFF121318)                 // Obsidian with purple-blue undertone
val DarkSurface = Color(0xFF1A1B22)            // Elevated surface
val DarkSurfaceContainerLow = Color(0xFF16171E)
val DarkSurfaceContainer = Color(0xFF20222B)
val DarkSurfaceContainerHigh = Color(0xFF2B2D38)
val DarkSurfaceVariant = Color(0xFF2D303D)

val DarkTextPrimary = Color(0xFFE4E5EE)        // High contrast readable text
val DarkTextSecondary = Color(0xFF9EABC0)      // Balanced muted secondary
val DarkTextMuted = Color(0xFF6E7787)
val DarkBorder = Color(0xFF3C404E)
val DarkBorderSubtle = Color(0xFF282A36)

// Semantic Dark Tokens
val DarkSuccess = Color(0xFF81C995)            // Calibrated green
val DarkSuccessBg = Color(0xFF0F2E1B)
val DarkSuccessBorder = Color(0xFF1E8E3E)

val DarkUrgent = Color(0xFFFF8A80)             // Calibrated coral-amber
val DarkUrgentBg = Color(0xFF381512)
val DarkUrgentBorder = Color(0xFF7E2E25)

val DarkError = Color(0xFFF2B8B5)
val DarkErrorContainer = Color(0xFF601410)
val DarkOnErrorContainer = Color(0xFFF9DEDC)

// --- Status Color Tokens Contract ---
data class StatusColorTokens(
    val urgent: Color,
    val urgentContainer: Color,
    val urgentBorder: Color,
    val upcoming: Color,
    val upcomingContainer: Color,
    val upcomingBorder: Color,
    val today: Color,
    val todayContainer: Color,
    val todayBorder: Color,
    val passed: Color,
    val passedContainer: Color,
    val passedBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color
)

val LightStatusColors = StatusColorTokens(
    urgent = LightUrgent,
    urgentContainer = LightUrgentBg,
    urgentBorder = LightUrgentBorder,
    upcoming = BrandPrimary,
    upcomingContainer = BrandPrimaryLight,
    upcomingBorder = BrandPrimaryFixed,
    today = LightSuccess,
    todayContainer = LightSuccessBg,
    todayBorder = LightSuccessBorder,
    passed = LightTextSecondary,
    passedContainer = LightSurfaceContainer,
    passedBorder = LightBorderSubtle,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textTertiary = LightTextMuted
)

val DarkStatusColors = StatusColorTokens(
    urgent = DarkUrgent,
    urgentContainer = DarkUrgentBg,
    urgentBorder = DarkUrgentBorder,
    upcoming = DarkBrandPrimary,
    upcomingContainer = DarkBrandPrimaryContainer.copy(alpha = 0.35f),
    upcomingBorder = DarkBrandPrimaryContainer.copy(alpha = 0.7f),
    today = DarkSuccess,
    todayContainer = DarkSuccessBg,
    todayBorder = DarkSuccessBorder,
    passed = DarkTextSecondary,
    passedContainer = DarkSurfaceContainerLow,
    passedBorder = DarkBorderSubtle,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textTertiary = DarkTextMuted
)

val LocalStatusColors = staticCompositionLocalOf { LightStatusColors }
