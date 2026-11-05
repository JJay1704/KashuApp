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
import com.kashuapp.data.debt.DebtPayment
import com.kashuapp.data.debt.DebtRepository
import com.kashuapp.data.debt.IDebtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    init {
        loadData()
    }

    fun loadData() {
        if (busy()) return
        val userId = authRepo.getCurrentUserId().id
        if (userId.isBlank() || userId == "Not Gotten") {
            showError("Sesión no válida")
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val rawDebts = debtRepo.getDebts(userId).getOrThrow()
                val pendingDebts = rawDebts.filter { it.outstandingAmount > 0 }.sortedBy { it.name.lowercase() }
                val paidDebts = rawDebts.filter { it.outstandingAmount <= 0 }.sortedBy { it.name.lowercase() }
                val list = pendingDebts + paidDebts

                val payments = debtRepo.getPayments(userId).getOrThrow()
                    .sortedByDescending { it.paidAt }
                val ownAccounts = accountRepo.getAllAccount(userId).getOrThrow()
                val currencies = currencyRepo.getAllCurrency().getOrThrow()
                    .map { it.code }.distinct().sorted()

                _uiState.update {
                    it.copy(
                        debts = list,
                        payments = payments,
                        accounts = ownAccounts,
                        currencies = currencies,
                        totalsByCurrency = list.groupBy { debt -> debt.currency }
                            .mapValues { (_, values) -> values.sumOf { debt -> debt.outstandingAmount } },
                        paymentAccounts = ownAccounts.filter { account ->
                            account.isActive && account.currency == it.paymentForm?.debt?.currency
                        },
                        isLoading = false
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se pudieron cargar las deudas. Revisa tu conexión y pulsa actualizar."
                    )
                }
            }
        }
    }

    fun openCreate() {
        if (busy()) return
        if (_uiState.value.currencies.isEmpty()) {
            showError("No hay monedas disponibles. Actualiza antes de registrar una deuda")
            return
        }
        _uiState.update {
            it.copy(
                form = DebtFormState(
                    id = UUID.randomUUID().toString(),
                    currency = it.currencies.firstOrNull() ?: "PEN"
                ),
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    fun openEdit(debt: Debt) {
        if (busy()) return
        _uiState.update {
            it.copy(
                form = DebtFormState(
                    id = debt.id,
                    isEditing = true,
                    name = debt.name,
                    creditor = debt.creditor.orEmpty(),
                    currency = debt.currency
                ),
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    fun changeForm(form: DebtFormState) {
        if (busy()) return
        _uiState.update { it.copy(form = form, errorMessage = null) }
    }

    fun dismissForm() {
        if (!busy()) _uiState.update { it.copy(form = null, errorMessage = null) }
    }

    fun saveDebt() {
        if (busy()) return
        val form = _uiState.value.form ?: return
        val userId = authRepo.getCurrentUserId().id
        if (userId.isBlank() || userId == "Not Gotten") {
            showError("Sesión no válida")
            return
        }
        if (form.name.trim().isEmpty() || form.name.length > 100 || form.creditor.length > 100) {
            showError("Nombre obligatorio de hasta 100 caracteres; acreedor de hasta 100")
            return
        }

        val amount = form.amount.trim().replace(',', '.').toDoubleOrNull()
        if (!form.isEditing && (amount == null || amount <= 0.0 || form.currency !in _uiState.value.currencies)) {
            showError("Selecciona una moneda y un monto mayor a 0")
            return
        }

        save {
            if (form.isEditing) {
                debtRepo.updateDetails(form.id, form.name.trim(), form.creditor.trim())
            } else {
                val validAmount = amount ?: 0.0
                debtRepo.createDebt(
                    Debt(
                        id = form.id,
                        userId = userId,
                        name = form.name.trim(),
                        creditor = form.creditor.trim(),
                        currency = form.currency,
                        initialAmount = validAmount,
                        outstandingAmount = validAmount
                    )
                )
            }
        }
    }

    fun openPayment(debt: Debt) {
        if (busy() || debt.outstandingAmount <= 0) return

        val now = Date()
        val limaTimeZone = TimeZone.getTimeZone("America/Lima")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = limaTimeZone }
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US).apply { timeZone = limaTimeZone }

        val accounts = _uiState.value.accounts.filter { it.isActive && it.currency == debt.currency }
        _uiState.update {
            it.copy(
                paymentForm = DebtPaymentState(
                    requestId = UUID.randomUUID().toString(),
                    debt = debt,
                    accountId = accounts.singleOrNull()?.id.orEmpty(),
                    date = dateFormat.format(now),
                    time = timeFormat.format(now)
                ),
                paymentAccounts = accounts,
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    fun changePayment(form: DebtPaymentState) {
        if (busy()) return
        _uiState.update { it.copy(paymentForm = form, errorMessage = null) }
    }

    fun dismissPayment() {
        if (!busy()) _uiState.update { it.copy(paymentForm = null, errorMessage = null) }
    }

    fun pay() {
        if (busy()) return
        val userId = authRepo.getCurrentUserId().id
        if (userId.isBlank() || userId == "Not Gotten") {
            showError("Sesión no válida")
            return
        }
        val form = _uiState.value.paymentForm ?: return
        val amount = form.amount.trim().replace(',', '.').toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            showError("Ingresa un monto válido mayor a 0")
            return
        }

        val account = _uiState.value.accounts.find { it.id == form.accountId }
        if (account == null || !account.isActive || account.currency != form.debt.currency) {
            showError("Selecciona una cuenta activa de la misma moneda")
            return
        }
        if (amount > form.debt.outstandingAmount || amount > account.currentBalance) {
            showError("El abono supera el saldo de la cuenta o la deuda pendiente")
            return
        }
        if (form.date.isBlank() || form.time.isBlank() || form.description.length > 250) {
            showError("Completa la fecha, hora y una descripción de hasta 250 caracteres")
            return
        }

        val paidAt = "${form.date}T${form.time}:00-05:00"
        save {
            debtRepo.payDebt(
                DebtPayment(
                    id = form.requestId,
                    debtId = form.debt.id,
                    accountId = form.accountId,
                    amount = amount,
                    currency = form.debt.currency,
                    paidAt = paidAt,
                    description = form.description.trim(),
                    userId = userId
                )
            )
        }
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
            showError("No puedes eliminar una deuda con abonos registrados")
            return
        }
        save { debtRepo.deleteDebt(debt.id) }
    }

    private fun busy() = _uiState.value.isLoading || _uiState.value.isSaving

    private fun showError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    private fun save(action: suspend () -> Result<Unit>) {
        _uiState.update { it.copy(isSaving = true, errorMessage = null, infoMessage = null) }
        viewModelScope.launch {
            val result = action()
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        form = null,
                        paymentForm = null,
                        debtToDelete = null,
                        infoMessage = "Operación guardada"
                    )
                }
                loadData()
            }.onFailure { failure ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = failure.message ?: "No se pudo guardar la operación"
                    )
                }
            }
        }
    }
}
