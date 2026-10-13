package com.example.groupprojectapp.ui.theme

import androidx.compose.ui.graphics.Color

// BRAND PALETTE (light mode)
val Navy = Color(0xFF081697)        // text, border lines
val Blue = Color(0xFF1466B2)        // buttons
val LightBlue = Color(0xFF84B1DA)   // top bars
val Periwinkle = Color(0xFF8491DA)  // left pane, chips
val OffWhite = Color(0xFFF8F8F2)    // background
val Red = Color(0xFFDE5757)         // errors, overdue, today line

// DARK MODE SURFACES (same navy/blue hues, darkened)
val DarkNavy = Color(0xFF0B1030)        // background
val DarkPeriwinkle = Color(0xFF2A3170)  // left pane
val DarkBlue = Color(0xFF1A2560)        // top bars, sheets

// TASK STATUS COLOURS (same in light and dark mode)
// Overdue isn't here: it uses MaterialTheme.colorScheme.error
val StatusDone = Color(0xFF73DDAD)        // green
val StatusInProgress = Color(0xFFFFEA9C)  // yellow
val StatusTodo = Color(0xFFFF8A8A)        // coral