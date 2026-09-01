package com.daysleft.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Specific component shapes for strict design consistency
object ComponentShapes {
    val Card = RoundedCornerShape(16.dp)
    val TextField = RoundedCornerShape(12.dp)
    val Dialog = RoundedCornerShape(20.dp)
    val Chip = RoundedCornerShape(8.dp)
    val Pill = CircleShape
    val Badge = CircleShape
}
