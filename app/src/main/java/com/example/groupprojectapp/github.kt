package com.example.groupprojectapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.groupprojectapp.github.GitHubCommit
import com.example.groupprojectapp.github.RetrofitClient

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues


@Composable
fun GithubScreen() {

    var commits by remember {
        mutableStateOf<List<GitHubCommit>>(emptyList())
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        try {
            commits = RetrofitClient.api.getCommits(
                owner = "terra-zeroth",
                repo = "GroupAssignmentApp"
            )
            error = null
        } catch (e: Exception) {
            error = "Failed to load commits: ${e.message}"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "GitHub Activity",
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold
        )

        error?.let {
            Text(
                text = it,
                modifier = Modifier.padding(16.dp)
            )
        }

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

                    HorizontalDivider()
                }
            }
        }
    }
}
