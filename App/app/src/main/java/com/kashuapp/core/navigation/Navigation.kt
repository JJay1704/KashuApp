package com.kashuapp.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kashuapp.ui.account.list.AccountScreen
import com.kashuapp.ui.category.CategoryScreen
import com.kashuapp.ui.debt.DebtScreen
import com.kashuapp.ui.home.HomeScreen
import com.kashuapp.ui.login.LoginScreen
import com.kashuapp.ui.signUp.RegisterView
import com.kashuapp.ui.theme.KashuAppTheme

@Composable
fun Navigation() {

    KashuAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val navController = rememberNavController()

            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = "login"

            ) {
                composable("login") {

                    LoginScreen(onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }, onNavigateToRegister = {
                        navController.navigate("register") {
                            launchSingleTop = true
                        }
                    })
                }
                composable("home") {
                    HomeScreen(onNavigateToCategory = {
                        navController.navigate("category") {
                            launchSingleTop = true
                        }
                    }, onNavigateToDebt = {
                        navController.navigate("debt") {
                            launchSingleTop = true
                        }
                    }, onNavigateToAccount = {
                        navController.navigate("account") {
                            launchSingleTop = true
                        }
                    }, onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    })
                }
                composable("register") {
                    RegisterView(onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }, onNavigateToLogin = {
                        navController.popBackStack("login", inclusive = false)
                    })
                }

                composable("category") {
                    CategoryScreen(
                        onBackToHome = {
                            navController.popBackStack("home", inclusive = false)
                        })
                }
                composable("account") {
                    AccountScreen(
                        onBackToHome = {
                            navController.popBackStack("home", inclusive = false)
                        })
                }
                composable("debt") {
                    DebtScreen(
                        onBack = {
                            navController.popBackStack("home", inclusive = false)
                        })
                }

            }
        }
    }
}
