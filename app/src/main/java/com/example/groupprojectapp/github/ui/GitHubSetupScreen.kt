package com.example.groupprojectapp.github.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun GithubSetupScreen(
    onBack: () ->Unit,
    onSave: (String, String) -> Unit
) {
    var owner by remember { mutableStateOf("") }
    var repo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "GitHub Setup",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = owner,
            onValueChange = { owner = it },
            label = { Text("GitHub Owner") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = repo,
            onValueChange = { repo = it },
            label = { Text("Repository Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (error) {
            Text(
                text = "Please enter a valid owner and repository.",
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                if (owner.isNotBlank() && repo.isNotBlank()) {
                    onSave(owner.trim(), repo.trim())
                } else {
                    error = true
                }
            }
        ) {
            Text("Save")
        }

        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
    }
}
