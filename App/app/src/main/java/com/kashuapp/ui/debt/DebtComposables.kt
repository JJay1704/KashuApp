package com.kashuapp.ui.debt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuDropdownField
import com.kashuapp.core.composables.KashuTextField
import com.kashuapp.data.account.Account
import com.kashuapp.data.debt.Debt
import com.kashuapp.data.debt.DebtPayment
import com.kashuapp.ui.theme.KashuTheme
import com.kashuapp.ui.transaction.KashuDatePickerField
import com.kashuapp.ui.transaction.KashuTimePickerField
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun DebtView(
    state: DebtState, onBack: () -> Unit, onRefresh: () -> Unit, onCreate: () -> Unit,
    onEdit: (Debt) -> Unit, onPay: (Debt) -> Unit, onRequestDelete: (Debt) -> Unit,
    onFormChange: (DebtFormState) -> Unit, onSaveDebt: () -> Unit, onDismissForm: () -> Unit,
    onPaymentChange: (DebtPaymentState) -> Unit, onSavePayment: () -> Unit, onDismissPayment: () -> Unit,
    onConfirmDelete: () -> Unit, onCancelDelete: () -> Unit
) {
    DebtContent(state, onBack, onRefresh, onCreate, onEdit, onPay, onRequestDelete)
    state.form?.let {
        DebtForm(it, state.currencies, state.isSaving, state.errorMessage, onFormChange, onSaveDebt, onDismissForm)
    }
    state.paymentForm?.let {
        DebtPaymentForm(it, state.paymentAccounts, state.isSaving, state.errorMessage,
            onPaymentChange, onSavePayment, onDismissPayment)
    }
    state.debtToDelete?.let { debt ->
        AlertDialog(
            onDismissRequest = { if (!state.isSaving) onCancelDelete() },
            title = { Text("Eliminar deuda", color = KashuTheme.colors.title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("¿Eliminar «${debt.name}»? Esta acción no se puede deshacer.", color = KashuTheme.colors.subtitle)
                    DebtError(state.errorMessage)
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmDelete, enabled = !state.isSaving) {
                    Text(if (state.isSaving) "Eliminando…" else "Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onCancelDelete, enabled = !state.isSaving) { Text("Cancelar", color = KashuTheme.colors.subtitle) }
            }, containerColor = KashuTheme.colors.surface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtContent(
    state: DebtState, onBack: () -> Unit, onRefresh: () -> Unit, onCreate: () -> Unit,
    onEdit: (Debt) -> Unit, onPay: (Debt) -> Unit, onDelete: (Debt) -> Unit
) {
    val enabled = !state.isLoading && !state.isSaving
    Scaffold(containerColor = KashuTheme.colors.mainBackground, topBar = {
        TopAppBar(
            title = { Text("Mis deudas", fontWeight = FontWeight.Bold, color = KashuTheme.colors.title) },
            navigationIcon = {
                IconButton(onClick = onBack, enabled = !state.isSaving) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = KashuTheme.colors.title)
                }
            },
            actions = {
                IconButton(onClick = onRefresh, enabled = enabled) {
                    Icon(Icons.Default.Refresh, "Actualizar deudas", tint = KashuTheme.colors.subtitle)
                }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = KashuTheme.colors.mainBackground)
        )
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { Text("Organiza tus compromisos y sigue cada abono.", color = KashuTheme.colors.subtitle) }
            if (state.isLoading || state.isSaving) item {
                LinearProgressIndicator(Modifier.fillMaxWidth(), color = KashuTheme.colors.mainColor,
                    trackColor = KashuTheme.colors.surface)
            }
            if (state.form == null && state.paymentForm == null && state.debtToDelete == null) {
                state.errorMessage?.let { message -> item { DebtError(message) } }
            }
            state.infoMessage?.let { message -> item {
                Surface(color = KashuTheme.colors.mainColor.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp)) {
                    Text(message, Modifier.fillMaxWidth().padding(12.dp), color = KashuTheme.colors.mainColor)
                }
            } }
            if (state.totalsByCurrency.isNotEmpty()) item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.totalsByCurrency.toList(), key = { it.first }) { (currency, amount) ->
                        DebtSummary(currency, amount, Modifier.fillParentMaxWidth(0.88f))
                    }
                }
            }
            item { KashuButton(text = "+ Nueva deuda", onClickFun = onCreate, enabled = enabled, textColor = Color.Black) }
            item { DebtSectionTitle("Tus deudas", "${state.debts.size} registradas") }
            if (state.debts.isEmpty() && !state.isLoading) item {
                DebtEmptyState(Icons.Default.AccountBalanceWallet, "Sin deudas registradas",
                    "Agrega una deuda para llevar el control de lo que te falta pagar.")
            }
            items(state.debts, key = { "debt-${it.id}" }) { debt ->
                DebtItemCard(debt, enabled, state.payments.none { it.debtId == debt.id },
                    onEdit = { onEdit(debt) }, onPay = { onPay(debt) }, onDelete = { onDelete(debt) })
            }
            item { DebtSectionTitle("Historial de abonos", "${state.payments.size} movimientos") }
            if (state.payments.isEmpty() && !state.isLoading) item {
                DebtEmptyState(Icons.Default.CheckCircle, "Aún no hay abonos",
                    "Aquí verás tus pagos y la cuenta que usaste para cada uno.")
            }
            items(state.payments, key = { "payment-${it.id}" }) { payment ->
                DebtPaymentCard(payment, state.debts.find { it.id == payment.debtId }?.name ?: "Deuda",
                    state.accounts.find { it.id == payment.accountId }?.name ?: "Cuenta")
            }
        }
    }
}

