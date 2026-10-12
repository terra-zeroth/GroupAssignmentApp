package com.example.groupprojectapp.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.groupprojectapp.userDatabase

/**
 * VIEW (MVVM): the login form UI only. It receives [LoginUiState] and one
 * lambda per user action, and holds no state or logic itself. That is what
 * makes it previewable. Shows red fields and an error message when
 * uiState.loginError is true.
 */
@Composable
fun LoginContent(
    uiState: LoginUiState,
    onGroupNameChange: (String) -> Unit,
    onUserNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = uiState.groupName,
            onValueChange = onGroupNameChange,
            label = { Text("Group Name") },
            isError = uiState.loginError
        )

        OutlinedTextField(
            value = uiState.userName,
            onValueChange = onUserNameChange,
            label = { Text("Name") },
            isError = uiState.loginError
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.loginError
        )

        if (uiState.loginError) {
            Text(
                text = "Incorrect username or password. Please try again.",
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = onLoginClick,
            shape = RectangleShape,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    LoginContent(
        uiState = LoginUiState(
            groupName = "Group A",
            userName = "Alex",
            password = ""
        ),
        onGroupNameChange = {},
        onUserNameChange = {},
        onPasswordChange = {},
        onLoginClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun LoginContentErrorPreview() {
    LoginContent(
        uiState = LoginUiState(
            groupName = "Group A",
            userName = "Alex",
            password = "",
            loginError = true
        ),
        onGroupNameChange = {},
        onUserNameChange = {},
        onPasswordChange = {},
        onLoginClick = {}
    )
}