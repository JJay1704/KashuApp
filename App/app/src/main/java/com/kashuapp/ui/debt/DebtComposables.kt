package com.kashuapp.ui.debt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kashuapp.ui.theme.KashuTheme
import com.kashuapp.data.debt.Debt
import java.util.Locale

@Composable
fun DebtView(
    state: DebtState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onCreate: () -> Unit,
    onEdit: (Debt) -> Unit,
    onPay: (Debt) -> Unit,
    onRequestDelete: (Debt) -> Unit,
    onFormChange: (DebtFormState) -> Unit,
    onSaveDebt: () -> Unit,
    onDismissForm: () -> Unit,
    onPaymentChange: (DebtPaymentState) -> Unit,
    onSavePayment: () -> Unit,
    onDismissPayment: () -> Unit,
    onConfirmDelete: () -> Unit,
    onCancelDelete: () -> Unit
) {
    DebtContent(state, onBack, onRefresh, onCreate, onEdit, onPay, onRequestDelete)
    state.form?.let { form ->
        DebtForm(form, state.currencies, state.isSaving, state.errorMessage,
            onFormChange, onSaveDebt, onDismissForm)
    }
    state.paymentForm?.let { form ->
        DebtPaymentForm(form,
            state.paymentAccounts,
            state.isSaving, state.errorMessage,
            onPaymentChange, onSavePayment, onDismissPayment)
    }
    state.debtToDelete?.let { debt ->
        AlertDialog(
            onDismissRequest = { if (!state.isSaving) onCancelDelete() },
            title = { Text("Eliminar deuda") },
            text = { Column {
                Text("¿Eliminar ${debt.name}? Solo se permite si no tiene abonos registrados.")
                DebtError(state.errorMessage)
            } },
            confirmButton = { TextButton(onClick = onConfirmDelete, enabled = !state.isSaving) {
                Text("Eliminar")
            } },
            dismissButton = { TextButton(onClick = onCancelDelete, enabled = !state.isSaving) {
                Text("Cancelar")
            } }, containerColor = KashuTheme.colors.surface
        )
    }
}

@Composable
fun DebtContent(
    state: DebtState, onBack: () -> Unit, onRefresh: () -> Unit, onCreate: () -> Unit,
    onEdit: (Debt) -> Unit, onPay: (Debt) -> Unit, onDelete: (Debt) -> Unit
) {
    val enabled = !state.isLoading && !state.isSaving
    Surface(color = KashuTheme.colors.mainBackground, contentColor = KashuTheme.colors.title) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onBack, enabled = !state.isSaving) { Text("Volver") }
                    Text("Deudas", style = MaterialTheme.typography.headlineSmall)
                    TextButton(onClick = onRefresh, enabled = enabled) { Text("Actualizar") }
                }
                Button(onClick = onCreate, enabled = enabled) { Text("Nueva deuda") }
                if (state.isLoading || state.isSaving) LinearProgressIndicator(Modifier.fillMaxWidth())
                DebtError(state.errorMessage)
                state.infoMessage?.let { Text(it, color = KashuTheme.colors.mainColor) }
            }
            items(state.totalsByCurrency.toList(), key = { it.first }) { (currency, amount) ->
                Text("Pendiente: ${debtMoney(currency, amount)}", fontWeight = FontWeight.Bold)
            }
            if (state.debts.isEmpty() && !state.isLoading) item { Text("No hay deudas registradas") }
            items(state.debts, key = { it.id }) { debt ->
                Card(colors = CardDefaults.cardColors(containerColor = KashuTheme.colors.surface)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(debt.name, style = MaterialTheme.typography.titleMedium)
                        debt.creditor?.takeIf { it.isNotBlank() }?.let { Text("Acreedor: $it") }
                        Text("Pendiente: ${debtMoney(debt.currency, debt.outstandingAmount)}")
                        Text("Inicial: ${debtMoney(debt.currency, debt.initialAmount)}")
                        if (debt.outstandingAmount == 0.0) Text("Pagada", color = KashuTheme.colors.mainColor)
                        Row {
                            TextButton(onClick = { onPay(debt) }, enabled = enabled && debt.outstandingAmount > 0) { Text("Abonar") }
                            TextButton(onClick = { onEdit(debt) }, enabled = enabled) { Text("Editar") }
                            TextButton(onClick = { onDelete(debt) }, enabled = enabled &&
                                state.payments.none { it.debtId == debt.id }) { Text("Eliminar") }
                        }
                    }
                }
            }
            item { Text("Historial de abonos", style = MaterialTheme.typography.titleLarge) }
            if (state.payments.isEmpty()) item { Text("Sin abonos registrados") }
            items(state.payments, key = { it.id }) { payment ->
                val debtName = state.debts.find { it.id == payment.debtId }?.name ?: "Deuda"
                val accountName = state.accounts.find { it.id == payment.accountId }?.name ?: "Cuenta"
                Card(colors = CardDefaults.cardColors(containerColor = KashuTheme.colors.surface)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("Pago de deuda: $debtName", fontWeight = FontWeight.Bold)
                        Text("$accountName · ${debtMoney(payment.currency, payment.amount)}")
                        Text(payment.paidAt)
                        if (payment.description.isNotBlank()) Text(payment.description)
                    }
                }
            }
        }
    }
}

