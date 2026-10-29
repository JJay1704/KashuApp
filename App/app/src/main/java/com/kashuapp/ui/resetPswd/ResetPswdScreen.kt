package com.kashuapp.ui.resetPswd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
            Text(
                text = "Restablece tu Contraseña",
                color = KashuTheme.colors.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(50.dp))

            when (uiState.step) {
                ResetSteps.EMAIL -> {
                    EmailReset { }

                }

                ResetSteps.CODE -> {
                    CodeReset { }

                }

                ResetSteps.NEW_PASSWORD -> {

                    PswdReset { }
                }

                ResetSteps.COMPLETE -> {
                    Text("Contraseña actualizada")
                }
            }


        }
    }
}