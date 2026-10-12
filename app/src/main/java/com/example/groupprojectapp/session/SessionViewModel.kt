package com.example.groupprojectapp.session

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * VIEWMODEL (MVVM): app-level session state. Holds which top-level screen is
 * showing and who is logged in, so rotating the phone no longer resets the app
 * to Login (these were plain `remember` values in MainActivity before).
 *
 * [onLoginSuccess] stores the names and goes to "home", [navigateTo] only
 * changes screen, and [logout] clears the names and returns to "Login".
 * MainActivity gets one instance and shares it with every screen.
 */
class SessionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    // Called on successful login
    fun onLoginSuccess(group: String, user: String) {
        _uiState.update {
            it.copy(
                groupName = group,
                userName = user,
                currentScreen = "home"
            )
        }
    }

    // Called when switching top-level screens
    fun navigateTo(screenName: String) {
        _uiState.update {
            it.copy(currentScreen = screenName)
        }
    }

    // Called when logging out
    fun logout() {
        _uiState.update {
            it.copy(
                groupName = "",
                userName = "",
                currentScreen = "Login"
            )
        }
    }
}