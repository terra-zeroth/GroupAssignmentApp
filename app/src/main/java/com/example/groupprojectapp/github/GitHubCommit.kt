package com.example.groupprojectapp.github

data class GitHubCommit(
    val sha: String,
    val commit: CommitDetails,
    val html_url: String
)

data class CommitDetails(
    val message: String,
    val author: CommitAuthor
)

data class CommitAuthor(
    val name: String,
    val date: String
)