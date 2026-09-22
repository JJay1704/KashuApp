package com.kashuapp.data.auth

import com.kashuapp.data.KashuSupaBase
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class AuthRepository : IAuthRepository {


    override suspend fun login(email: String, password: String): Result<UserInfo> {

        try {


            KashuSupaBase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = KashuSupaBase.auth.currentUserOrNull()




            if (user != null) {


                return Result.success(user)
            } else {
                return Result.failure(Exception("Usuario no encontrado"))

            }


        } catch (e: Exception) {

            return Result.failure(e)

        }


    }

    override fun getCurrentUserId(): UserProfile {
        val user = KashuSupaBase.auth.currentUserOrNull()

        if (user == null) {
            return UserProfile(id = "Not Gotten")
        }
        val metadata = user.userMetadata
        var fullName = ""
        var fatherName = ""
        var motherName = ""
        var lastName = ""
        if (metadata != null) {
            val fn = metadata["full_name"]
            if (fn != null) fullName = fn.jsonPrimitive.content
            val fn2 = metadata["father_lastName"]
            if (fn2 != null) fatherName = fn2.jsonPrimitive.content
            val mn = metadata["mother_lastName"]
            if (mn != null) motherName = mn.jsonPrimitive.content
            val ln = metadata["last_name"]
            if (ln != null) lastName = ln.jsonPrimitive.content
        }
        var email = ""
        if (user.email != null) {
            email = user.email!!
        }
        return UserProfile(
            id = user.id,
            email = email,
            fullName = fullName,
            fatherName = fatherName,
            motherName = motherName,
            lastName = lastName
        )
    }






    override suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        fatherName: String,
        motherName: String,
        lastName: String
    ): Result<UserInfo> {
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