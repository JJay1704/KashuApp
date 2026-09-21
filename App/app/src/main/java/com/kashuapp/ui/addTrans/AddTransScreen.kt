package com.kashuapp.ui.addTrans
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuDropdownField
import com.kashuapp.core.composables.KashuTextField
import com.kashuapp.ui.theme.KashuTheme
import java.util.Calendar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransact(

    viewModel: AddTransVM = remember { AddTransVM( ) },

    onDismiss: () -> Unit = {},



) {
    LaunchedEffect (Unit) {
        viewModel.loadCategories()
        viewModel.loadAccounts()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        viewModel.onDate(dateFormat.format(calendar.time))
        viewModel.onTime(timeFormat.format(calendar.time))



    }
    val uiState by viewModel.uiStateTran.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    var errorMessage by remember { mutableStateOf("") }




    var isCreatingNewCategory by remember { mutableStateOf(false) }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
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
                text = "Add Transaction",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = KashuTheme.colors.title
            )

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(KashuTheme.colors.mainBackground)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {


                KashuButton(
                    modifier = Modifier.weight(1f),
                    text = "Expense",
                    height = 42,
                    rounded = 10,
                    backColor = if (uiState.type != "INCOME") Color(0xFFEF4444) else Color.Transparent,
                    textColor = if (uiState.type != "INCOME") Color.White else KashuTheme.colors.subtitle,
                    onClickFun = { viewModel.onType("EXPENSE") }
                )



                KashuButton(
                    modifier = Modifier.weight(1f),
                    text = "Income",
                    height = 42,
                    rounded = 10,
                    backColor = if (uiState.type == "INCOME") KashuTheme.colors.mainColor else Color.Transparent,
                    textColor = if (uiState.type == "INCOME") Color.Black else KashuTheme.colors.subtitle,
                    onClickFun = { viewModel.onType("INCOME") }
                )

            }



            Spacer(modifier = Modifier.height(16.dp))


            KashuTextField(
                label = "Amount (S/.)",
                type =uiState.amount,
                onType = { viewModel.onAmount(it) },
                placeholder = "0.00",
                colorPlaceHolder = KashuTheme.colors.subtitle,
                iconInput = Icons.Default.AttachMoney,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = null
            )

            Spacer(modifier = Modifier.height(14.dp))




            KashuDropdownField(
                label = "Account",
                selectedValue = uiState.accountName,

                icon = Icons.Default.AccountCircle,
                items = viewModel.accounts,
                itemLabel = { it.name },
                onItemSelected = { selectedAccount ->
                    viewModel.onAccount(selectedAccount)
                }
            )



            Spacer(modifier = Modifier.height(16.dp))

            KashuDropdownField(

                label = "Category",
                selectedValue = uiState.categoryName,
                placeholder = "Seleccionar categoría",
                icon = Icons.Default.Category,
                items = viewModel.categories,
                itemLabel = { it.name },
                onItemSelected = {
                    selectedCategory -> viewModel.onCategory(selectedCategory)



                                 },
                extraActionLabel = "Agregar nueva categoría",
                onExtraActionClick = { isCreatingNewCategory = true }
            )




            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {


                KashuDatePickerField(
                    modifier = Modifier.weight(0.5f),
                    selectedDate = uiState.date,
                    onDateSelected = { viewModel.onDate(it)}
                )

                KashuTimePickerField(
                    modifier = Modifier.weight(0.5f),
                    selectedTime = uiState.time,
                    onTimeSelected = {viewModel.onTime(it) }
                )


            }

            Spacer(modifier = Modifier.height(14.dp))

            KashuTextField(
                label = "Descripción (opcional)",
                type = uiState.description,
                onType = { viewModel.onDescription(it) },
                placeholder = "Detalle del movimiento",
                colorPlaceHolder = KashuTheme.colors.subtitle,
                iconInput = Icons.Default.Description,
                trailingIcon = null
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))






            KashuButton(
                modifier = Modifier.fillMaxWidth(),

                        text = "Save Transaction",
                onClickFun = {
                    viewModel.postTrans(
                        date = uiState.date,
                        time = uiState.time,
                        onSuccess = {
                            onDismiss()
                        },

                    )
                },

            )
        }
    }
}











//@Composable
//fun AddTransactionSheet(
//    onDismiss: () -> Unit = {},
//    onSaveTransaction: (
//        amount: Double,
//        type: String,
//        category: String,
//        account: String,
//        description: String,
//        date: String,
//        time: String
//    ) -> Unit = { _, _, _, _, _, _, _ -> }
//) {
//    AddTransact(onDismiss = onDismiss, onSaveTransaction = onSaveTransaction)
//}