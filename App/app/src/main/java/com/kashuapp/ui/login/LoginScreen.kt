package com.kashuapp.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuLogo
import com.kashuapp.core.composables.KashuTextField
import com.kashuapp.ui.theme.KashuTheme

@Composable
fun LoginScreen(

    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginVM = viewModel(),


    ) {

    val uiStateValues by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {




        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KashuTheme.colors.mainBackground)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(0.2f))

            Surface(
                modifier = Modifier.padding(top = 40.dp),

                shape = RoundedCornerShape(4.dp),
                color = KashuTheme.colors.surface,
                border = BorderStroke(
                    width = 1.dp, color = KashuTheme.colors.surface
                ),
                shadowElevation = 1.dp
            ) {
                KashuLogo(fontsize = 40)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Welcome to Kashu",
                color = KashuTheme.colors.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Subtitle("Log in to continue", KashuTheme.colors.subtitle)
            Spacer(modifier = Modifier.weight(0.9f))
            KashuTextField(
                label = "Email",
                type = uiStateValues.email,

                onType = { viewModel.onEmail(it) },
                placeholder = "user@email.com",
                iconInput = Icons.Default.Email,
                colorPlaceHolder = Color.Gray,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                trailingIcon = null


            )
            Spacer(modifier = Modifier.height(14.dp))
            KashuTextField(
                label = "Password",
                extraLabel = "Forgot your password?",
                type = uiStateValues.password,
                onType = { viewModel.onPassword(it) },
                placeholder = "••••••••",
                iconInput = Icons.Default.Lock,
                colorPlaceHolder = Color.Gray,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (uiStateValues.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),


                trailingIcon = {
                    IconButton(onClick = { viewModel.onTogglePassword() }) {
                        Icon(
                            imageVector = if (uiStateValues.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (uiStateValues.isPasswordVisible) "Hide password" else "Show password",
                            tint = Color.Gray
                        )
                    }
                })
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                KashuButton(


                    onClickFun = {
                        viewModel.loginSuccess(
                            onSucces = { onNavigateToHome() })
                    },
                    modifier = Modifier.weight(0.8f),
                    text = "Log in ->",
                    isLoading = uiStateValues.isLoading,
                    enabled = !uiStateValues.isLoading


                )
                Spacer(modifier = Modifier.width(4.dp))
                OutlinedButton(
                    onClick = { /* Acción huella digital */ },
                    modifier = Modifier
                        .weight(0.2f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = KashuTheme.colors.surface,
                        contentColor = KashuTheme.colors.mainColor
                    ),
                    border = BorderStroke(
                        width = 2.dp, color = MaterialTheme.colorScheme.outline
                    ),

                    ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometric Login",
                        tint = KashuTheme.colors.title
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {

                if (uiStateValues.errorMessage.isNotEmpty()) {
                    Text(
                        text = uiStateValues.errorMessage,
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                KashuSocialButton(
                    onClickFun = { })
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't Have an Account? ",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
                Text(
                    modifier = Modifier.clickable { onNavigateToRegister() },
                    text = "Sign Up",
                    color = KashuTheme.colors.mainColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,

                    )
            }
            Spacer(modifier = Modifier.weight(0.2f))






        }

        if (uiStateValues.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = KashuTheme.colors.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = KashuTheme.colors.mainColor, strokeWidth = 3.5.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Checking credentials...",
                            color = KashuTheme.colors.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

}