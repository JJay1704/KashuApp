package com.example.kashuapp.core.navigation

sealed class Screen (val route: String){
    object Login : Screen("login_screen")
    object Dashboard : Screen("dashboard_screen")
}