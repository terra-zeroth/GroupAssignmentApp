package com.example.groupprojectapp.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Thin wrapper: gets the [LoginViewModel], collects its state and passes it
 * to [LoginContent]. Calls [onSuccess] with (groupName, userName) after a
 * correct login. This is the part that can't be previewed.
 */

@Composable
fun LoginScreen(
    onSuccess: (String, String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LoginContent(
        uiState = uiState,
        onGroupNameChange = viewModel::onGroupNameChange,
        onUserNameChange = viewModel::onUserNameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = {
            viewModel.onLoginClick(onSuccess)
        }
    )
}