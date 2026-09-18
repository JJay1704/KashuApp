package com.kashuapp.feature.auth.login

import android.annotation.SuppressLint
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
         try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
                if (user != null){
                    return Result.success(user)
                }else{
                  return  Result.failure(Exception("Usuario no encontrado"))
                }
        } catch (e: Exception) {
           return Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
         try {
            auth.sendPasswordResetEmail(email).await()
           return    Result.success(Unit)
        } catch (e: Exception) {
           return Result.failure(e)
        }
    }
}