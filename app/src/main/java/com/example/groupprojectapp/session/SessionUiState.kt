package com.example.groupprojectapp.session

/** What the app needs to remember across screens: the current top-level screen, group name and user name. */
data class SessionUiState(
    val currentScreen: String = "Login",
    val groupName: String = "",
    val userName: String = "",
    val selectedEntryId: Long? = null, // for editing documentations
    val selectedTaskId: Long? = null, // which task's document to show (null = all)
    val docsBackTarget: String = "home" // where Documentation's Back goes
    // because documentation can be opened from two places - home and Timeline
    // so back should return to whichever one you came from
)
