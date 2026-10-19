package com.kashuapp.goals.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.goals.ui.composables.AddSavingsDialog
import com.kashuapp.goals.ui.composables.GoalCard
import com.kashuapp.goals.ui.composables.GoalFormDialog
import com.kashuapp.ui.theme.KashuTheme
import java.text.NumberFormat
import java.util.Locale

/**
 * Pantalla Principal del Módulo de Metas de Ahorro Interactivas (HU-07 / RF-07).
 *
 * Basada en el Mockup oficial de KashuApp:
 * - Sección "Timeline & Motivation - Track your progress and stay on budget"
 * - Carrusel horizontal interactivo de metas
 * - Panel analítico con progreso global consolidado
 * - Diálogos modales para creación, edición y abonos en tiempo real
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onNavigateBack: () -> Unit = {},
    goalsVM: GoalsVM = viewModel()
) {
    val uiState by goalsVM.uiState.collectAsState()
    val formState by goalsVM.formState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "PE")).apply {
            maximumFractionDigits = 2
        }
    }

    LaunchedEffect(Unit) {
        goalsVM.loadGoals()
    }

    // Notificaciones Snackbar para éxito o error
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            goalsVM.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            goalsVM.clearMessages()
        }
    }

    Scaffold(
        containerColor = KashuTheme.colors.mainBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Timeline & Motivation",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = KashuTheme.colors.title
                    )
                    Text(
                        text = "Track your progress and stay on budget.",
                        fontSize = 13.sp,
                        color = KashuTheme.colors.subtitle
                    )
                }

                // Botón + para registrar nueva meta
                IconButton(
                    onClick = { goalsVM.openCreateGoal() },
                    modifier = Modifier
                        .size(42.dp)
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
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading && uiState.goals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = KashuTheme.colors.mainColor)
                }
            } else if (uiState.goals.isEmpty()) {
                // Estado vacío amigable
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(KashuTheme.colors.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = KashuTheme.colors.mainColor,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Aún no tienes metas de ahorro",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KashuTheme.colors.title
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Presiona el botón '+' para crear tu primer objetivo y empezar a ahorrar.",
                            fontSize = 14.sp,
                            color = KashuTheme.colors.subtitle,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            lineHeight = 20.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // 1. Carrusel Horizontal de Metas Destacadas (Idéntico al Mockup)
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.goals, key = { it.id ?: it.title }) { goal ->
                                GoalCard(
                                    goal = goal,
                                    onAddSavingsClick = { goalsVM.openAddSavings(goal) },
                                    onEditClick = { goalsVM.openEditGoal(goal) },
                                    onDeleteClick = { goal.id?.let { goalsVM.deleteGoal(it) } },
                                    modifier = Modifier.width(310.dp)
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(20.dp)) }

                    // 2. Tarjeta Resumen Consolidado (Total Ahorrado vs Meta Total)
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(KashuTheme.colors.surface)
                                .padding(20.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Progreso Global de Ahorro",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KashuTheme.colors.title
                                    )
                                    Text(
                                        text = "${uiState.overallProgressPercentage}%",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = KashuTheme.colors.mainColor
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                LinearProgressIndicator(
                                    progress = { uiState.overallProgressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = KashuTheme.colors.mainColor,
                                    trackColor = KashuTheme.colors.mainColor.copy(alpha = 0.2f),
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Total Acumulado",
                                            fontSize = 12.sp,
                                            color = KashuTheme.colors.subtitle
                                        )
                                        Text(
                                            text = currencyFormatter.format(uiState.totalSavedAmount),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = KashuTheme.colors.title
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Objetivo Total",
                                            fontSize = 12.sp,
                                            color = KashuTheme.colors.subtitle
                                        )
                                        Text(
                                            text = currencyFormatter.format(uiState.totalTargetAmount),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = KashuTheme.colors.title
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    // 3. Encabezado de Lista Detallada
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Todas mis metas (${uiState.goals.size})",
                                fontSize = 17.sp,
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
                    }

                    item { Spacer(modifier = Modifier.height(12.dp)) }

                    // 4. Lista Vertical Completa
                    items(uiState.goals, key = { "vertical_${it.id ?: it.title}" }) { goal ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                            GoalCard(
                                goal = goal,
                                onAddSavingsClick = { goalsVM.openAddSavings(goal) },
                                onEditClick = { goalsVM.openEditGoal(goal) },
                                onDeleteClick = { goal.id?.let { goalsVM.deleteGoal(it) } }
                            )
                        }
                    }
                }
            }
        }

        // Diálogo para Crear / Editar Meta
        if (uiState.isFormOpen) {
            GoalFormDialog(
                isEditing = uiState.goalToEdit != null,
                formState = formState,
                onTitleChange = { goalsVM.onFormTitleChange(it) },
                onTargetAmountChange = { goalsVM.onFormTargetAmountChange(it) },
                onInitialAmountChange = { goalsVM.onFormInitialAmountChange(it) },
                onDeadlineChange = { goalsVM.onFormDeadlineChange(it) },
                onIconChange = { goalsVM.onFormIconChange(it) },
                onSelectAccount = { goalsVM.onSelectAccount(it) },
                onSelectCategory = { goalsVM.onSelectCategory(it) },
                onDismiss = { goalsVM.closeForm() },
                onSave = { goalsVM.saveGoal() }
            )
        }

        // Diálogo para Abonar a la Meta
        if (uiState.isAddSavingsOpen && uiState.selectedGoalForSavings != null) {
            AddSavingsDialog(
                goal = uiState.selectedGoalForSavings!!,
                onDismiss = { goalsVM.closeAddSavings() },
                onConfirmSavings = { amountStr ->
                    goalsVM.submitSavings(amountStr)
                }
            )
        }
    }
}
