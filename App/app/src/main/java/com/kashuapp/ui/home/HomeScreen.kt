package com.kashuapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kashuapp.core.composables.KashuLogo
import com.kashuapp.core.navBar.HomeTab
import com.kashuapp.ui.menu.KashuHomeMenu
import com.kashuapp.ui.theme.KashuTheme
import com.kashuapp.ui.transaction.TransactionView

@Composable
fun HomeScreen(

    onNavigateToCategory: () -> Unit = {}

) {
    var selectedTab by remember { mutableStateOf<HomeTab>(HomeTab.Home) }
    Scaffold(
        containerColor = KashuTheme.colors.mainBackground, bottomBar = {
            NavigationBar(
                containerColor = KashuTheme.colors.surface
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


                    Box(
                        modifier = Modifier.fillMaxWidth(),

                    )


                    {

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Box{

                                    KashuHomeMenu(  onNavigateToCategory = onNavigateToCategory)

                                }
                            }

                    }
                }

                is HomeTab.Goals -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Metas de Ahorro", color = KashuTheme.colors.subtitle
                        )
                    }
                }

                is HomeTab.Transaction -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                    ) {
                        TransactionView(onNavigateToCategory = onNavigateToCategory)
                    }
                }

            }
        }
    }
}

