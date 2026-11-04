package com.kashuapp.ui.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.account.AccountRepository
import com.kashuapp.data.account.IAccountRepository
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import com.kashuapp.data.currency.CurrencyRepository
import com.kashuapp.data.currency.ICurrencyRepository
import com.kashuapp.data.debt.Debt
import com.kashuapp.data.debt.DebtRepository
import com.kashuapp.data.debt.IDebtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class DebtVM(
    private val debtRepo: IDebtRepository = DebtRepository(),
    private val accountRepo: IAccountRepository = AccountRepository(),
    private val authRepo: IAuthRepository = AuthRepository(),
    private val currencyRepo: ICurrencyRepository = CurrencyRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(DebtState())
    val uiState = _uiState.asStateFlow()

    private var pendingPayment: DebtPaymentState? = null
    private var pendingCreation: DebtFormState? = null

    init { loadData() }

    fun loadData() {
        if (busy()) return
        val userId = authRepo.getCurrentUserId().id
        if (userId.isBlank() || userId == "Not Gotten") {
            showError("Sesión no válida")
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val debts = debtRepo.getDebts(userId)
            val payments = debtRepo.getPayments(userId)
            val accounts = accountRepo.getAllAccount(userId)
            val currencies = currencyRepo.getAllCurrency()
            if (debts.isSuccess && payments.isSuccess && accounts.isSuccess && currencies.isSuccess) {
                val list = debts.getOrThrow().sortedBy { it.name.lowercase() }
                val ownAccounts = accounts.getOrThrow().filter { it.userId == userId }
                _uiState.update { it.copy(
                    debts = list,
                    payments = payments.getOrThrow().sortedByDescending { payment -> payment.paidAt },
                    accounts = ownAccounts,
                    currencies = currencies.getOrThrow().map { currency -> currency.code }.distinct(),
                    totalsByCurrency = list.groupBy { debt -> debt.currency }
                        .mapValues { (_, values) -> values.sumOf { debt -> debt.outstandingAmount } },
                    paymentAccounts = ownAccounts.filter { account -> account.isActive &&
                        account.currency == it.paymentForm?.debt?.currency },
                    isLoading = false
                ) }
            } else {
                _uiState.update { it.copy(isLoading = false,
                    errorMessage = "No se pudo cargar Deudas. Revisa la conexión y la configuración del backend.") }
            }
        }
    }

    fun openCreate() {
        if (busy()) return
        _uiState.update { it.copy(form = pendingCreation ?: DebtFormState(
            id = UUID.randomUUID().toString(), currency = it.currencies.firstOrNull() ?: "PEN"),
            errorMessage = null, infoMessage = null) }
    }

    fun openEdit(debt: Debt) {
        if (busy()) return
        _uiState.update { it.copy(form = DebtFormState(id = debt.id, isEditing = true,
            name = debt.name, creditor = debt.creditor.orEmpty(), currency = debt.currency),
            errorMessage = null, infoMessage = null) }
    }

    fun changeForm(form: DebtFormState) {
        if (busy()) return
        if (pendingCreation?.id == form.id) {
            showError("Reintenta el registro con los mismos datos; si ya se guardó, actualiza la lista")
        } else {
            _uiState.update { it.copy(form = form, errorMessage = null) }
        }
    }

    fun dismissForm() {
        if (!busy()) _uiState.update { it.copy(form = null, errorMessage = null) }
    }

    fun saveDebt() {
        if (busy()) return
        val form = _uiState.value.form ?: return
        val userId = authRepo.getCurrentUserId().id
        if (userId.isBlank() || userId == "Not Gotten") { showError("Sesión no válida"); return }
        if (form.name.trim().isEmpty() || form.name.length > 100 || form.creditor.length > 100) {
            showError("Nombre obligatorio de hasta 100 caracteres; acreedor de hasta 100")
            return
        }
        val amount = positiveAmount(form.amount)
        if (!form.isEditing && (amount == null || form.currency !in _uiState.value.currencies)) {
            showError("Selecciona una moneda y un monto positivo con hasta dos decimales")
            return
        }
        if (!form.isEditing) pendingCreation = form
        save {
            if (form.isEditing) {
                debtRepo.updateDetails(form.id, form.name.trim(), form.creditor.trim())
            } else {
                debtRepo.createDebt(Debt(form.id, userId, form.name.trim(), form.creditor.trim(),
                    form.currency, amount!!.toDouble(), amount.toDouble()))
            }
        }
    }

    fun openPayment(debt: Debt) {
        if (busy() || debt.outstandingAmount <= 0) return
        val previous = pendingPayment
        if (previous != null && previous.debt.id != debt.id) {
            showError("Primero resuelve el abono pendiente a ${previous.debt.name}; revisa su historial")
            return
        }
        val now = Date()
        fun format(pattern: String) = SimpleDateFormat(pattern, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("America/Lima")
        }.format(now)
        _uiState.update { it.copy(paymentForm = previous ?: DebtPaymentState(
            UUID.randomUUID().toString(), debt, date = format("yyyy-MM-dd"), time = format("HH:mm")),
            paymentAccounts = it.accounts.filter { account -> account.isActive && account.currency == debt.currency },
            errorMessage = null, infoMessage = null) }
    }

    fun changePayment(form: DebtPaymentState) {
        if (busy()) return
        if (pendingPayment != null) {
            showError("Reintenta el mismo abono o actualiza el historial antes de cambiar sus datos")
        } else {
            _uiState.update { it.copy(paymentForm = form, errorMessage = null) }
        }
    }

    fun dismissPayment() {
        if (!busy()) _uiState.update { it.copy(paymentForm = null, errorMessage = null) }
    }

    fun pay() {
        if (busy()) return
        val form = _uiState.value.paymentForm ?: return
        val amount = positiveAmount(form.amount)
        if (amount == null) { showError("Ingresa un monto positivo con hasta dos decimales"); return }
        val account = _uiState.value.accounts.find { it.id == form.accountId }
        if (pendingPayment == null) {
            if (account == null || !account.isActive || account.currency != form.debt.currency) {
                showError("Selecciona una cuenta activa de la misma moneda"); return
            }
            if (amount.toBigDecimal() > form.debt.outstandingAmount.toBigDecimal() ||
                amount.toBigDecimal() > account.currentBalance.toBigDecimal()) {
                showError("El abono supera el saldo de la cuenta o la deuda pendiente"); return
            }
        }
        if (!validDateTime(form.date, form.time) || form.description.length > 250) {
            showError("Usa fecha AAAA-MM-DD, hora HH:mm y descripción de hasta 250 caracteres"); return
        }
        pendingPayment = form
        save { debtRepo.payDebt(form.requestId, form.debt.id, form.accountId, amount,
            "${form.date} ${form.time}", form.description.trim()) }
    }

    fun requestDelete(debt: Debt) {
        if (!busy()) _uiState.update { it.copy(debtToDelete = debt, errorMessage = null) }
    }

    fun cancelDelete() {
        if (!busy()) _uiState.update { it.copy(debtToDelete = null) }
    }

    fun deleteDebt() {
        if (busy()) return
        val debt = _uiState.value.debtToDelete ?: return
        if (_uiState.value.payments.any { it.debtId == debt.id }) {
            showError("No puedes eliminar una deuda con abonos registrados"); return
        }
        save { debtRepo.deleteDebt(debt.id) }
    }

    private fun busy() = _uiState.value.isLoading || _uiState.value.isSaving
    private fun showError(message: String) { _uiState.update { it.copy(errorMessage = message) } }

    private fun save(action: suspend () -> Result<Unit>) {
        _uiState.update { it.copy(isSaving = true, errorMessage = null, infoMessage = null) }
        viewModelScope.launch {
            action().onSuccess {
                if (_uiState.value.paymentForm != null) pendingPayment = null
                if (_uiState.value.form?.isEditing == false) pendingCreation = null
                _uiState.update { it.copy(isSaving = false, form = null, paymentForm = null,
                    debtToDelete = null, infoMessage = "Operación guardada") }
                loadData()
            }.onFailure { failure ->
                _uiState.update { it.copy(isSaving = false,
                    errorMessage = failure.message ?: "No se pudo guardar. Revisa el historial antes de repetir.") }
            }
        }
    }

    private fun positiveAmount(text: String): String? {
        val normalized = text.trim().replace(',', '.')
        if (!Regex("\\d{1,10}(\\.\\d{1,2})?").matches(normalized)) return null
        val amount = normalized.toBigDecimalOrNull() ?: return null
        return if (amount > BigDecimal.ZERO) amount.setScale(2).toPlainString() else null
    }

    private fun validDateTime(date: String, time: String): Boolean {
        val value = "$date $time"
        if (!Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}").matches(value)) return false
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).apply { isLenient = false }
        val position = ParsePosition(0)
        return format.parse(value, position) != null && position.index == value.length
    }
}
