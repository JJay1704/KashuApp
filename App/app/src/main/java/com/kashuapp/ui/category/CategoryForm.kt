package com.kashuapp.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    onSave: (name: String, type: String) -> Unit,
    onDelete: ((categoryId: String) -> Unit)? = null
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember(categoryToEdit) { mutableStateOf(categoryToEdit?.name ?: "") }
    var selectedType by remember(categoryToEdit) { mutableStateOf(categoryToEdit?.type ?: "EXPENSE") }

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
                    onClickFun = { selectedType = "EXPENSE" }
                )
                KashuButton(
                    modifier = Modifier.weight(1f),
                    text = "Ingreso",
                    height = 40,
                    rounded = 10,
                    backColor = if (selectedType.uppercase() == "INCOME") KashuTheme.colors.mainColor else Color.Transparent,
                    textColor = if (selectedType.uppercase() == "INCOME") Color.Black else KashuTheme.colors.subtitle,
                    onClickFun = { selectedType = "INCOME" }
                )
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

            if (!errorMessage.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            KashuButton(
                modifier = Modifier.fillMaxWidth(),
                text = if (categoryToEdit == null) "Crear Categoría" else "Guardar Cambios",
                isLoading = isLoading,
                onClickFun = {
                    onSave(name, selectedType)
                }
            )

            if (categoryToEdit?.id != null && !categoryToEdit.isDefault && onDelete != null) {
                Spacer(modifier = Modifier.height(12.dp))
                KashuButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Eliminar Categoría",
                    backColor = Color(0xFFEF4444),
                    textColor = Color.White,
                    onClickFun = {
                        onDelete(categoryToEdit.id)
                        onDismiss()
                    }
                )
            }
        }
    }
}