@Composable
private fun DebtSummary(currency: String, amount: Double, modifier: Modifier = Modifier) {
    Surface(modifier, shape = RoundedCornerShape(20.dp), color = KashuTheme.colors.surface) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DebtIcon(Icons.Default.AccountBalanceWallet)
                Text("TOTAL PENDIENTE · $currency", color = KashuTheme.colors.subtitle, style = MaterialTheme.typography.labelMedium)
            }
            Text(debtMoney(currency, amount), color = KashuTheme.colors.title,
                style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Saldo por pagar en esta moneda", color = KashuTheme.colors.subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun DebtItemCard(debt: Debt, enabled: Boolean, canDelete: Boolean,
    onEdit: () -> Unit, onPay: () -> Unit, onDelete: () -> Unit) {
    val paid = (debt.initialAmount - debt.outstandingAmount).coerceAtLeast(0.0)
    val progress = if (debt.initialAmount > 0) (paid / debt.initialAmount).toFloat().coerceIn(0f, 1f) else 0f
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = KashuTheme.colors.surface)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DebtIcon(if (debt.outstandingAmount <= 0) Icons.Default.CheckCircle else Icons.Default.AccountBalanceWallet)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(debt.name, fontWeight = FontWeight.SemiBold, color = KashuTheme.colors.title,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(debt.creditor?.takeIf { it.isNotBlank() } ?: "Sin acreedor indicado", color = KashuTheme.colors.subtitle,
                        style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = onEdit, enabled = enabled) {
                    Icon(Icons.Default.Edit, "Editar ${debt.name}", tint = KashuTheme.colors.subtitle, modifier = Modifier.size(19.dp))
                }
                IconButton(onClick = onDelete, enabled = enabled && canDelete) {
                    Icon(Icons.Default.DeleteOutline, "Eliminar ${debt.name}", modifier = Modifier.size(20.dp),
                        tint = KashuTheme.colors.subtitle.copy(alpha = if (canDelete) 1f else 0.35f))
                }
            }
            Text(if (debt.outstandingAmount <= 0) "Deuda pagada" else "Por pagar",
                color = KashuTheme.colors.subtitle, style = MaterialTheme.typography.labelMedium)
            Text(debtMoney(debt.currency, debt.outstandingAmount), color = KashuTheme.colors.title,
                style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = KashuTheme.colors.mainColor, trackColor = KashuTheme.colors.mainColor.copy(alpha = 0.12f))
            Text("${(progress * 100).toInt()}% pagado · Inicial: ${debtMoney(debt.currency, debt.initialAmount)}",
                color = KashuTheme.colors.subtitle, style = MaterialTheme.typography.bodySmall)
            if (debt.outstandingAmount > 0) {
                KashuButton(text = "Registrar abono", onClickFun = onPay, enabled = enabled, height = 44, textSize = 14,
                    backColor = KashuTheme.colors.mainColor.copy(alpha = 0.12f), textColor = KashuTheme.colors.mainColor)
            } else Text("Completada", color = KashuTheme.colors.mainColor, fontWeight = FontWeight.SemiBold)
            if (!canDelete) Text("Las deudas con abonos conservan su historial.",
                color = KashuTheme.colors.subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun DebtPaymentCard(payment: DebtPayment, debtName: String, accountName: String) {
    Surface(shape = RoundedCornerShape(16.dp), color = KashuTheme.colors.surface) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DebtIcon(Icons.Default.CheckCircle)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(debtName, color = KashuTheme.colors.title, fontWeight = FontWeight.SemiBold)
                    Text(accountName, color = KashuTheme.colors.subtitle, style = MaterialTheme.typography.bodySmall)
                }
            }
            Text("− ${debtMoney(payment.currency, payment.amount)}", color = KashuTheme.colors.title, fontWeight = FontWeight.Bold)
            Text(debtPaymentDate(payment.paidAt), color = KashuTheme.colors.subtitle, style = MaterialTheme.typography.bodySmall)
            if (payment.description.isNotBlank()) Text(payment.description, color = KashuTheme.colors.subtitle)
        }
    }
}

