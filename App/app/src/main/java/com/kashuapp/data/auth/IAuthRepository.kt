package com.kashuapp.data.auth

import io.github.jan.supabase.auth.user.UserInfo

interface IAuthRepository {


    suspend fun login(email: String, password: String): Result<UserInfo>

    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        fatherName: String,
        motherName: String,
        lastName: String
    ): Result<UserInfo>

    fun getCurrentUserId(): UserProfile
}