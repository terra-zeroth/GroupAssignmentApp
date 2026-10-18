
package com.example.groupprojectapp

import android.content.Intent
import android.content.ActivityNotFoundException
import android.widget.Toast

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.core.net.toUri

import com.example.groupprojectapp.github.data.GitHubCommit
import com.example.groupprojectapp.github.data.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GithubScreen(
    owner: String,
    repo: String,
    onBack: () -> Unit
) {
    var commits by remember {
        mutableStateOf<List<GitHubCommit>>(emptyList())
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    val context = LocalContext.current

    LaunchedEffect(owner, repo) {
        commits = emptyList()
        error = null

        if (owner.isBlank() || repo.isBlank()) {
            error = "Please set up your GitHub repository in Settings."
        } else {
            try {
                commits = RetrofitClient.api.getCommits(
                    owner = owner,
                    repo = repo
                )
            } catch (e: Exception) {
                error = "Failed to load commits: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("GitHub Activity") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )

                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // Display any API or configuration errors
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Open the configured repository in a browser
            if (owner.isNotBlank() && repo.isNotBlank()) {

                Button(
                    onClick = {
                        val url = "https://github.com/$owner/$repo"

                        // Implicit Intent from the lecture
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            url.toUri()
                        )

                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(
                                context,
                                "No browser available",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Open GitHub Repository")
                }
            }

            // Display commits inside the app
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(commits) { commit ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = commit.commit.message,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = commit.commit.author.name
                        )

                        Text(
                            text = commit.commit.author.date
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        HorizontalDivider()
                    }
                }
            }
        }
    }
}