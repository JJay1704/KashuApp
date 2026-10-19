package com.kashuapp.goals.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuTextField
import com.kashuapp.goals.data.Goal
import com.kashuapp.ui.theme.KashuTheme
import java.text.NumberFormat
import java.util.Locale

/**
 * Diálogo interactivo para abonar dinero a una meta de ahorro específica.
 * Calcula y previsualiza en vivo el impacto del abono en el progreso y monto faltante (RF-07.1).
 */
@Composable
fun AddSavingsDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onConfirmSavings: (amountStr: String) -> Unit
) {
    var depositInput by remember { mutableStateOf("") }
    val depositDouble = depositInput.toDoubleOrNull() ?: 0.0

    val newCurrentAmount = goal.currentAmount + depositDouble
    val newProgressFraction = if (goal.targetAmount > 0.0) {
        (newCurrentAmount / goal.targetAmount).toFloat().coerceIn(0.0f, 1.0f)
    } else {
        0.0f
    }
    val newPercentage = (newProgressFraction * 100).toInt()
    val newRemaining = maxOf(0.0, goal.targetAmount - newCurrentAmount)

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "PE")).apply {
            maximumFractionDigits = 2
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = KashuTheme.colors.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Encabezado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = KashuTheme.colors.mainColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Abonar Ahorro",
                            color = KashuTheme.colors.title,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = KashuTheme.colors.subtitle
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Meta: ${goal.title}",
                    color = KashuTheme.colors.subtitle,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Input de Monto a Abonar
                KashuTextField(
                    label = "Monto a abonar",
                    placeholder = "0.00",
                    type = depositInput,
                    onType = { depositInput = it },
                    colorPlaceHolder = KashuTheme.colors.subtitle,
                    iconInput = Icons.Default.AttachMoney,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    trailingIcon = null
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Panel de Previsualización Interactiva (Cálculo en Vivo)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(KashuTheme.colors.mainBackground)
                        .padding(14.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Impacto del Abono en Tiempo Real",
                            color = KashuTheme.colors.subtitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Nuevo saldo:",
                                color = KashuTheme.colors.subtitle,
                                fontSize = 13.sp
                            )
                            Text(
                                text = currencyFormatter.format(newCurrentAmount),
                                color = KashuTheme.colors.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Monto restante:",
                                color = KashuTheme.colors.subtitle,
                                fontSize = 13.sp
                            )
                            Text(
                                text = currencyFormatter.format(newRemaining),
                                color = KashuTheme.colors.mainColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { newProgressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = KashuTheme.colors.mainColor,
                            trackColor = KashuTheme.colors.mainColor.copy(alpha = 0.2f),
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Nuevo avance: $newPercentage%",
                            color = KashuTheme.colors.subtitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botón de Confirmación utilizando el componente oficial del proyecto
                KashuButton(
                    text = "Confirmar Abono",
                    onClickFun = { onConfirmSavings(depositInput) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
