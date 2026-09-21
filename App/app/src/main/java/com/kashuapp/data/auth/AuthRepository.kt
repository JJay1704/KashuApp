package com.kashuapp.data.auth

import com.kashuapp.data.KashuSupaBase
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository : IAuthRepository {


    override suspend fun login(email: String, password: String): Result<UserInfo> {

         try {


            KashuSupaBase.auth.signInWith(Email){
                this.email = email
                this.password = password
            }
            val user = KashuSupaBase.auth.currentUserOrNull()


            if (user != null) {


               return  Result.success(user)
            } else {
                 return Result.failure(Exception("Usuario no encontrado"))

            }


        } catch (e: Exception) {

           return   Result.failure(e)

        }


    }
    override suspend fun signUp(  email: String,
                                  password: String,
                                  fullName: String,
                                  fatherName: String,
                                  motherName: String,
                                  lastName : String): Result<UserInfo> {
         try {

            KashuSupaBase.auth.signUpWith(Email) {
                this.email = email
                this.password = password

                this.data = buildJsonObject {
                    put("full_name", fullName)
                    put("father_lastName", fatherName)
                    put("mother_lastName", motherName)
                    put("last_name", lastName)
                }


            }
            val user = KashuSupaBase.auth.currentUserOrNull()
            if (user != null) {
                return Result.success(user)
            } else {
                return Result.failure(Exception("Error al registrar el usuario"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }


}