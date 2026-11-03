package com.kashuapp.data.auth


interface IAuthRepository {


    suspend fun login(email: String, password: String): Result<Unit>


    suspend fun logOut(): Result<Unit>
    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        fatherName: String,
        motherName: String,
        lastName: String
    ): Result<Unit>

    fun getCurrentUserId(): UserProfile


    suspend fun sendCode(email: String): Result<Unit>
    suspend fun confirmToken(email: String, code: String): Result<Unit>
    suspend fun updatePassword(password: String): Result <Unit>
}