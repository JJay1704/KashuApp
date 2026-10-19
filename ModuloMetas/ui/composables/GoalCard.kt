package com.kashuapp.goals.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashuapp.goals.data.Goal
import com.kashuapp.ui.theme.KashuTheme
import java.text.NumberFormat
import java.util.Locale

/**
 * Componente visual de tarjeta para una Meta de Ahorro.
 * Diseñado en base al Mockup oficial de KashuApp:
 * - Ícono temático con fondo translúcido
 * - Badge "In Progress" / "Completed"
 * - Nombre, monto actual en grande y monto meta
 * - Barra de progreso reactiva animada y fecha límite ("Target: ...")
 * - Botón interactivo de abono rápido ("+ Abonar")
 */
@Composable
fun GoalCard(
    goal: Goal,
    onAddSavingsClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    // Animación suave del progreso porcentual
    val animatedProgress by animateFloatAsState(
        targetValue = goal.progressFraction,
        animationSpec = tween(durationMillis = 600),
        label = "GoalProgressAnimation"
    )

    val currencyFormatter = remember(goal.currency) {
        NumberFormat.getCurrencyInstance(
            if (goal.currency.equals("USD", ignoreCase = true)) Locale.US else Locale("es", "PE")
        ).apply {
            maximumFractionDigits = 2
        }
    }

    val iconVector: ImageVector = when (goal.icon.lowercase()) {
        "home" -> Icons.Default.Home
        "flight", "trip" -> Icons.Default.Flight
        "star" -> Icons.Default.Star
        else -> Icons.Default.Savings
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(KashuTheme.colors.surface)
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Fila Superior: Ícono, Estado y Menú de Opciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícono de la meta en contenedor circular estilizado
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(KashuTheme.colors.mainColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = goal.title,
                        tint = KashuTheme.colors.mainColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Badge de Estado: "Completed" o "In Progress"
                    val badgeBg = if (goal.isTargetReached) {
                        KashuTheme.colors.mainColor.copy(alpha = 0.2f)
                    } else {
                        KashuTheme.colors.subtitle.copy(alpha = 0.12f)
                    }
                    val badgeTextColor = if (goal.isTargetReached) {
                        KashuTheme.colors.mainColor
                    } else {
                        KashuTheme.colors.subtitle
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(badgeBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (goal.isTargetReached) "Completed" else "In Progress",
                            color = badgeTextColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Menú desplegable para Editar / Eliminar
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = KashuTheme.colors.subtitle
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(KashuTheme.colors.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Editar Meta", color = KashuTheme.colors.title) },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = KashuTheme.colors.title)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onEditClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = Color(0xFFEF4444)) },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Título de la meta
            Text(
                text = goal.title,
                color = KashuTheme.colors.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Montos: Acumulado actual en grande y Monto objetivo
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = currencyFormatter.format(goal.currentAmount),
                    color = KashuTheme.colors.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "/ ${currencyFormatter.format(goal.targetAmount)}",
                    color = KashuTheme.colors.subtitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Barra de progreso estilizada
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = KashuTheme.colors.mainColor,
                trackColor = KashuTheme.colors.mainColor.copy(alpha = 0.15f),
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Porcentaje a la izquierda y Target Date a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${goal.progressPercentage}%",
                    color = KashuTheme.colors.subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Target: ${goal.deadline}",
                    color = KashuTheme.colors.subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botón interactivo de Abono Rápido ("Abonar dinero a esta meta")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(KashuTheme.colors.mainColor.copy(alpha = 0.12f))
                    .clickable { onAddSavingsClick() }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = KashuTheme.colors.mainColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (goal.isTargetReached) "Seguir abonando" else "+ Abonar a la meta",
                        color = KashuTheme.colors.mainColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
