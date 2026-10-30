package com.kashuapp.ui.goals.list

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
import com.kashuapp.ui.goals.AddSavingsDialog
import com.kashuapp.ui.goals.GoalCard
import com.kashuapp.ui.goals.GoalGlobalProgress
import com.kashuapp.ui.goals.form.GoalFormDialog
import com.kashuapp.ui.theme.KashuTheme

@Composable
fun GoalsScreen(
    goalsVM: GoalsVM = viewModel()
) {
    val uiState by goalsVM.uiState.collectAsState()

    LaunchedEffect(Unit) {
        goalsVM.loadGoals()
    }

    if (uiState.isFormOpen) {
        GoalFormDialog(
            goalToEdit = uiState.goalToEdit,
            onDismiss = {
                goalsVM.closeForm()
                goalsVM.loadGoals()
            }
        )
    }

    val selectedGoal = uiState.selectedGoalForSavings
    if (uiState.isAddSavingsOpen && selectedGoal != null) {
        AddSavingsDialog(
            goal = selectedGoal,
            onDismiss = { goalsVM.closeAddSavings() },
            onConfirmSavings = { amountStr ->
                goalsVM.submitSavings(amountStr)
            }
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
                text = "Mis Metas de Ahorro",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = KashuTheme.colors.title
            )

            IconButton(
                onClick = { goalsVM.openCreateGoal() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(KashuTheme.colors.mainColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nueva Meta",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading && uiState.goals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = KashuTheme.colors.mainColor)
            }
        } else if (uiState.goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no tienes metas registradas",
                    color = KashuTheme.colors.subtitle,
                    fontSize = 14.sp
                )
            }
        } else {
            GoalGlobalProgress(uiState = uiState)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Todas mis metas (${uiState.goals.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KashuTheme.colors.title
                )

                Text(
                    text = "${uiState.completedGoalsCount} completadas",
                    fontSize = 13.sp,
                    color = KashuTheme.colors.mainColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.goals) { goal ->
                    GoalCard(
                        goal = goal,
                        deadlineAlert = goalsVM.getDeadlineAlert(goal),
                        onAddSavingsClick = { goalsVM.openAddSavings(goal) },
                        onClickCard = { goalsVM.openEditGoal(goal) }
                    )
                }
            }
        }
    }
}