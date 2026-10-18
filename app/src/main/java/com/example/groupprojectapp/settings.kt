package com.example.groupprojectapp

import androidx.compose.runtime.Composable
import com.example.groupprojectapp.settings.ui.SettingsFeature

@Composable
fun SettingsScreen(
    fontScale: Float,
    onFontScaleChange: (Float) -> Unit,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onGithubClick: () -> Unit,
    onBack: () -> Unit
) {
    SettingsFeature(
        fontScale = fontScale,
        onFontScaleChange = onFontScaleChange,
        isDarkMode = isDarkMode,
        onDarkModeChange = onDarkModeChange,
        onGithubClick = onGithubClick,
        onBack = onBack
    )
}
