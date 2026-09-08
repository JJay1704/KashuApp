package com.example.kashuapp.data.repository

import com.example.kashuapp.domain.model.User
import kotlinx.coroutines.delay

class UserRepository {


    suspend fun authenticate(username: String, pass: String): User? {
        delay(1000) // Simula 1 segundo de espera
        // Usuario de prueba para tu proyecto:
        return if (username == "admin" && pass == "1234") {
            User(id = "1", username = "admin", email = "admin@kashu.com", password =    "admin")
        } else {
            null // Si no coincide, retorna null (error de login)
        }
    }
}