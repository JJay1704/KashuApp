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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.collectAsState
import com.kashuapp.core.composables.BalanceCard
import com.kashuapp.data.transaction.Transaction
import com.kashuapp.ui.transaction.TransactionFilters
import com.kashuapp.ui.transaction.TransactionList


@Composable
fun TransactionView(
    viewModel: TransactionVM = viewModel(),
    onNavigateToCategory: () -> Unit = {},

    ) {
    val uiState by viewModel.uiState.collectAsState()
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

        TransactionFilters (
            uiState = uiState,
            periods = viewModel.periods,
            transactionTypes = viewModel.transactionTypes,
            onAccountSelected = { viewModel.selectAccount(it) },
            onCategorySelected = { viewModel.selectCategory(it) },
            onTypeSelected = { viewModel.selectedType(it) },
            onPeriodSelected = { viewModel.selectPeriod(it) }
        )

        Spacer(modifier = Modifier.height(20.dp))


        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {


            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Recent Activity",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = KashuTheme.colors.title
                )

                if (uiState.selectedAccount != null) {
                    Box {
                        BalanceCard(
                            balance = viewModel.getBalance(uiState.selectedAccount?.id ?: "")
                        )
                    }
                }
            }
        }

            Spacer(modifier = Modifier.height(8.dp))
            val message = uiState.errorMessage
            if (message != null) {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (uiState.isLoading && uiState.transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = KashuTheme.colors.mainColor
                    )
                }
            } else if (
                uiState.transactions.isEmpty() &&
                uiState.errorMessage == null
            ) {
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
            } else if (uiState.transactions.isNotEmpty()) {
                TransactionList (
                    uiState = uiState,
                    onTransactionClick = {transaction -> selectedTransactionToEdit = transaction}
                )
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



