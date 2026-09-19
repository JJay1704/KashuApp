package com.kashuapp.data


import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.providers.builtin.Email
class SupabaseAuthRepository : IAuthRepository {


    override suspend fun login(email: String, password: String): Result<UserInfo> {

        return try {


            KashuSupaBase.auth.signInWith(Email){
                this.email = email
                this.password = password
            }
            val user = KashuSupaBase.auth.currentUserOrNull()


            if (user != null) {


                 Result.success(user)
            } else {
                 Result.failure(Exception("Usuario no encontrado"))

            }


        } catch (e: Exception) {

             Result.failure(e)

        }


    }
    override suspend fun signUp(email: String, password: String): Result<UserInfo> {
        return try {

            KashuSupaBase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val user = KashuSupaBase.auth.currentUserOrNull()
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Error al registrar el usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}














