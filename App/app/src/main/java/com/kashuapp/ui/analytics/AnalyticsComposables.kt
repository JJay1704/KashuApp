package com.kashuapp.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashuapp.ui.theme.KashuTheme

private val chartPalette = listOf(
    Color(0xFF34D399),
    Color(0xFF3B82F6),
    Color(0xFFF59E0B),
    Color(0xFFEF4444),
    Color(0xFF8B5CF6),
    Color(0xFFEC4899)
)

@Composable
fun MetricSummaryCards(
    totalIncome: Double,
    totalExpense: Double,
    netSavings: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(KashuTheme.colors.surface)
                .padding(12.dp)
        ) {
            Column {
                Text(text = "Ingresos", fontSize = 12.sp, color = KashuTheme.colors.subtitle)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "S/. $totalIncome",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(KashuTheme.colors.surface)
                .padding(12.dp)
        ) {
            Column {
                Text(text = "Gastos", fontSize = 12.sp, color = KashuTheme.colors.subtitle)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "S/. $totalExpense",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(KashuTheme.colors.surface)
                .padding(12.dp)
        ) {
            Column {
                Text(text = "Neto", fontSize = 12.sp, color = KashuTheme.colors.subtitle)
                Spacer(modifier = Modifier.height(4.dp))
                val netColor = if (netSavings >= 0.0) Color(0xFF10B981) else Color(0xFFEF4444)
                Text(
                    text = "S/. $netSavings",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = netColor
                )
            }
        }
    }
}

@Composable
fun PieChartExpenses(
    shares: List<CategoryExpenseShare>,
    totalExpense: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(KashuTheme.colors.surface)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Distribución de Gastos por Categoría",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = KashuTheme.colors.title
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    var currentStartAngle = -90f
                    val strokeWidth = 36.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
                    val arcSize = Size(diameter, diameter)

                    shares.forEachIndexed { index, share ->
                        val sliceColor = chartPalette[index % chartPalette.size]
                        drawArc(
                            color = sliceColor,
                            startAngle = currentStartAngle,
                            sweepAngle = share.sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )
                        currentStartAngle += share.sweepAngle
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Total Gastos",
                        fontSize = 11.sp,
                        color = KashuTheme.colors.subtitle
                    )
                    Text(
                        text = "S/. $totalExpense",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = KashuTheme.colors.title
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                shares.forEachIndexed { index, share ->
                    val sliceColor = chartPalette[index % chartPalette.size]
                    val pctInt = share.percentage.toInt()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(sliceColor)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = share.categoryName,
                            fontSize = 14.sp,
                            color = KashuTheme.colors.title,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "S/. ${share.amount} ($pctInt%)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KashuTheme.colors.title
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComparisonBarChart(
    income: Double,
    expense: Double,
    modifier: Modifier = Modifier
) {
    val maxVal = if (income >= expense && income > 0.0) income else if (expense > 0.0) expense else 1.0
    val rawIncomeFraction = (income / maxVal).toFloat()
    val rawExpenseFraction = (expense / maxVal).toFloat()
    val incomeHeightFraction = if (rawIncomeFraction < 0.05f) 0.05f else if (rawIncomeFraction > 1.0f) 1.0f else rawIncomeFraction
    val expenseHeightFraction = if (rawExpenseFraction < 0.05f) 0.05f else if (rawExpenseFraction > 1.0f) 1.0f else rawExpenseFraction

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(KashuTheme.colors.surface)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Comparativa Mensual: Ingresos vs. Gastos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = KashuTheme.colors.title
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "S/. $income",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height((140 * incomeHeightFraction).dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ingresos",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = KashuTheme.colors.title
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "S/. $expense",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height((140 * expenseHeightFraction).dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(Color(0xFFEF4444))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Gastos",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = KashuTheme.colors.title
                    )
                }
            }
        }
    }
}
