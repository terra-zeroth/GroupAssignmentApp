package com.example.groupprojectapp

data class UserData(
    val password: String,
    val tasks: List<String> = emptyList()
)

val userDatabase = mapOf(
    "Manasviba" to UserData(password = "password1"),
    "Aliyah" to UserData(password = "password2"),
    "Twisha" to UserData(password = "password3"),
    "Test" to UserData(password = "password4")
)