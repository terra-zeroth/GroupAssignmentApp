package com.example.groupprojectapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
/**
 * VIEW: the login screen. It has three fields (group name, user name,
 * password) and a Login button.
 *
 * Group name and user name are hoisted: they live in MainActivity and arrive
 * here as parameters, with change callbacks. The password is kept locally
 * with `remember`.
 *
 * Login check: when the button is tapped, it looks the user name up in
 * [userDatabase] (UserData.kt) and compares the password. If it matches,
 * it navigates to "home". Nothing happens on a wrong password.
 */

@Composable
fun LoginScreen(
    groupName: String,
    onGroupNameChange: (String) -> Unit,
    userName: String,
    onUserNameChange: (String) -> Unit,
    onNavigate: (String) -> Unit) {
    var password by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = groupName,
            onValueChange = onGroupNameChange,
            label = { Text("Group Name") }
        )

        OutlinedTextField(
            value = userName,
            onValueChange = onUserNameChange,
            label = { Text("Name") },
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )



        Button(
            onClick = {
                if (userDatabase[userName]?.password == password) {
                    onNavigate("home")
                }
            },
            shape = RectangleShape,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}

