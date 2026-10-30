package com.kashuapp.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.core.composables.KashuTopBar
import com.kashuapp.core.navigation.HomeTab
import com.kashuapp.ui.analytics.AnalyticsScreen
import com.kashuapp.ui.budget.BudgetScreen
import com.kashuapp.ui.goals.list.GoalsScreen
import com.kashuapp.ui.theme.KashuTheme
import com.kashuapp.ui.transaction.form.AddTransact
import com.kashuapp.ui.transaction.list.TransactionVM
import com.kashuapp.ui.transaction.list.TransactionView

@Composable
fun HomeScreen(

    onNavigateToCategory: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToAccount: () -> Unit = {},
    homeVM: HomeVM = viewModel()

) {

    val homeState by homeVM.uiState.collectAsState()
    val transactionListVM: TransactionVM = viewModel()
    var showAddTransaction by remember { mutableStateOf(false) }
    if (showAddTransaction) {
        AddTransact(
            onDismiss = {
                showAddTransaction = false
                transactionListVM.getTrans()
                homeVM.loadData()
            }, onNavigateToCategory = onNavigateToCategory
        )
    }


    var selectedTab by remember { mutableStateOf<HomeTab>(HomeTab.Home) }
    Scaffold(
        containerColor = KashuTheme.colors.mainBackground,

        topBar = {

            KashuTopBar(
                onNavigateToCategory = onNavigateToCategory,
                onNavigateToAccount = onNavigateToAccount,
                onLogout = onLogout,
                onNewTrans = { showAddTransaction = true })
        }, bottomBar = {
            NavigationBar(
                containerColor = KashuTheme.colors.surface,
            ) {
                HomeTab.tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon, contentDescription = tab.title
                            )
                        },
                        label = { Text(text = tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = KashuTheme.colors.mainColor,
                            selectedTextColor = KashuTheme.colors.mainColor,
                            unselectedIconColor = KashuTheme.colors.subtitle,
                            unselectedTextColor = KashuTheme.colors.subtitle,
                            indicatorColor = KashuTheme.colors.surface
                        )
                    )
                }
            }

        }) { innerPadding ->


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                is HomeTab.Home -> {

                    LaunchedEffect(Unit) {
                        homeVM.loadData()
                    }
                    HomeView(
                        uiState = homeState,
                        onViewAllClick = { selectedTab = HomeTab.History })
                }

                is HomeTab.Goals -> {
                    GoalsScreen()

                }

                is HomeTab.Budget -> {
                    BudgetScreen()
                }

                is HomeTab.Analytics -> {
                    AnalyticsScreen()
                }

                is HomeTab.History -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                    ) {
                        TransactionView(
                            viewModel = transactionListVM,
                            onNavigateToCategory = onNavigateToCategory
                        )

                    }
                }
            }
        }
    }
}

