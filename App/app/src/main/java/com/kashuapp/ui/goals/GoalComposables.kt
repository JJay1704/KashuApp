package com.kashuapp.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuTextField
import com.kashuapp.data.goal.Goal
import com.kashuapp.ui.goals.list.GoalsState
import com.kashuapp.ui.theme.KashuTheme

@Composable
fun GoalGlobalProgress(
    modifier: Modifier = Modifier,
    uiState: GoalsState = GoalsState()
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(KashuTheme.colors.surface)
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso Global de Ahorro",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KashuTheme.colors.title
                )
                Text(
                    text = "${uiState.overallProgressPercentage}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = KashuTheme.colors.mainColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { uiState.overallProgressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = KashuTheme.colors.mainColor,
                trackColor = KashuTheme.colors.mainColor.copy(alpha = 0.2f),
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Acumulado",
                        fontSize = 12.sp,
                        color = KashuTheme.colors.subtitle
                    )
                    Text(
                        text = "${uiState.totalSavedAmount}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = KashuTheme.colors.title
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Objetivo Total",
                        fontSize = 12.sp,
                        color = KashuTheme.colors.subtitle
                    )
                    Text(
                        text = "${uiState.totalTargetAmount}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = KashuTheme.colors.title
                    )
                }
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    onAddSavingsClick: () -> Unit,
    onClickCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTargetReached = goal.currentAmount >= goal.targetAmount
    val rawFraction = if (goal.targetAmount > 0.0) {
        (goal.currentAmount / goal.targetAmount).toFloat()
    } else {
        0.0f
    }
    val progressFraction = if (rawFraction > 1.0f) 1.0f else if (rawFraction < 0.0f) 0.0f else rawFraction
    val progressPercentage = (progressFraction * 100).toInt()

    Card(
        onClick = onClickCard,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KashuTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(KashuTheme.colors.mainColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = goal.title,
                            tint = KashuTheme.colors.mainColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = goal.title,
                            color = KashuTheme.colors.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Fecha límite: ${goal.deadline}",
                            color = KashuTheme.colors.subtitle,
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = "$progressPercentage%",
                    color = KashuTheme.colors.mainColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${goal.currency} ${goal.currentAmount}",
                    color = KashuTheme.colors.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "/ ${goal.currency} ${goal.targetAmount}",
                    color = KashuTheme.colors.subtitle,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = KashuTheme.colors.mainColor,
                trackColor = KashuTheme.colors.mainColor.copy(alpha = 0.15f),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(KashuTheme.colors.mainColor.copy(alpha = 0.12f))
                    .clickable { onAddSavingsClick() }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = KashuTheme.colors.mainColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isTargetReached) "Seguir abonando" else "Abonar a la meta",
                        color = KashuTheme.colors.mainColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSavingsDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onConfirmSavings: (amountStr: String) -> Unit
) {
    var depositInput by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = KashuTheme.colors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Abonar a: ${goal.title}",
                color = KashuTheme.colors.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Actual: ${goal.currency} ${goal.currentAmount} / Meta: ${goal.currency} ${goal.targetAmount}",
                color = KashuTheme.colors.subtitle,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            KashuButton(
                text = "Confirmar Abono",
                onClickFun = { onConfirmSavings(depositInput) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
