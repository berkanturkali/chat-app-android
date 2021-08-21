package com.example.chatapp.repo

import com.example.chatapp.model.User
import com.example.chatapp.network.ChatApi
import com.example.chatapp.utils.apiCall
import com.google.gson.JsonObject
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepoImpl @Inject constructor(
    private val api: ChatApi,
    private val retrofit: Retrofit
):UserRepo {
    override suspend fun signup(user: User) = apiCall(retrofit) { api.signup(user) }
    override suspend fun login(credentials: JsonObject) = apiCall(retrofit) { api.login(credentials) }
    override suspend fun getMe() = apiCall(retrofit) { api.getMe() }
}