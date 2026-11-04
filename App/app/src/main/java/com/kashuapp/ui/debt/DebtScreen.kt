package com.kashuapp.ui.debt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DebtScreen(onBack: () -> Unit, viewModel: DebtVM = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    DebtView(
        state = state,
        onBack = onBack,
        onRefresh = viewModel::loadData,
        onCreate = viewModel::openCreate,
        onEdit = viewModel::openEdit,
        onPay = viewModel::openPayment,
        onRequestDelete = viewModel::requestDelete,
        onFormChange = viewModel::changeForm,
        onSaveDebt = viewModel::saveDebt,
        onDismissForm = viewModel::dismissForm,
        onPaymentChange = viewModel::changePayment,
        onSavePayment = viewModel::pay,
        onDismissPayment = viewModel::dismissPayment,
        onConfirmDelete = viewModel::deleteDebt,
        onCancelDelete = viewModel::cancelDelete
    )
}
