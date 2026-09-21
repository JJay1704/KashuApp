package com.kashuapp.core.composables
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashuapp.ui.theme.KashuTheme
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.width

import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider

import androidx.compose.runtime.getValue

import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.draw.clip

@Composable
fun KashuLogo(
    modifier: Modifier = Modifier,
    fontsize : Int  = 14,
    horizontalPadding: Int = 20,
    verticalPadding: Int = 8
){
    Text(
        text = "KASHU",
        color = KashuTheme.colors.mainColor,
        fontWeight = FontWeight.Bold,
        fontSize = fontsize.sp,
        letterSpacing = 2.sp,
        modifier = modifier.padding(
            horizontal = horizontalPadding.dp,
            vertical = verticalPadding.dp
        )    )
}
@Composable
fun KashuButton(
    modifier: Modifier = Modifier,
    text: String = "",
    onClickFun: () -> Unit,
    backColor: Color = KashuTheme.colors.mainColor,
    height: Int = 50,
    rounded: Int = 12,
    textSize : Int = 16,
    textColor : Color = KashuTheme.colors.title
) {
    Button(

                onClick = onClickFun,
        modifier = modifier.height(height.dp),
        shape = RoundedCornerShape(rounded.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backColor,
            contentColor = Color.Black,
        ),
        contentPadding = PaddingValues(0.dp),

    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = text,
            fontSize = textSize.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}




@Composable
fun KashuTextField(
    modifier: Modifier = Modifier,
    label  : String = "",
    sizeLabel : Int = 14,
    extraLabel : String = "",
    onExtraClick: () -> Unit = {},
    type : String ="",
    onType : (String) -> Unit,
    placeholder : String = "",
    isError: Boolean = false,
    colorPlaceHolder : Color = Color.Black,
    iconInput: ImageVector = Icons.Default.Email,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)?
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if(label.isNotBlank()){
                Text(
                    fontSize = sizeLabel.sp,
                    text = label,
                    color = KashuTheme.colors.title
                )
            }
            if (extraLabel.isNotBlank()){
                Text(
                    fontSize = sizeLabel.sp,
                    text = extraLabel,
                    color = KashuTheme.colors.mainColor,
                    modifier = Modifier.clickable { onExtraClick() }, // 👈 Le agregamos esto
                    fontWeight = FontWeight.Bold
                )
            }
        }
        OutlinedTextField(


                    value = type,
            onValueChange = onType ,
            placeholder = { Text(placeholder , color = colorPlaceHolder ) },
            isError = isError,
            leadingIcon = {
                Icon(iconInput, contentDescription = label, tint = colorPlaceHolder)
            },
            trailingIcon = trailingIcon, // 👈 SOLO AGREGA ESTA LÍNEA

            visualTransformation = visualTransformation,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Unspecified ,
                unfocusedContainerColor = Color.Unspecified,
                focusedBorderColor = KashuTheme.colors.mainColor,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = KashuTheme.colors.title,
                unfocusedTextColor = colorPlaceHolder
            )

        )
    }
}




@Composable
fun <T> KashuDropdownField(
    modifier: Modifier = Modifier,
    label: String = "",
    selectedValue: String,
    placeholder: String = "Seleccionar $selectedValue",
    icon: ImageVector,
    items: List<T>,
    itemLabel: (T) -> String = { it.toString() },
    onItemSelected: (T) -> Unit,
    extraActionLabel: String? = null,
    onExtraActionClick: (() -> Unit)? = null
) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = KashuTheme.colors.title
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = if (selectedValue.isEmpty()) placeholder else selectedValue,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = if (selectedValue.isEmpty()) KashuTheme.colors.subtitle else KashuTheme.colors.title,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledContainerColor = Color.Transparent,
                    disabledLeadingIconColor = KashuTheme.colors.subtitle,
                    disabledTrailingIconColor = KashuTheme.colors.subtitle
                ),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(icon, contentDescription = label)
                },
                trailingIcon = {
                    IconButton (onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "Desplegar"
                        )
                    }
                }
            )
            // Capa invisible para capturar el clic en cualquier parte del campo
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { isExpanded = true }
            )
            // Menú flotante
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(KashuTheme.colors.surface)
            ) {
                items.forEach { item ->
                    val text = itemLabel(item)
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = text,
                                color = if (text == selectedValue) KashuTheme.colors.mainColor else KashuTheme.colors.title,
                                fontWeight = if (text == selectedValue) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onItemSelected(item)
                            isExpanded = false
                        }
                    )
                }
                // Opción extra opcional (ej: + Agregar nueva categoría)
                if (extraActionLabel != null && onExtraActionClick != null) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Agregar",
                                    tint = KashuTheme.colors.mainColor
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "+ $extraActionLabel",
                                    color = KashuTheme.colors.mainColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        onClick = {
                            isExpanded = false
                            onExtraActionClick()
                        }
                    )
                }
            }
        }
    }
}