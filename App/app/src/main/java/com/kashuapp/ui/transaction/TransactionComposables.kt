package com.kashuapp.ui.transaction

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashuapp.core.composables.KashuDropdownField
import com.kashuapp.data.account.Account
import com.kashuapp.data.category.Category
import com.kashuapp.data.transaction.Transaction
import com.kashuapp.ui.theme.KashuTheme
import com.kashuapp.ui.transaction.list.TransactionState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.lazy.items

@Composable
fun TransactionFilters(
    uiState: TransactionState,
    periods: List<String>,
    transactionTypes: List<String>,
    onAccountSelected: (Account?) -> Unit,
    onCategorySelected: (Category?) -> Unit,
    onTypeSelected: (String?) -> Unit,
    onPeriodSelected: (String?) -> Unit
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()
    ) {

        item {

            KashuDropdownField(
                fillMaxWidth = false,
                selectedValue = uiState.selectedAccount?.name ?: "",
                placeholder = "Account",
                icon = Icons.Default.AccountCircle,
                items = uiState.accounts,
                itemLabel = { it.name },
                onItemSelected = { selectedAccount ->
                    onAccountSelected(
                        selectedAccount
                    )
                },
                isFilter = true,
                onClearFilter = { onAccountSelected(null) })


        }

        item {

            KashuDropdownField(
                fillMaxWidth = false,
                selectedValue = uiState.selectedCategory?.name ?: "",
                placeholder = "Category",
                icon = Icons.Default.Category,
                items = uiState.categories,
                itemLabel = { it.name },
                onItemSelected = { selectedCategory ->
                    onCategorySelected(
                        selectedCategory
                    )
                },
                isFilter = true,
                onClearFilter = { onCategorySelected(null) }

            )
        }
        item {


            KashuDropdownField(
                fillMaxWidth = false,
                selectedValue = uiState.selectedType ?: "",
                placeholder = "Type",
                icon = Icons.Default.AddCircleOutline,
                items = transactionTypes,
                itemLabel = { if (it == "EXPENSE") "Gasto" else "Ingreso" },
                onItemSelected = { type -> onTypeSelected(type) },
                isFilter = true,
                onClearFilter = { onTypeSelected(null) })
        }


        item {
            KashuDropdownField(
                fillMaxWidth = false,
                selectedValue = uiState.selectedPeriod ?: "",
                placeholder = "Date",
                icon = Icons.Default.CalendarToday,
                items = periods,
                itemLabel = { it },
                onItemSelected = { selectedPeriod -> onPeriodSelected(selectedPeriod) },
                isFilter = true,
                onClearFilter = {
                    onPeriodSelected(null)
                })
        }

    }
}

@Composable
fun TransactionList(
    uiState: TransactionState,
    onTransactionClick: (Transaction) -> Unit
) {
    LazyColumn (
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(uiState.transactions) { transaction ->
            val categoryName = uiState.categories
                .find { it.id == transaction.categoryId }
                ?.name ?: "Sin categoría"

            val account = uiState.accounts
                .find { it.id == transaction.accountId }

            TransactionItemCard(
                item = transaction,
                categoryName = categoryName,
                accountName = account?.name ?: "Sin cuenta",
                currency = account?.currency ?: "",
                onClickCard = {
                    onTransactionClick(transaction)
                }
            )
        }
    }
}
@Composable
fun KashuDatePickerField(
    modifier: Modifier = Modifier,
    label: String = "Fecha",
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                onDateSelected(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
    Column(modifier = modifier) {
        if (label.isNotBlank()) {
            Text(
                text = label, fontSize = 14.sp, color = KashuTheme.colors.title
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = KashuTheme.colors.title,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledContainerColor = Color.Transparent,
                    disabledLeadingIconColor = KashuTheme.colors.mainColor
                ),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = label,
                        tint = KashuTheme.colors.mainColor
                    )
                })
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { datePickerDialog.show() })
        }
    }
}


@Composable
fun KashuTimePickerField(
    modifier: Modifier = Modifier,
    label: String = "Hora",
    selectedTime: String,
    is24Hour: Boolean = true,
    onTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    val timePickerDialog = remember {
        TimePickerDialog(
            context, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                onTimeSelected(timeFormat.format(calendar.time))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24Hour
        )
    }

    Column(modifier = modifier) {
        if (label.isNotBlank()) {
            Text(
                text = label, fontSize = 14.sp, color = KashuTheme.colors.title
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedTime,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = KashuTheme.colors.title,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledContainerColor = Color.Transparent,
                    disabledLeadingIconColor = KashuTheme.colors.mainColor
                ),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = label,
                        tint = KashuTheme.colors.mainColor
                    )
                })
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { timePickerDialog.show() })
        }
    }
}


@Composable
fun TransactionItemCard(
    item: Transaction,

    categoryName: String,
    accountName: String,
    currency: String = "PEN",
    onClickCard: () -> Unit = {},
    category: Category? = null,

    ) {
    val isIncome = item.type.equals("INCOME", ignoreCase = true)
    val amountColor = if (isIncome) Color(0xFF10B981) else Color(0xFFEF4444)
    val amountPrefix = if (isIncome) "+ $currency" else "- $currency"

    val iconVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward


    Card(
        onClick = onClickCard,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = KashuTheme.colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(amountColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = item.type,
                        tint = amountColor,
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
                    if (!item.description.isNullOrBlank()) {
                        Text(
                            text = item.description.orEmpty(),
                            fontSize = 12.sp,
                            color = KashuTheme.colors.subtitle,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = accountName,
                        fontSize = 12.sp,
                        color = KashuTheme.colors.subtitle,
                        maxLines = 1
                    )
                }
            }

            Text(
                text = "$amountPrefix ${
                    String.format(
                        Locale.US, "%.2f", item.amount
                    )
                }", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = amountColor
            )
        }
    }
}
