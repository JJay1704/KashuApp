package com.kashuapp.ui.resetPswd

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuTextField
import com.kashuapp.ui.theme.KashuTheme

@Composable
fun EmailReset(
    uiState: ResetPswdState,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBackToHome: () -> Unit
) {
    KashuTextField(
        placeholder = "Ingresa tu Correo Electronico",
        colorPlaceHolder = Color.Gray,
        type = uiState.email,
        onType = { email ->
            onEmailChange(email)
        },
        errorMessage = uiState.isError,
        isError = uiState.isError != null
    )

    Spacer(modifier = Modifier.height(10.dp))

    KashuButton(
        isLoading = uiState.isLoading,
        enabled = !uiState.isLoading,
        text = "Restablecer",
        onClickFun = {
            onSubmit()
        }
    )

    Spacer(modifier = Modifier.height(20.dp))

    KashuButton(
        backColor = KashuTheme.colors.surface,
        text = "Cancelar",
        onClickFun = {
            onBackToHome()
        }
    )

    Spacer(modifier = Modifier.height(10.dp))

    val successMessage = uiState.successMessage

    if (!successMessage.isNullOrBlank()) {
        Text(
            text = successMessage,
            color = KashuTheme.colors.title
        )
    }
}

@Composable
fun CodeReset(
    uiState: ResetPswdState,
    onCodeChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Text(
        text = "Escribe el código enviado a ${uiState.email}",
        color = KashuTheme.colors.title
    )

    Spacer(modifier = Modifier.height(20.dp))

    KashuTextField(
        placeholder = "Codigo de Verificacion",
        colorPlaceHolder = Color.Gray,
        type = uiState.code,
        onType = { code ->
            onCodeChange(code)
        },
        errorMessage = uiState.isError,
        isError = uiState.isError != null
    )

    KashuButton(
        isLoading = uiState.isLoading,
        enabled = !uiState.isLoading,
        text = "Restablecer",
        onClickFun = {
            onSubmit()
        }
    )
}

@Composable
fun PswdReset(
    uiState: ResetPswdState,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onSubmit: () -> Unit
) {

    Text(
        text = "Escriba nueva contraseña", color = KashuTheme.colors.title
    )


    Spacer(modifier = Modifier.height(20.dp))


    KashuTextField(
        label = "Ingrese contraseña",
        colorPlaceHolder = Color.Gray,
        type = uiState.newPswd,
        onType = { onPasswordChange(it) },
        errorMessage = uiState.isError,
        isError = uiState.isError != null,
        visualTransformation = if (uiState.isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),


        trailingIcon = {
            IconButton(
                onClick = { onTogglePasswordVisibility() }) {
                Icon(
                    imageVector = if (uiState.isPasswordVisible) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    }, contentDescription = if (uiState.isPasswordVisible) {
                        "Ocultar contraseña"
                    } else {
                        "Mostrar contraseña"
                    }, tint = KashuTheme.colors.subtitle
                )
            }
        },
    )
    KashuTextField(
        visualTransformation = if (uiState.isConfirmPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        trailingIcon = {
            IconButton(onClick = { onToggleConfirmPasswordVisibility() }) {
                Icon(
                    imageVector = if (uiState.isConfirmPasswordVisible) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    }, contentDescription = if (uiState.isConfirmPasswordVisible) {
                        "Ocultar confirmación"
                    } else {
                        "Mostrar confirmación"
                    }, tint = KashuTheme.colors.subtitle
                )
            }
        },
        placeholder = "Confirme la Contraseña",
        colorPlaceHolder = Color.Gray,
        type = uiState.newPswdConfirm,
        onType = { onConfirmPasswordChange(it) },
        errorMessage = uiState.isError,
        isError = uiState.isError != null
    )
    KashuButton(
        isLoading = uiState.isLoading,
        enabled = !uiState.isLoading,
        text = "Actualizar Contraseña",
        onClickFun = {
            onSubmit()
        })

}

@Composable
fun CompletedPswd(
    onCompleted: () -> Unit
) {
    Text("Contraseña actualizada")
    Spacer(modifier = Modifier.height(20.dp))

    KashuButton(
        text = "Continuar ", onClickFun = {
            onCompleted()
        })


}