package com.kashuapp.ui.transaction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
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
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuLogo
import com.kashuapp.ui.addTrans.AddTransact
import androidx.compose.foundation.lazy.items
@Composable
fun TransactionView(
    viewModel: TransactionVM = remember { TransactionVM() }
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getTrans()
        viewModel.loadCategories()
    }


    if (mostrarDialogo){
        AddTransact(
        onDismiss = {
            mostrarDialogo = false
            viewModel.getTrans()
            viewModel.loadCategories()},

        )
    }


    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(                modifier = Modifier.weight(0.8f),
            ){
                KashuLogo(
                    fontsize = 20,
                    horizontalPadding = 0,
                    verticalPadding = 0
                )

            }

            KashuButton(
                modifier = Modifier.weight(0.2f),
                height = 36,
                onClickFun = { mostrarDialogo = true },
                text = "+"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Recent Activity",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {

            items(viewModel.transactions){item ->

                    val itemCategory = viewModel.getCategoryName(item.categoryId)
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(itemCategory)
                    Text("${item.amount}")


                }


            }












        }
    }
}