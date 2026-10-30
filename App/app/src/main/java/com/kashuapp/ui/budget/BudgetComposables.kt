package com.kashuapp.ui.budget

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuDropdownField
import com.kashuapp.core.composables.KashuTextField
import com.kashuapp.data.budget.Budget
import com.kashuapp.data.category.Category
import com.kashuapp.ui.theme.KashuTheme

@Composable
fun MonthSelector(
    month: Int,
    year: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val months = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    val monthName = if (month in 1..12) months[month - 1] else ""

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(KashuTheme.colors.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPrevious) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Mes anterior",
                    tint = KashuTheme.colors.title
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = monthName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = KashuTheme.colors.title
                )
                Text(
                    text = "$year",
                    fontSize = 13.sp,
                    color = KashuTheme.colors.subtitle
                )
            }

            IconButton(onClick = onNext) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Mes siguiente",
                    tint = KashuTheme.colors.title
                )
            }
        }
    }
}

@Composable
fun OverallBudgetSummaryCard(
    totalBudgeted: Double,
    totalSpent: Double,
    progress: Float,
    percentage: Int,
    currency: String = "PEN",
    modifier: Modifier = Modifier
) {
    val progressColor = when {
        percentage >= 100 -> Color(0xFFEF4444)
        percentage >= 80 -> Color(0xFFF59E0B)
        else -> KashuTheme.colors.mainColor
    }

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
                    text = "Presupuesto General",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KashuTheme.colors.title
                )
                Text(
                    text = "$percentage%",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Gastado",
                        fontSize = 12.sp,
                        color = KashuTheme.colors.subtitle
                    )
                    Text(
                        text = "$currency $totalSpent",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = KashuTheme.colors.title
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Límite Total",
                        fontSize = 12.sp,
                        color = KashuTheme.colors.subtitle
                    )
                    Text(
                        text = "$currency $totalBudgeted",
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
fun BudgetCard(
    budget: Budget,
    categoryName: String,
    spentAmount: Double,
    onClickCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rawFraction = if (budget.amountLimit > 0.0) {
        (spentAmount / budget.amountLimit).toFloat()
    } else {
        0.0f
    }
    val progressFraction = rawFraction.coerceIn(0.0f, 1.0f)
    val percentage = if (budget.amountLimit > 0.0) {
        ((spentAmount / budget.amountLimit) * 100).toInt()
    } else {
        0
    }
    val isExceeded = percentage >= 100
    val isWarning = percentage in 80..99
    val remainingAmount = (budget.amountLimit - spentAmount).coerceAtLeast(0.0)

    val progressColor = when {
        isExceeded -> Color(0xFFEF4444)
        isWarning -> Color(0xFFF59E0B)
        else -> KashuTheme.colors.mainColor
    }

    Card(
        onClick = onClickCard,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KashuTheme.colors.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(progressColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = categoryName,
                            tint = progressColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = categoryName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KashuTheme.colors.title
                        )
                        Text(
                            text = "${budget.currency} $spentAmount de ${budget.currency} ${budget.amountLimit}",
                            fontSize = 13.sp,
                            color = KashuTheme.colors.subtitle
                        )
                    }
                }

                if (isExceeded || isWarning) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alerta",
                            tint = progressColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isExceeded) "Excedido" else "80%+",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = progressColor
                        )
                    }
                } else {
                    Text(
                        text = "$percentage%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isExceeded) {
                        "Excedido por: ${budget.currency} ${spentAmount - budget.amountLimit}"
                    } else {
                        "Restante: ${budget.currency} $remainingAmount"
                    },
                    fontSize = 12.sp,
                    color = if (isExceeded) Color(0xFFEF4444) else KashuTheme.colors.subtitle
                )
                Text(
                    text = "$percentage% consumido",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = progressColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetFormDialog(
    state: BudgetState,
    onCategoryChange: (Category) -> Unit,
    onAmountChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (state.isEditing) "Editar Presupuesto" else "Nuevo Presupuesto",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = KashuTheme.colors.title
            )

            Spacer(modifier = Modifier.height(16.dp))

            KashuDropdownField(
                modifier = Modifier.fillMaxWidth(),
                label = "Categoría de Gasto",
                selectedValue = state.categoryName,
                placeholder = "Seleccionar categoría",
                icon = Icons.Default.Category,
                items = state.categories,
                itemLabel = { it.name },
                onItemSelected = { selectedCategory ->
                    onCategoryChange(selectedCategory)
                },
                hasError = state.isCategoryError != null,
                labelError = state.isCategoryError
            )

            Spacer(modifier = Modifier.height(12.dp))

            KashuTextField(
                label = "Límite mensual (${state.selectedCurrency})",
                placeholder = "0.00",
                type = state.amountLimit,
                onType = onAmountChange,
                iconInput = Icons.Default.AttachMoney,
                colorPlaceHolder = KashuTheme.colors.subtitle,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.isAmountError != null,
                errorMessage = state.isAmountError,
                trailingIcon = null
            )

            if (!state.errorMessage.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.errorMessage,
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            KashuButton(
                text = if (state.isEditing) "Actualizar Presupuesto" else "Guardar Presupuesto",
                isLoading = state.isLoading,
                onClickFun = onConfirm,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.isEditing) {
                Spacer(modifier = Modifier.height(14.dp))

                KashuButton(
                    modifier = Modifier.fillMaxWidth(),
                    backColor = Color(0xFFEF4444),
                    text = "Eliminar Presupuesto",
                    onClickFun = onDelete
                )
            }
        }
    }
}
