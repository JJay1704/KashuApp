package com.kashuapp.ui.goals.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuDropdownField
import com.kashuapp.core.composables.KashuTextField
import com.kashuapp.data.goal.Goal
import com.kashuapp.ui.theme.KashuTheme
import com.kashuapp.ui.transaction.KashuDatePickerField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalFormDialog(
    goalToEdit: Goal? = null,
    onDismiss: () -> Unit,
    viewModel: GoalFormVM = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(goalToEdit) {
        viewModel.initForm(goalToEdit)
    }

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
                text = if (uiState.isEditing) "Editar Meta" else "Nueva Meta de Ahorro",
                color = KashuTheme.colors.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            KashuTextField(
                label = "Nombre de la meta",
                placeholder = "ej. Viaje a Japón",
                type = uiState.title,
                onType = { viewModel.onTitle(it) },
                colorPlaceHolder = KashuTheme.colors.subtitle,
                iconInput = Icons.Default.Flag,
                isError = uiState.isTitleError != null,
                errorMessage = uiState.isTitleError,
                trailingIcon = null
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (!uiState.isEditing) KashuDropdownField(label = "Moneda", selectedValue = uiState.currency,
                items = uiState.currencies, itemLabel = { it }, onItemSelected = viewModel::onCurrency)

            KashuTextField(
                label = "Monto Objetivo",
                placeholder = "0.00",
                type = uiState.targetAmount,
                onType = { viewModel.onTargetAmount(it) },
                colorPlaceHolder = KashuTheme.colors.subtitle,
                iconInput = Icons.Default.AttachMoney,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = uiState.isTargetAmountError != null,
                errorMessage = uiState.isTargetAmountError,
                trailingIcon = null
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (!uiState.isEditing) KashuTextField(
                label = "Ahorro previo (ya reservado, opcional)",
                placeholder = "0.00",
                type = uiState.initialAmount,
                onType = { viewModel.onInitialAmount(it) },
                colorPlaceHolder = KashuTheme.colors.subtitle,
                iconInput = Icons.Default.Savings,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = null
            )

            Spacer(modifier = Modifier.height(10.dp))

            KashuDatePickerField(
                modifier = Modifier.fillMaxWidth(),
                label = "Fecha límite",
                selectedDate = uiState.deadline.ifEmpty { "Seleccionar fecha" },
                onDateSelected = { viewModel.onDeadline(it) }
            )

            if (uiState.isDeadlineError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.isDeadlineError ?: "",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            KashuDropdownField(
                modifier = Modifier.fillMaxWidth(),
                label = "Cuenta (Opcional)",
                selectedValue = uiState.accountName,
                placeholder = "Seleccionar cuenta",
                icon = Icons.Default.AccountCircle,
                items = uiState.accounts.filter { it.currency == uiState.currency },
                itemLabel = { it.name },
                onItemSelected = { selectedAccount ->
                    viewModel.onAccount(selectedAccount)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            KashuDropdownField(
                modifier = Modifier.fillMaxWidth(),
                label = "Categoría (Opcional)",
                selectedValue = uiState.categoryName,
                placeholder = "Seleccionar categoría",
                icon = Icons.Default.Category,
                items = uiState.categories,
                itemLabel = { it.name },
                onItemSelected = { selectedCategory ->
                    viewModel.onCategory(selectedCategory)
                }
            )

            if (!uiState.errorMessage.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage ?: "",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            KashuButton(
                modifier = Modifier.fillMaxWidth(),
                text = if (uiState.isEditing) "Actualizar Meta" else "Guardar Meta",
                isLoading = uiState.isLoading,
                onClickFun = {
                    viewModel.saveGoal(
                        goalToEdit = goalToEdit,
                        onSuccess = {
                            onDismiss()
                        }
                    )
                }
            )

            if (uiState.isEditing) {
                Spacer(modifier = Modifier.height(14.dp))

                KashuButton(
                    modifier = Modifier.fillMaxWidth(),
                    backColor = Color(0xFFEF4444),
                    text = "Eliminar Meta",
                    onClickFun = {
                        viewModel.deleteGoal(
                            goalId = goalToEdit?.id,
                            onSuccess = {
                                onDismiss()
                            }
                        )
                    }
                )
            }
        }
    }
}
