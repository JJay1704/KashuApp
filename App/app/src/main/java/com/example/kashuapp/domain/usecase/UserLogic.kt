package com.example.kashuapp.domain.usecase

import com.example.kashuapp.data.repository.UserRepository
import com.example.kashuapp.domain.model.User

class UserLogic(val repo: UserRepository = UserRepository()) {

    suspend fun executeLogin(userName: String, password: String): Result<User> {
        if (userName.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Digite los parámetros"))
        }

        val user = repo.authenticate(userName, password)
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Usuario o contraseña incorrectos"))
        }
    }
}
