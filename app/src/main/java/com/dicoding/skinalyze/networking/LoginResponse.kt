package com.dicoding.skinalyze.networking

data class LoginResponse(
    val message: String,
    val user: UserData?
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val gender: String,
    val date_of_birth: String
)
