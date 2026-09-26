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


@Composable
fun LoginScreen(
    groupName: String,
    onGroupNameChange: (String) -> Unit,
    userName: String,
    onUserNameChange: (String) -> Unit,
    onNavigate: (String) -> Unit) {
    var password by remember { mutableStateOf("") }
    val validAccounts = mapOf(
        "Manasviba" to "password1",
        "Aliyah" to "password2",
        "Twisha" to "password3",
        "Test" to "password4"

    )

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
                if (validAccounts[userName] == password) {
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

