package com.kashuapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kashuapp.ui.home.HomeScreen
import com.kashuapp.ui.login.LoginScreen
import com.kashuapp.ui.signUp.RegisterView
import com.kashuapp.ui.theme.KashuAppTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KashuAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = "login"

                    ){
                        composable ("login")
                        {

                            LoginScreen(
                                onNavigateToHome = {
                                    navController.navigate("home") {

                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }
                        composable("home"){
                            HomeScreen()
                        }
                        composable("register"){
                            RegisterView(
                                onNavigateToHome = {
                                    navController.navigate("home")
                                                   },
                                onNavigateToLogin = {navController.navigate("login")})

                        }

                    }
                }
            }
        }
    }
}








