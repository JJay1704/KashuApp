package com.kashuapp.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.data.transaction.Transaction
import com.kashuapp.ui.theme.KashuTheme

@Composable
fun TransactionView(
    viewModel: TransactionVM = viewModel(),
    onNavigateToCategory: () -> Unit = {}
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getTrans()
        viewModel.loadCategories()
    }


    if (mostrarDialogo) {
        AddTransact(
            onDismiss = {
                mostrarDialogo = false
                viewModel.getTrans()
                viewModel.loadCategories()
            },
            onNavigateToCategory = onNavigateToCategory
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
            ) {
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
                        item = item,
                        categoryName = itemCategory
                    )
                }
            }
        }
    }



}
@Composable
fun TransactionItemCard(
    item: Transaction,
    categoryName: String
) {
    val isIncome = item.type.equals("INCOME", ignoreCase = true)
    val amountColor = if (isIncome) Color(0xFF10B981) else Color(0xFFEF4444)
    val amountPrefix = if (isIncome) "+ S/." else "- S/."
    val iconVector =
        if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = KashuTheme.colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(amountColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = item.type,
                        tint = amountColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = categoryName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KashuTheme.colors.title
                    )
                    Text(
                        text = if (!item.description.isNullOrBlank()) "${item.transactionDate} · ${item.description}" else item.transactionDate,
                        fontSize = 12.sp,
                        color = KashuTheme.colors.subtitle,
                        maxLines = 1
                    )
                }
            }

            Text(
                text = "$amountPrefix ${
                    String.format(
                        java.util.Locale.US,
                        "%.2f",
                        item.amount
                    )
                }",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}