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

    viewModel: ResetPswdVM = viewModel(),
    onBackToHome: () -> Unit,

    ) {

    val uiState by viewModel.uiState.collectAsState()


    KashuTextField(
        placeholder = "Ingresa tu Correo Electronico",
        colorPlaceHolder = Color.Gray,
        type = uiState.email,
        onType = { viewModel.onEmail(it) },
        errorMessage = uiState.isError,
        isError = uiState.isError != null
    )

    Spacer(modifier = Modifier.height(10.dp))

    KashuButton(
        isLoading = uiState.isLoading,
        enabled = !uiState.isLoading,
        text = "Restablecer",
        onClickFun = {
            viewModel.reset(onSuccess = {})
        })
    Spacer(modifier = Modifier.height(20.dp))

    KashuButton(
        backColor = KashuTheme.colors.surface, text = "Cancelar",
        onClickFun = {
            onBackToHome()
            viewModel.onEmail("")

        },
    )
    Spacer(modifier = Modifier.height(10.dp))
    if (!uiState.successMessage.isNullOrBlank()) {

        Text(
            text = uiState.successMessage, color = KashuTheme.colors.title
        )
    }
}


@Composable
fun CodeReset(
    viewModel: ResetPswdVM = viewModel(),
    onBackToHome: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsState()
    Text(
        text = "Escribe el código enviado a ${uiState.email}", color = KashuTheme.colors.title
    )


    Spacer(modifier = Modifier.height(20.dp))


    KashuTextField(
        placeholder = "Codigo de Verificacion",
        colorPlaceHolder = Color.Gray,
        type = uiState.code,
        onType = { viewModel.onCode(it) },
        errorMessage = uiState.isError,
        isError = uiState.isError != null
    )
    KashuButton(
        isLoading = uiState.isLoading,
        enabled = !uiState.isLoading,
        text = "Restablecer",
        onClickFun = {
            viewModel.confirmToken()
        })


}


@Composable
fun PswdReset(
    viewModel: ResetPswdVM = viewModel(),
    onBackToHome: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsState()
    Text(
        text = "Escriba nueva contraseña", color = KashuTheme.colors.title
    )


    Spacer(modifier = Modifier.height(20.dp))


    KashuTextField(
        label = "Ingrese contraseña",
        colorPlaceHolder = Color.Gray,
        type = uiState.newPswd,
        onType = { viewModel.onNewPswd(it) },
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
                onClick = { viewModel.togglePasswordVisibility() }) {
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
            IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
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
        onType = { viewModel.onConfirmPswd(it) },
        errorMessage = uiState.isError,
        isError = uiState.isError != null
    )
    KashuButton(
        isLoading = uiState.isLoading,
        enabled = !uiState.isLoading,
        text = "Actualizar Contraseña",
        onClickFun = {
            viewModel.updatePswd()
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