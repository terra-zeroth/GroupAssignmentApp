package com.example.groupprojectapp.github

import com.example.groupprojectapp.github.data.GitHubCommit
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApi {

    @GET("repos/{owner}/{repo}/commits")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): List<GitHubCommit>
}
