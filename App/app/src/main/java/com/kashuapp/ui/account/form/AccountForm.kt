package com.kashuapp.ui.account.form

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
import com.kashuapp.data.account.Account
import com.kashuapp.ui.theme.KashuTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountForm(
    accountToEdit: Account? = null,
    onDismiss: () -> Unit,
    onDelete: ((accountId: String) -> Unit)? = null,
    viewModel: AccountFormVM = viewModel(),


) {


    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(accountToEdit) {
        viewModel.initForm(accountToEdit)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
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
                text = if (accountToEdit == null) "New Account" else "Edit Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = KashuTheme.colors.title
            )


            KashuTextField(
                label = "Name",
                type = uiState.name,
                onType = { viewModel.onName(it) },
                placeholder = "Account Name ",
                colorPlaceHolder = KashuTheme.colors.subtitle,
                iconInput = Icons.Default.AttachMoney,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                trailingIcon = null,
                isError = uiState.isNameError != null,
                errorMessage = uiState.isNameError
            )

            KashuDropdownField(
                label = "Account Type",
                selectedValue = uiState.selectedAccountType?.name ?: "",
                placeholder = "Select Type",
                icon = Icons.Default.AccountCircle,
                items = uiState.accountTypes,
                itemLabel = { it.name },
                onItemSelected = { selectedAccount ->
                    viewModel.onAccountType(selectedAccount)

                },
                hasError = uiState.isAccountTypeError != null,
                labelError = uiState.isAccountTypeError
            )
            Spacer(modifier = Modifier.height(8.dp))

            KashuTextField(
                label = "Initial Balance",
                type = uiState.initialBalance,
                onType = { viewModel.onBalance(it) },
                placeholder = "0.00",
                colorPlaceHolder = KashuTheme.colors.subtitle,
                iconInput = Icons.Default.AttachMoney,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = null,
                isError = uiState.isInitialBalanceError != null,
                errorMessage = uiState.isInitialBalanceError


            )
            KashuDropdownField(
                label = "Currency",
                selectedValue = uiState.selectedCurrency?.name ?: "",
                placeholder = "Select Currency",
                icon = Icons.Default.AccountCircle,
                items = uiState.currency,
                itemLabel = { it.name },
                onItemSelected = { selectedAccount ->
                    viewModel.onCurrency(selectedAccount)

                },
                hasError = uiState.isCurrencyError != null,
                labelError = uiState.isCurrencyError
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!uiState.errorMessage.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage ?: "", color = Color(0xFFEF4444), fontSize = 12.sp
                )
            }



            KashuButton(
                modifier = Modifier.fillMaxWidth(),
                text = if (accountToEdit == null) "Save Account" else "Save Changes",
                isLoading = uiState.isLoading,
                onClickFun = {
                    viewModel.saveAccount(

                        accountToEdit = accountToEdit,
                        onSuccess = { onDismiss() })
                })

            if (accountToEdit?.id != null && onDelete != null) {
                Spacer(modifier = Modifier.height(12.dp))
                KashuButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Eliminar Cuenta",
                    backColor = Color(0xFFEF4444),
                    textColor = Color.White,
                    onClickFun = {
                        onDelete(accountToEdit.id)
                        onDismiss()
                    })
            }
        }
    }
}
