package com.kashuapp.ui.budget

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.ui.theme.KashuTheme

@Composable
fun BudgetScreen(
    viewModel: BudgetVM = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    if (uiState.isFormOpen) {
        BudgetFormDialog(
            state = uiState,
            onCategoryChange = { viewModel.onCategory(it) },
            onAmountChange = { viewModel.onAmountLimit(it) },
            onConfirm = { viewModel.saveBudget() },
            onDelete = { viewModel.deleteBudget(uiState.budgetToEdit?.id) },
            onDismiss = { viewModel.closeForm() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Presupuestos Mensuales",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = KashuTheme.colors.title
            )

            IconButton(
                onClick = { viewModel.openCreate() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(KashuTheme.colors.mainColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nuevo Presupuesto",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        MonthSelector(
            month = uiState.selectedMonth,
            year = uiState.selectedYear,
            onPrevious = { viewModel.changeMonth(-1) },
            onNext = { viewModel.changeMonth(1) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading && uiState.budgets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = KashuTheme.colors.mainColor)
            }
        } else if (uiState.budgets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay presupuestos para este mes",
                    color = KashuTheme.colors.subtitle,
                    fontSize = 14.sp
                )
            }
        } else {
            OverallBudgetSummaryCard(
                totalBudgeted = uiState.totalBudgeted,
                totalSpent = uiState.totalSpent,
                progress = uiState.overallProgressFraction,
                percentage = uiState.overallPercentage
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Límites por Categoría",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = KashuTheme.colors.title
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.budgets) { budget ->
                    BudgetCard(
                        budget = budget,
                        categoryName = viewModel.getCategoryName(budget.categoryId),
                        spentAmount = viewModel.getSpentForCategory(budget.categoryId),
                        onClickCard = { viewModel.openEdit(budget) }
                    )
                }
            }
        }
    }
}
