package com.kashuapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.kashuapp.core.composables.NetWorthCard
import com.kashuapp.ui.theme.KashuTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import com.kashuapp.ui.transaction.TransactionItemCard


@Composable
fun HomeView(
    onViewAllClick: () -> Unit = {},
    uiState: HomeState,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)

    ) {
        val showInitialLoading =
            uiState.isLoading &&
                    uiState.accounts.isEmpty() &&
                    uiState.transactions.isEmpty()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            if (showInitialLoading) {
                CircularProgressIndicator(
                    color = KashuTheme.colors.mainColor
                )
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.accountsByCurrency) { item ->
                        Box(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .fillMaxHeight()

                        ) {
                            NetWorthCard(
                                accountNetWorth = item.formattedTotal,
                                modifier = Modifier.fillMaxHeight()

                            )
                        }
                    }
                }
            }
        }
        val message = uiState.errorMessage

        if (message != null) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
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
                modifier = Modifier.clickable { onViewAllClick() },
                color = KashuTheme.colors.mainColor
            )

        }
        Spacer(modifier = Modifier.height(24.dp))


        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(uiState.transactions) { item ->
                val categoryName = uiState.categories
                    .find { it.id == item.categoryId }
                    ?.name ?: "Sin categoría"

                val account = uiState.accounts
                    .find { it.id == item.accountId }
                TransactionItemCard(
                    item = item,
                    categoryName = categoryName,
                    accountName = account?.name ?: "Sin cuenta",
                    currency = account?.currency ?: ""
                )
            }
        }


    }


}