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
import com.kashuapp.ui.transaction.TransactionItemCard


@Composable
fun TransactionView(
    viewModel: TransactionVM = viewModel(),

    onNavigateToCategory: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }

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
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Recent Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold
        )

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
                    TransactionItemCard(
                        item = item, categoryName = itemCategory
                    )
                }
            }
        }
    }
}





