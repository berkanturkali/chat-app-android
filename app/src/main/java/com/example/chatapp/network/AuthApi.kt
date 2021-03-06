package com.example.chatapp.network

import com.example.chatapp.model.TokenResponse
import com.example.chatapp.model.User
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("account/signup")
    suspend fun signup(@Body user: User): Response<String>

    @POST("account/login")
    suspend fun login(@Body credentials: JsonObject): Response<TokenResponse>
}