internal fun debtMoney(currency: String, amount: Double): String =
    "$currency ${String.format(Locale.US, "%.2f", amount)}"

@Composable
internal fun DebtError(message: String?) {
    if (!message.isNullOrBlank()) Text(message, color = MaterialTheme.colorScheme.error)
}

@Composable
private fun DebtForm(
    form: DebtFormState, currencies: List<String>, saving: Boolean, error: String?,
    onChange: (DebtFormState) -> Unit, onSave: () -> Unit, onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = { if (!saving) onDismiss() },
        title = { Text(if (form.isEditing) "Editar deuda" else "Nueva deuda") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = form.name, onValueChange = { onChange(form.copy(name = it)) },
                    label = { Text("Nombre") }, enabled = !saving, singleLine = true)
                OutlinedTextField(value = form.creditor, onValueChange = { onChange(form.copy(creditor = it)) },
                    label = { Text("Acreedor (opcional)") }, enabled = !saving, singleLine = true)
                if (!form.isEditing) {
                    Text("Moneda")
                    currencies.forEach { currency ->
                        Row {
                            RadioButton(selected = form.currency == currency,
                                onClick = { onChange(form.copy(currency = currency)) }, enabled = !saving)
                            Text(currency, Modifier.padding(top = 12.dp))
                        }
                    }
                    OutlinedTextField(value = form.amount, onValueChange = { onChange(form.copy(amount = it)) },
                        label = { Text("Monto pendiente inicial") }, enabled = !saving, singleLine = true)
                    Text("Registrar esta deuda no ingresa dinero a tus cuentas.")
                } else {
                    Text("Moneda: ${form.currency}. El saldo pendiente se reduce registrando abonos.")
                }
                DebtError(error)
            }
        },
        confirmButton = { TextButton(onClick = onSave, enabled = !saving) { Text(if (saving) "Guardando…" else "Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !saving) { Text("Cancelar") } },
        containerColor = KashuTheme.colors.surface
    )
}

@Composable
private fun DebtPaymentForm(
    form: DebtPaymentState, accounts: List<com.kashuapp.data.account.Account>, saving: Boolean,
    error: String?, onChange: (DebtPaymentState) -> Unit, onSave: () -> Unit, onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = { if (!saving) onDismiss() },
        title = { Text("Abonar a ${form.debt.name}") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pendiente: ${debtMoney(form.debt.currency, form.debt.outstandingAmount)}")
                Text("Cuenta de origen (${form.debt.currency})")
                if (accounts.isEmpty()) Text("No tienes cuentas activas de esta moneda")
                accounts.forEach { account ->
                    Row {
                        RadioButton(selected = form.accountId == account.id,
                            onClick = { onChange(form.copy(accountId = account.id.orEmpty())) }, enabled = !saving)
                        Text("${account.name}: ${debtMoney(account.currency, account.currentBalance)}", Modifier.padding(top = 12.dp))
                    }
                }
                OutlinedTextField(value = form.amount, onValueChange = { onChange(form.copy(amount = it)) },
                    label = { Text("Monto del abono") }, enabled = !saving, singleLine = true)
                OutlinedTextField(value = form.date, onValueChange = { onChange(form.copy(date = it)) },
                    label = { Text("Fecha (AAAA-MM-DD)") }, enabled = !saving, singleLine = true)
                OutlinedTextField(value = form.time, onValueChange = { onChange(form.copy(time = it)) },
                    label = { Text("Hora (HH:mm, Lima)") }, enabled = !saving, singleLine = true)
                OutlinedTextField(value = form.description, onValueChange = { onChange(form.copy(description = it)) },
                    label = { Text("Descripción (opcional)") }, enabled = !saving)
                Text("Abono a capital, sin intereses. Se descontará de la cuenta seleccionada.")
                DebtError(error)
            }
        },
        confirmButton = { TextButton(onClick = onSave, enabled = !saving && accounts.isNotEmpty()) {
            Text(if (saving) "Guardando…" else "Confirmar abono")
        } },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !saving) { Text("Cancelar") } },
        containerColor = KashuTheme.colors.surface
    )
}
