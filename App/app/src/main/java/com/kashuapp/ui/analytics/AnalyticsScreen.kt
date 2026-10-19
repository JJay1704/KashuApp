package com.kashuapp.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.ui.budget.MonthSelector
import com.kashuapp.ui.theme.KashuTheme

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsVM = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Distribución (Torta)", "Comparativa (Barras)")

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Gráficos y Estadísticas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = KashuTheme.colors.title
        )

        Spacer(modifier = Modifier.height(14.dp))

        MonthSelector(
            month = uiState.selectedMonth,
            year = uiState.selectedYear,
            onPrevious = { viewModel.changeMonth(-1) },
            onNext = { viewModel.changeMonth(1) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        MetricSummaryCards(
            totalIncome = uiState.totalIncome,
            totalExpense = uiState.totalExpense,
            netSavings = uiState.netSavings
        )

        Spacer(modifier = Modifier.height(14.dp))

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = KashuTheme.colors.surface,
            contentColor = KashuTheme.colors.mainColor,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = KashuTheme.colors.mainColor
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) KashuTheme.colors.mainColor else KashuTheme.colors.subtitle
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = KashuTheme.colors.mainColor)
            }
        } else if (!uiState.hasData) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sin movimientos registrados en este mes",
                    color = KashuTheme.colors.subtitle,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    if (selectedTabIndex == 0) {
                        PieChartExpenses(
                            shares = uiState.categoryExpenses,
                            totalExpense = uiState.totalExpense
                        )
                    } else {
                        ComparisonBarChart(
                            income = uiState.totalIncome,
                            expense = uiState.totalExpense
                        )
                    }
                }
            }
        }
    }
}
