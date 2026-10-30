package com.kashuapp.ui.resetPswd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuTextField
import com.kashuapp.ui.theme.KashuTheme

@Composable
fun ResetPswdScreen(
    onBackToHome: () -> Unit,
    viewModel: ResetPswdVM = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(KashuTheme.colors.mainBackground)
                .clip(RoundedCornerShape(24.dp))
                .background(KashuTheme.colors.mainBackground)
                .padding(24.dp), verticalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,

                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.offset(x = (-12).dp),
                    onClick = {
                        if (uiState.step == ResetSteps.EMAIL) {
                            viewModel.resetFlow()
                            onBackToHome()
                        } else {
                            viewModel.goBack()
                        }
                    }) {
                    Icon(

                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = KashuTheme.colors.title
                    )
                }
                Text(
                    text = "Restablece tu Contraseña",
                    color = KashuTheme.colors.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }



            Spacer(modifier = Modifier.height(50.dp))

            when (uiState.step) {
                ResetSteps.EMAIL -> {
                    EmailReset(
                        uiState = uiState,
                        onEmailChange = { email ->
                            viewModel.onEmail(email)
                        },
                        onSubmit = {
                            viewModel.reset(onSuccess = {})
                        },
                        onBackToHome = {
                            viewModel.resetFlow()
                            onBackToHome()
                        }
                    )

                }

                ResetSteps.CODE -> {
                    CodeReset(
                        uiState = uiState,
                        onCodeChange = { code ->
                            viewModel.onCode(code)
                        },
                        onSubmit = {
                            viewModel.confirmToken()
                        }
                    )
                }

                ResetSteps.NEW_PASSWORD -> {
                    PswdReset(
                        uiState = uiState,
                        onPasswordChange = {
                            viewModel.onNewPswd(it)
                        },
                        onConfirmPasswordChange = {
                            viewModel.onConfirmPswd(it)
                        },
                        onTogglePasswordVisibility = {
                            viewModel.togglePasswordVisibility()
                        },
                        onToggleConfirmPasswordVisibility = {
                            viewModel.toggleConfirmPasswordVisibility()
                        },
                        onSubmit = {
                            viewModel.updatePswd()
                        }
                    )
                }

                ResetSteps.COMPLETE -> {
                    CompletedPswd(
                        onCompleted = {
                            viewModel.resetFlow()
                            onBackToHome()
                        }
                    )
                }
            }


        }
    }
}