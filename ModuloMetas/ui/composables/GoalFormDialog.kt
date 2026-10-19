package com.kashuapp.goals.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.kashuapp.core.composables.KashuDropdownField
import com.kashuapp.core.composables.KashuTextField
import com.kashuapp.data.account.Account
import com.kashuapp.data.category.Category
import com.kashuapp.goals.ui.GoalFormState
import com.kashuapp.ui.theme.KashuTheme
import com.kashuapp.ui.transaction.KashuDatePickerField

/**
 * Modal interactivo para la creación y edición de metas de ahorro (RF-07).
 * Soporta selección opcional de Cuenta y Categoría (o Alcancía Virtual si no se eligen).
 */
@Composable
fun GoalFormDialog(
    isEditing: Boolean,
    formState: GoalFormState,
    onTitleChange: (String) -> Unit,
    onTargetAmountChange: (String) -> Unit,
    onInitialAmountChange: (String) -> Unit,
    onDeadlineChange: (String) -> Unit,
    onIconChange: (String) -> Unit,
    onSelectAccount: (Account?) -> Unit,
    onSelectCategory: (Category?) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val iconsList = listOf(
        Pair("savings", Icons.Default.Savings),
        Pair("home", Icons.Default.Home),
        Pair("flight", Icons.Default.Flight),
        Pair("star", Icons.Default.Star)
    )

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
                    .verticalScroll(rememberScrollState())
            ) {
                // Cabecera del diálogo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "Editar Meta" else "Nueva Meta de Ahorro",
                        color = KashuTheme.colors.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = KashuTheme.colors.subtitle
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de Ícono
                Text(
                    text = "Ícono de la meta",
                    color = KashuTheme.colors.subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    iconsList.forEach { (key, vector) ->
                        val isSelected = formState.icon == key
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) KashuTheme.colors.mainColor.copy(alpha = 0.25f)
                                    else KashuTheme.colors.mainBackground
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) KashuTheme.colors.mainColor else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { onIconChange(key) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = vector,
                                contentDescription = key,
                                tint = if (isSelected) KashuTheme.colors.mainColor else KashuTheme.colors.subtitle,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                KashuTextField(
                    label = "Nombre de la meta",
                    placeholder = "ej. Viaje a Japón",
                    type = formState.title,
                    onType = onTitleChange,
                    colorPlaceHolder = KashuTheme.colors.subtitle,
                    iconInput = Icons.Default.Flag,
                    isError = formState.titleError != null,
                    errorMessage = formState.titleError,
                    trailingIcon = null
                )

                Spacer(modifier = Modifier.height(10.dp))

                KashuTextField(
                    label = "Monto Objetivo",
                    placeholder = "0.00",
                    type = formState.targetAmount,
                    onType = onTargetAmountChange,
                    colorPlaceHolder = KashuTheme.colors.subtitle,
                    iconInput = Icons.Default.AttachMoney,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = formState.targetAmountError != null,
                    errorMessage = formState.targetAmountError,
                    trailingIcon = null
                )

                Spacer(modifier = Modifier.height(10.dp))

                KashuTextField(
                    label = "Ahorro actual acumulado (opcional)",
                    placeholder = "0.00",
                    type = formState.initialAmount,
                    onType = onInitialAmountChange,
                    colorPlaceHolder = KashuTheme.colors.subtitle,
                    iconInput = Icons.Default.Savings,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    trailingIcon = null
                )

                Spacer(modifier = Modifier.height(10.dp))

                KashuDatePickerField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Fecha límite",
                    selectedDate = formState.deadline.ifEmpty { "Seleccionar fecha límite" },
                    onDateSelected = onDeadlineChange
                )

                if (formState.deadlineError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formState.deadlineError,
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selector Opcional: Cuenta Asociada
                KashuDropdownField(
                    label = "Cuenta asociada (Opcional)",
                    selectedValue = formState.selectedAccountName ?: "Ninguna (Alcancía en efectivo)",
                    placeholder = "Seleccionar cuenta",
                    icon = Icons.Default.AccountBalance,
                    items = listOf<Account?>(null) + formState.availableAccounts,
                    itemLabel = { acc -> acc?.let { "${it.name} (${it.currency})" } ?: "Ninguna (Alcancía en efectivo)" },
                    onItemSelected = { acc -> onSelectAccount(acc) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Selector Opcional: Categoría
                KashuDropdownField(
                    label = "Categoría temática (Opcional)",
                    selectedValue = formState.selectedCategoryName ?: "Ninguna (Ahorro General)",
                    placeholder = "Seleccionar categoría",
                    icon = Icons.Default.Category,
                    items = listOf<Category?>(null) + formState.availableCategories,
                    itemLabel = { cat -> cat?.name ?: "Ninguna (Ahorro General)" },
                    onItemSelected = { cat -> onSelectCategory(cat) }
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Botón Guardar utilizando el componente oficial del proyecto
                KashuButton(
                    text = if (isEditing) "Actualizar Meta" else "Crear Meta",
                    isLoading = formState.isSubmitting,
                    onClickFun = onSave,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
