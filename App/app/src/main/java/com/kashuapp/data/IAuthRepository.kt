package com.kashuapp.data

import io.github.jan.supabase.auth.user.UserInfo
interface IAuthRepository {


    suspend fun login(email:String, password: String): Result<UserInfo>

    suspend fun signUp(email: String, password: String): Result<UserInfo>

}


