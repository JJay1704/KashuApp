package com.kashuapp.ui.home

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.core.composables.NetWorthCard
import com.kashuapp.ui.theme.KashuTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import com.kashuapp.ui.transaction.TransactionItemCard


@Composable
fun HomeView(
    onViewAllClick: () -> Unit = {},
    homeVM: HomeVM = viewModel()
) {
    LaunchedEffect(Unit) {
        homeVM.loadData()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)

    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()
        ) {

            items(homeVM.balances) { item ->
                Column(modifier = Modifier.fillParentMaxWidth()) {
                    Text("Saldo activo: ${item.currency} ${String.format("%.2f", item.assets)}", color = KashuTheme.colors.title)
                    Text("Deudas: ${item.currency} ${String.format("%.2f", item.liabilities)}", color = KashuTheme.colors.subtitle)
                    Text("Reservado en metas: ${item.currency} ${String.format("%.2f", item.reserved)}", color = KashuTheme.colors.subtitle)
                    NetWorthCard(accountNetWorth = "${item.currency} ${String.format("%.2f", item.netWorth)}")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        homeVM.errorMessage?.let { Text(it, color = KashuTheme.colors.subtitle) }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Recent Activity",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = KashuTheme.colors.title
            )

            Text(
                text = "View all",
                modifier = Modifier.clickable {    onViewAllClick() },
                color = KashuTheme.colors.mainColor
            )

        }
        Spacer(modifier = Modifier.height(24.dp))


        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(homeVM.transactions) { item ->
                val itemCategory = homeVM.getCategoryName(item.categoryId)
                val itemAccount = homeVM.getAccountName(item.accountId)
                TransactionItemCard(
                    item = item,
                    categoryName = itemCategory,
                    accountName = itemAccount,
                    category = homeVM.categories.find { it.id == item.categoryId },
                    currency = homeVM.accounts.find { it.id == item.accountId }?.currency ?: "",
                )
            }
        }



    }



}
