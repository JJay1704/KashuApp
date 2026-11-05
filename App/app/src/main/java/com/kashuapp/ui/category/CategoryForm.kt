package com.kashuapp.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuTextField
import com.kashuapp.data.category.Category
import com.kashuapp.ui.theme.KashuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryForm(
    categoryToEdit: Category?,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, type: String, icon: String, color: String) -> Unit,
    onDelete: ((categoryId: String) -> Unit)? = null
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember(categoryToEdit) { mutableStateOf(categoryToEdit?.name ?: "") }
    var selectedIcon by remember(categoryToEdit) {
        mutableStateOf(categoryToEdit?.icon ?: "category")
    }
    var selectedColor by remember(categoryToEdit) {
        mutableStateOf(categoryToEdit?.color ?: "#34D399")
    }
    var selectedType by remember(categoryToEdit) {
        mutableStateOf(
            categoryToEdit?.type ?: "EXPENSE"
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = KashuTheme.colors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (categoryToEdit == null) "Nueva Categoría" else "Editar Categoría",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = KashuTheme.colors.title
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(KashuTheme.colors.mainBackground)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KashuButton(
                    modifier = Modifier.weight(1f),
                    text = "Gasto",
                    height = 40,
                    rounded = 10,
                    backColor = if (selectedType.uppercase() == "EXPENSE") Color(0xFFEF4444) else Color.Transparent,
                    textColor = if (selectedType.uppercase() == "EXPENSE") Color.White else KashuTheme.colors.subtitle,
                    onClickFun = { selectedType = "EXPENSE" })
                KashuButton(
                    modifier = Modifier.weight(1f),
                    text = "Ingreso",
                    height = 40,
                    rounded = 10,
                    backColor = if (selectedType.uppercase() == "INCOME") KashuTheme.colors.mainColor else Color.Transparent,
                    textColor = if (selectedType.uppercase() == "INCOME") Color.Black else KashuTheme.colors.subtitle,
                    onClickFun = { selectedType = "INCOME" })
            }

            Spacer(modifier = Modifier.height(16.dp))

            KashuTextField(
                label = "Nombre de categoría",
                type = name,
                onType = { name = it },
                placeholder = "Ej. Alimentación, Sueldo...",
                colorPlaceHolder = KashuTheme.colors.subtitle,
                trailingIcon = null
            )

            val availableColors = listOf(
                "Verde" to "#34D399",
                "Azul" to "#3B82F6",
                "Ámbar" to "#F59E0B",
                "Rojo" to "#EF4444",
                "Violeta" to "#8B5CF6"
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Color", color = KashuTheme.colors.title)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableColors.size) { index ->
                    val (_, hex) = availableColors[index]
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(categoryColor(hex))
                            .border(
                                3.dp, if (selectedColor.equals(
                                        hex, ignoreCase = true
                                    )
                                ) KashuTheme.colors.title else Color.Transparent, CircleShape
                            )
                            .clickable(enabled = !isLoading) {
                                selectedColor = hex
                            })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Ícono", color = KashuTheme.colors.title)
            val availableIcons = listOf(
                "category" to "General",
                "food" to "Alimentación",
                "transport" to "Transporte",
                "shopping" to "Compras"
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableIcons.size) { index ->
                    val (id, label) = availableIcons[index]
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(KashuTheme.colors.surface)
                            .border(
                                2.dp, if (selectedIcon == id) KashuTheme.colors.title
                                else Color.Transparent, CircleShape
                            )
                            .clickable(enabled = !isLoading) {
                                selectedIcon = id
                            }, contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon(id),
                            contentDescription = label,
                            tint = categoryColor(selectedColor)
                        )
                    }
                }
            }

            if (!errorMessage.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage, color = Color(0xFFEF4444), fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            KashuButton(
                modifier = Modifier.fillMaxWidth(),
                text = if (categoryToEdit == null) "Create Category" else "Save",
                isLoading = isLoading,
                onClickFun = {
                    onSave(name, selectedType, selectedIcon, selectedColor)
                })

            if (categoryToEdit?.id != null && !categoryToEdit.isDefault && onDelete != null) {
                Spacer(modifier = Modifier.height(12.dp))
                KashuButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Delete Category",
                    backColor = Color(0xFFEF4444),
                    textColor = Color.White,
                    onClickFun = {
                        onDelete(categoryToEdit.id)
                        onDismiss()
                    })
            }
        }
    }
}
