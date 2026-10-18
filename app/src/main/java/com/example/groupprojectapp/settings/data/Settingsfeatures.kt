
package com.example.groupprojectapp.settings.ui

import androidx.compose.runtime.Composable

@Composable
fun SettingsFeature(
    fontScale: Float,
    onFontScaleChange: (Float) -> Unit,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onGithubClick: () -> Unit,
    onBack: () -> Unit
) {
    SettingsContent(
        fontScale = fontScale,
        onFontScaleChange = onFontScaleChange,
        isDarkMode = isDarkMode,
        onDarkModeChange = onDarkModeChange,
        onGithubClick = onGithubClick,
        onBack = onBack
    )
}
