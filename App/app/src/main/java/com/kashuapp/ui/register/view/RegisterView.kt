package com.kashuapp.ui.register.view


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuLogo
import com.kashuapp.ui.login.KashuTextField
import com.kashuapp.ui.login.Subtitle
import com.kashuapp.ui.register.viewModel.RegisterViewModel
import com.kashuapp.ui.theme.KashuTheme
@Composable
fun RegisterView(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = remember { RegisterViewModel() }
) {
    // 1. Observamos el estado del RegisterViewModel
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KashuTheme.colors.mainBackground)
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()), // Por si el teclado tapa los campos
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        // 2. Logo
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = KashuTheme.colors.surface,
            border = BorderStroke(1.dp, color = KashuTheme.colors.surface),
            shadowElevation = 1.dp
        ) {
            KashuLogo(fontsize = 40)
        }
        Spacer(modifier = Modifier.height(16.dp))
        // 3. Título y Subtítulo
        Text(
            text = "Create an Account",
            color = KashuTheme.colors.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Subtitle("Sign up to get started", KashuTheme.colors.subtitle)
        Spacer(modifier = Modifier.height(24.dp))


        KashuTextField(
            label = "Name",
            type = uiState.name,
            onType = { viewModel.onName(it) },
            placeholder = "Juan Manuel",
            iconInput = Icons.Default.VerifiedUser,
            colorPlaceHolder = Color.Gray,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            trailingIcon = null
        )
        Spacer(modifier = Modifier.height(14.dp))

        KashuTextField(
            label = "Last Name",
            type = uiState.lastName,
            onType = { viewModel.onLastName(it) },
            placeholder = "Perez Alvarez",
            iconInput = Icons.Default.VerifiedUser,
            colorPlaceHolder = Color.Gray,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            trailingIcon = null
        )
        Spacer(modifier = Modifier.height(14.dp))

        KashuTextField(
            label = "Email",
            type = uiState.email,
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
            type = uiState.password,
            onType = { viewModel.onPassword(it) },
            placeholder = "At least 6 characters",
            iconInput = Icons.Default.Lock,
            colorPlaceHolder = Color.Gray,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { viewModel.onTogglePassword() }) {
                    Icon(
                        imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password",
                        tint = Color.Gray
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(14.dp))

        KashuTextField(
            label = "Confirm Password",
            type = uiState.confirmPassword,
            onType = { viewModel.onConfirmPassword(it) },
            placeholder = "Repeat your password",
            iconInput = Icons.Default.Lock,
            colorPlaceHolder = Color.Gray,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (uiState.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { viewModel.onToggleConfirmPassword() }) {
                    Icon(
                        imageVector = if (uiState.isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle confirm password",
                        tint = Color.Gray
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (uiState.errorMessage.isNotBlank()) {
            Text(
                text = uiState.errorMessage,
                color = Color.Red,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))

        KashuButton(
            onClickFun = {
                viewModel.register(onSuccess = onNavigateToHome)
            },
            modifier = Modifier.fillMaxWidth(),
            text = if (uiState.isLoading) "Creating Account..." else "Sign Up ->"
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
            Text(
                text = "Log In",
                color = KashuTheme.colors.mainColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}