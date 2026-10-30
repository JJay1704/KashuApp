package com.kashuapp.ui.transaction.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.ui.theme.KashuTheme
import com.kashuapp.ui.transaction.form.AddTransact
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import com.kashuapp.core.composables.BalanceCard
import com.kashuapp.core.composables.KashuDropdownField
import com.kashuapp.data.transaction.Transaction
import com.kashuapp.ui.transaction.TransactionItemCard
import com.kashuapp.ui.transaction.KashuDatePickerField


@Composable
fun TransactionView(
    viewModel: TransactionVM = viewModel(),
    onNavigateToCategory: () -> Unit = {},

    ) {

    var showDialog by remember { mutableStateOf(false) }
    var selectedTransactionToEdit by remember { mutableStateOf<Transaction?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getTrans()

    }
    if (showDialog) {
        AddTransact(
            onDismiss = {
                showDialog = false
                viewModel.getTrans()
            }, onNavigateToCategory = onNavigateToCategory
        )
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(0.8f),
            ) {}
        }


        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {


            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                item {

                    KashuDropdownField(
                        fillMaxWidth = false,
                        selectedValue = viewModel.selectedAccount?.name ?: "",
                        placeholder = "Account",
                        icon = Icons.Default.AccountCircle,
                        items = viewModel.accounts,
                        itemLabel = { it.name },
                        onItemSelected = { selectedAccount ->
                            viewModel.selectAccount(
                                selectedAccount
                            )
                        },
                        isFilter = true,
                        onClearFilter = { viewModel.selectAccount(null) }
                    )


                }

                item {

                    KashuDropdownField(
                        fillMaxWidth = false,
                        selectedValue = viewModel.selectedCategory?.name ?: "",
                        placeholder = "Category",
                        icon = Icons.Default.Category,
                        items = viewModel.categories,
                        itemLabel = { it.name },
                        onItemSelected = { selectedCategory ->
                            viewModel.selectCategory(
                                selectedCategory
                            )
                        },
                        isFilter = true,
                        onClearFilter = { viewModel.selectCategory(null) }

                    )
                }
                item {


                    KashuDropdownField(
                        fillMaxWidth = false,
                        selectedValue = viewModel.selectedType ?: "",
                        placeholder = "Type",
                        icon = Icons.Default.AddCircleOutline,
                        items = viewModel.transactionTypes,
                        itemLabel = { if (it == "EXPENSE") "Gasto" else "Ingreso" },
                        onItemSelected = { type -> viewModel.selectedType(type) },
                        isFilter = true,
                        onClearFilter = { viewModel.selectedType(null) }
                    )
                }


                item {
                    KashuDropdownField(
                        fillMaxWidth = false,
                        selectedValue = viewModel.selectedPeriod ?: "",
                        placeholder = "Date",
                        icon = Icons.Default.CalendarToday,
                        items = viewModel.periods,
                        itemLabel = { it },
                        onItemSelected = { selectedPeriod -> viewModel.selectPeriod(selectedPeriod) },
                        isFilter = true,
                        onClearFilter = {
                            viewModel.selectPeriod(null)
                        }
                    )
                }

            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        if (viewModel.selectedPeriod == "Personalizado") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KashuDatePickerField(Modifier.weight(1f), "Desde", viewModel.startDate, viewModel::selectStartDate)
                KashuDatePickerField(Modifier.weight(1f), "Hasta", viewModel.endDate, viewModel::selectEndDate)
            }
        }
        viewModel.errorMessage?.let { Text(it, color = KashuTheme.colors.subtitle) }
        if (viewModel.isLoading) Text("Cargando movimientos…", color = KashuTheme.colors.subtitle)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Recent Activity",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = KashuTheme.colors.title
            )

            if (viewModel.selectedAccount != null) {
                Box {
                    BalanceCard(
                        balance = viewModel.getBalance(viewModel.selectedAccount?.id ?: "")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        if (viewModel.transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no tienes movimientos registrados",
                    color = KashuTheme.colors.subtitle,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(viewModel.transactions) { item ->
                    val itemCategory = viewModel.getCategoryName(item.categoryId)
                    val itemAccount = viewModel.getAccountName(item.accountId)
                    TransactionItemCard(
                        item = item,
                        categoryName = itemCategory,
                        accountName = itemAccount,
                        category = viewModel.categories.find { it.id == item.categoryId },
                        currency = viewModel.accounts.find { it.id == item.accountId }?.currency
                            ?: "", onClickCard = { selectedTransactionToEdit = item })
                }
            }
            if (selectedTransactionToEdit != null) {
                AddTransact(
                    item = selectedTransactionToEdit,
                    accountName = viewModel.getAccountName(selectedTransactionToEdit?.accountId),
                    categoryName = viewModel.getCategoryName(
                        selectedTransactionToEdit?.categoryId ?: ""
                    ),
                    onDismiss = {
                        selectedTransactionToEdit = null
                        viewModel.getTrans()
                    },
                    onNavigateToCategory = onNavigateToCategory
                )
            }
        }
    }
}



