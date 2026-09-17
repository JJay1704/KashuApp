package com.kashuapp.feature.auth.login


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashuapp.ui.theme.KashuTheme

@Composable
fun LogoText(
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
fun CustomTextField(
    label  : String = "",
    sizeLabel : Int = 14,
    extraLabel : String = "",
    onExtraClick: () -> Unit = {},
    type : String ="",
    onType : (String) -> Unit,
    placeholder : String = "",
    colorPlaceHolder : Color = Color.Black,
    iconInput: ImageVector = Icons.Default.Email,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null // 👈 SOLO AGREGA ESTA LÍNEA
) {
    Column(
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if(label.isNotBlank()){
                Text(
                    fontSize = sizeLabel.sp,
                    text = label,
                    color = KashuTheme.colors.subtitle
                )
            }
            if (extraLabel.isNotBlank()){
                Text(
                    fontSize = sizeLabel.sp,
                    text = extraLabel,
                    color = KashuTheme.colors.mainColor,
                    modifier = Modifier.clickable { onExtraClick() } // 👈 Le agregamos esto
                )
            }
        }
         OutlinedTextField(
        value = type,
             onValueChange = onType ,
        placeholder = { Text(placeholder , color = colorPlaceHolder ) },
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
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = KashuTheme.colors.mainColor,
            unfocusedBorderColor = Color.Black,
            focusedTextColor = KashuTheme.colors.subtitle,
            unfocusedTextColor = KashuTheme.colors.subtitle
        )

        )
    }
    }


