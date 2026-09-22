package com.kashuapp.ui.addCategory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuTextField
import com.kashuapp.ui.theme.KashuTheme

@Composable
fun AddCategoryScreen(
    onBackToHome: () -> Unit,
    viewModel: AddCategoryVM = viewModel()

) {
    val uiState by viewModel.uiState.collectAsState()


    var selectedColorHex by remember { mutableStateOf("#34D399") }
    var selectedIcon by remember { mutableStateOf(Icons.Default.Restaurant) }

    val colors = remember {
        listOf(
            "#34D399", "#10B981", "#3B82F6", "#6366F1",
            "#8B5CF6", "#EC4899", "#EF4444", "#F97316",
            "#F59E0B", "#14B8A6", "#64748B"
        )
    }

    val icons = remember {
        listOf(
            Icons.Default.Restaurant,
            Icons.Default.ShoppingCart,
            Icons.Default.DirectionsCar,
            Icons.Default.Home,
            Icons.Default.Work,
            Icons.Default.Savings,
            Icons.Default.LocalHospital,
            Icons.Default.School,
            Icons.Default.FitnessCenter,
            Icons.Default.Category
        )
    }

    val currentColor = remember(selectedColorHex) {
        try {
            val clean = selectedColorHex.removePrefix("#")
            val colorInt = if (clean.length == 6) (0xFF000000 or clean.toLong(16)).toInt()
            else clean.toLong(16).toInt()
            Color(colorInt)
        } catch (e: Exception) {
            Color(0xFF34D399)
        }
    }

    Scaffold(
        containerColor = KashuTheme.colors.mainBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToHome) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = KashuTheme.colors.title
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nueva Categoría",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = KashuTheme.colors.title
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = KashuTheme.colors.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(currentColor.copy(alpha = 0.18f))
                            .border(2.dp, currentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = selectedIcon,
                            contentDescription = null,
                            tint = currentColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = uiState.name.ifBlank { "Nombre de la categoría" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.name.isNotBlank()) KashuTheme.colors.title else KashuTheme.colors.subtitle
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (uiState.type == "EXPENSE") Color(0xFFEF4444).copy(alpha = 0.12f)
                        else KashuTheme.colors.mainColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (uiState.type == "EXPENSE") "Gasto" else "Ingreso",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (uiState.type == "EXPENSE") Color(0xFFEF4444) else KashuTheme.colors.mainColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }


            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Tipo de Categoría",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KashuTheme.colors.title
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = uiState.type == "EXPENSE",
                        onClick = { viewModel.onType("EXPENSE") },
                        label = { Text("Gasto") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFEF4444).copy(alpha = 0.15f),
                            selectedLabelColor = Color(0xFFEF4444),
                            containerColor = KashuTheme.colors.surface,
                            labelColor = KashuTheme.colors.subtitle
                        )
                    )

                    FilterChip(
                        selected = uiState.type == "INCOME",
                        onClick = { viewModel.onType("INCOME") },
                        label = { Text("Ingreso") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KashuTheme.colors.mainColor.copy(alpha = 0.15f),
                            selectedLabelColor = KashuTheme.colors.mainColor,
                            containerColor = KashuTheme.colors.surface,
                            labelColor = KashuTheme.colors.subtitle
                        )
                    )
                }
            }


            KashuTextField(
                label = "Nombre",
                placeholder = "Ej: Alimentación, Salario, Transporte...",
                type = uiState.name,
                onType = { viewModel.onName(it) },
                iconInput = Icons.Default.Category,
                trailingIcon = null
            )

            // Selector de Color
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Color",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KashuTheme.colors.title
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(colors) { hexColor ->
                        val itemColor = try {
                            val clean = hexColor.removePrefix("#")
                            val colorInt =
                                if (clean.length == 6) (0xFF000000 or clean.toLong(16)).toInt()
                                else clean.toLong(16).toInt()
                            Color(colorInt)
                        } catch (e: Exception) {
                            Color(0xFF34D399)
                        }
                        val isSelected = selectedColorHex.equals(hexColor, ignoreCase = true)

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(itemColor)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) KashuTheme.colors.title else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorHex = hexColor },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }


            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Icono",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KashuTheme.colors.title
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(icons) { iconVector ->
                        val isSelected = selectedIcon == iconVector
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) currentColor.copy(alpha = 0.2f)
                                    else KashuTheme.colors.surface
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) currentColor else KashuTheme.colors.subtitle.copy(
                                        alpha = 0.2f
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedIcon = iconVector },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = if (isSelected) currentColor else KashuTheme.colors.subtitle,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))


            KashuButton(
                text = "Crear Categoría",
                backColor = KashuTheme.colors.mainColor,
                textColor = Color.White,
                onClickFun = {
                    viewModel.postCategory()
                    onBackToHome()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}