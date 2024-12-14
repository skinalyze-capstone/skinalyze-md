package com.dicoding.skinalyze.networking

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("send-register")
    fun signUp(@Body request: SignUpRequest): Call<SignUpResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}

