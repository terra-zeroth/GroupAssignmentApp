package com.example.groupprojectapp.login.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.groupprojectapp.UserData
import com.example.groupprojectapp.userDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/** Everything the login screen displays: the three form fields and whether the last attempt failed. */
data class LoginUiState(
    // assuming groupName, password. username can be changed later
    val groupName: String = "",
    val userName: String = "",
    val password: String = "", // can default to letMeIn but raises a security issue
    val loginError: Boolean = false
)

/**
 * VIEWMODEL (MVVM): holds the login form state and does the password check.
 *
 * Field changes update [LoginUiState] with .copy() and clear any error.
 * [onLoginClick] looks the user name up in [userDatabase] (UserData.kt). On a
 * match it resets the form and calls the success callback with the group and
 * user name. Otherwise it sets loginError so the screen shows a message.
 *
 * The callback is passed in per click, not stored in the constructor: the
 * ViewModel survives rotation, so a stored callback would go stale.
 */
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onGroupNameChange(newGroup: String) {
        _uiState.update {
            it.copy(groupName = newGroup, loginError = false)
        }
    }

    fun onUserNameChange(newName: String) {
        _uiState.update {
            it.copy(userName = newName, loginError = false)
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(password = newPassword, loginError = false)
        }
    }

    fun onLoginClick(onSuccess: (String, String) -> Unit) {
        val group = uiState.value.groupName.trim()
        val user = uiState.value.userName.trim()
        val pass = uiState.value.password.trim()

        val correctPassword = userDatabase[user]?.password

        if (correctPassword == pass) {
            // Reset the form fields back to blank before triggering navigation
            _uiState.value = LoginUiState()
            onSuccess(group, user)
        } else {
            Log.d("LOGIN", "wrong password") // TODO: remove this Log since it was for testing
            _uiState.update { it.copy(loginError = true) }
        }
    }
}