@Composable
private fun DebtSectionTitle(title: String, detail: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, color = KashuTheme.colors.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(detail, color = KashuTheme.colors.subtitle, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun DebtIcon(icon: ImageVector) {
    Box(Modifier.size(40.dp).clip(CircleShape).background(KashuTheme.colors.mainColor.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = KashuTheme.colors.mainColor, modifier = Modifier.size(21.dp))
    }
}

@Composable
private fun DebtEmptyState(icon: ImageVector, title: String, description: String) {
    Surface(color = KashuTheme.colors.surface, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DebtIcon(icon)
            Text(title, color = KashuTheme.colors.title, fontWeight = FontWeight.SemiBold)
            Text(description, color = KashuTheme.colors.subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
internal fun DebtError(message: String?) {
    if (!message.isNullOrBlank()) Surface(color = MaterialTheme.colorScheme.error.copy(alpha = 0.10f), shape = RoundedCornerShape(12.dp)) {
        Text(message, Modifier.fillMaxWidth().padding(12.dp), color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebtForm(form: DebtFormState, currencies: List<String>, saving: Boolean, error: String?,
    onChange: (DebtFormState) -> Unit, onSave: () -> Unit, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = { if (!saving) onDismiss() }, sheetState = sheetState,
        containerColor = KashuTheme.colors.surface) {
        Column(Modifier.fillMaxWidth().imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)
            .padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(if (form.isEditing) "Editar deuda" else "Nueva deuda", color = KashuTheme.colors.title,
                style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Lleva el control de lo que debes, sin mezclar monedas.", color = KashuTheme.colors.subtitle)
            KashuTextField(label = "Nombre", type = form.name,
                onType = { if (!saving) onChange(form.copy(name = it.take(100))) }, placeholder = "Ej. Préstamo de estudios",
                iconInput = Icons.Default.Description, colorPlaceHolder = KashuTheme.colors.subtitle)
            KashuTextField(label = "Acreedor (opcional)", type = form.creditor,
                onType = { if (!saving) onChange(form.copy(creditor = it.take(100))) }, placeholder = "¿A quién le debes?",
                iconInput = Icons.Default.Person, colorPlaceHolder = KashuTheme.colors.subtitle)
            if (!form.isEditing) {
                KashuDropdownField(label = "Moneda", selectedValue = form.currency, items = currencies,
                    icon = Icons.Default.AttachMoney, onItemSelected = { if (!saving) onChange(form.copy(currency = it)) })
                KashuTextField(label = "Monto pendiente inicial", type = form.amount,
                    onType = { if (!saving) onChange(form.copy(amount = it)) }, placeholder = "0.00",
                    iconInput = Icons.Default.AttachMoney, colorPlaceHolder = KashuTheme.colors.subtitle,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                Text("Registrar la deuda no agrega dinero a tus cuentas.", color = KashuTheme.colors.subtitle,
                    style = MaterialTheme.typography.bodySmall)
            } else Text("Moneda: ${form.currency}. El saldo se reduce al registrar abonos.",
                color = KashuTheme.colors.subtitle, style = MaterialTheme.typography.bodySmall)
            DebtError(error)
            KashuButton(text = "Guardar deuda", onClickFun = onSave, isLoading = saving, textColor = Color.Black)
            TextButton(onClick = onDismiss, enabled = !saving, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Cancelar", color = KashuTheme.colors.subtitle)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebtPaymentForm(form: DebtPaymentState, accounts: List<Account>, saving: Boolean, error: String?,
    onChange: (DebtPaymentState) -> Unit, onSave: () -> Unit, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = { if (!saving) onDismiss() }, sheetState = sheetState,
        containerColor = KashuTheme.colors.surface) {
        Column(Modifier.fillMaxWidth().imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)
            .padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Registrar abono", color = KashuTheme.colors.title,
                style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(form.debt.name, color = KashuTheme.colors.subtitle)
            Surface(color = KashuTheme.colors.mainColor.copy(alpha = 0.10f), shape = RoundedCornerShape(12.dp)) {
                Text("Pendiente: ${debtMoney(form.debt.currency, form.debt.outstandingAmount)}",
                    Modifier.fillMaxWidth().padding(14.dp), color = KashuTheme.colors.title, fontWeight = FontWeight.SemiBold)
            }
            if (accounts.isEmpty()) DebtError("No tienes cuentas activas en ${form.debt.currency} para realizar este abono.")
            else {
                val selected = accounts.find { it.id == form.accountId }
                KashuDropdownField(label = "Cuenta de origen", selectedValue = selected?.name.orEmpty(),
                    items = accounts, itemLabel = { it.name }, icon = Icons.Default.AccountBalanceWallet,
                    onItemSelected = { if (!saving) onChange(form.copy(accountId = it.id.orEmpty())) })
                selected?.let {
                    Text("Disponible: ${debtMoney(it.currency, it.currentBalance)}", color = KashuTheme.colors.subtitle,
                        style = MaterialTheme.typography.bodySmall)
                }
            }
            KashuTextField(label = "Monto del abono (${form.debt.currency})", type = form.amount,
                onType = { if (!saving) onChange(form.copy(amount = it)) }, placeholder = "0.00",
                iconInput = Icons.Default.AttachMoney, colorPlaceHolder = KashuTheme.colors.subtitle,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KashuDatePickerField(modifier = Modifier.weight(1f), selectedDate = form.date,
                    onDateSelected = { if (!saving) onChange(form.copy(date = it)) })
                KashuTimePickerField(modifier = Modifier.weight(1f), label = "Hora (Lima)", selectedTime = form.time,
                    onTimeSelected = { if (!saving) onChange(form.copy(time = it)) })
            }
            KashuTextField(label = "Descripción (opcional)", type = form.description,
                onType = { if (!saving) onChange(form.copy(description = it.take(250))) }, placeholder = "Detalle del pago",
                iconInput = Icons.Default.Description, colorPlaceHolder = KashuTheme.colors.subtitle)
            Text("El abono reduce el pendiente y descuenta el mismo monto de tu cuenta.",
                color = KashuTheme.colors.subtitle, style = MaterialTheme.typography.bodySmall)
            DebtError(error)
            KashuButton(text = "Confirmar abono", onClickFun = onSave, isLoading = saving,
                enabled = accounts.isNotEmpty(), textColor = Color.Black)
            TextButton(onClick = onDismiss, enabled = !saving, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Cancelar", color = KashuTheme.colors.subtitle)
            }
        }
    }
}

internal fun debtMoney(currency: String, amount: Double): String = "$currency ${String.format(Locale.US, "%,.2f", amount)}"

private fun debtPaymentDate(value: String): String {
    return try {
        val raw = value.replace(' ', 'T')
        val dateTimePart = raw.substringBefore("+").substringBefore("Z").substringBefore(".")
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val parsed = parser.parse(dateTimePart) ?: return value
        val formatter = SimpleDateFormat("dd MMM yyyy · HH:mm", Locale.forLanguageTag("es-PE"))
        formatter.format(parsed)
    } catch (e: Exception) {
        value
    }
}
