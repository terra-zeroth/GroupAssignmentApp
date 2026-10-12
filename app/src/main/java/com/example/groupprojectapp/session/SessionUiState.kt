package com.example.groupprojectapp.session

/** What the app needs to remember across screens: the current top-level screen, group name and user name. */
data class SessionUiState(
    val currentScreen: String = "Login",
    val groupName: String = "",
    val userName: String = "",
)
