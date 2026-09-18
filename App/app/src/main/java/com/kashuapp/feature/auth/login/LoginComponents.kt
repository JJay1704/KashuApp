package com.kashuapp.feature.auth.login



import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashuapp.R
import com.kashuapp.ui.theme.KashuTheme

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
fun KashuTextField(
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
fun KashuButton(
    text: String = "",
    onClickFun: () -> Unit,
    modifier: Modifier = Modifier,
    height: Int = 50,
    rounded: Int = 12,
    textSize : Int = 16
) {
    Button(
        onClick = onClickFun,
        modifier = modifier.height(height.dp),
        shape = RoundedCornerShape(rounded.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = KashuTheme.colors.mainColor,
            contentColor = Color.Black
        )
    ) {

        Text(
            text = text,
            fontSize = textSize.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun KashuSocialButton(

    onClickFun: () -> Unit,
    @DrawableRes iconRes: Int = R.drawable.ic_google,
    contentDescription: String = "Social Login",
    width : Int = 100,
    height: Int = 48,
    rounded: Int = 12,
    colorLogo : Color = Color.Unspecified,
    logoSize : Int = 20

 ){

    OutlinedButton(
        onClick = onClickFun,
        modifier = Modifier
            .width(width.dp)
            .height(height.dp),
        shape = RoundedCornerShape(rounded.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = KashuTheme.colors.surface,
            contentColor = KashuTheme.colors.surface
        ),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.outline)
    ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                tint = colorLogo   ,
                modifier = Modifier.size(logoSize.dp)
            )
        }
}
@Composable
fun Subtitle(text: String, subtitleColor: Color ){
    Text(
        text = text,
        color = subtitleColor,
        fontSize = 15.sp
    )
}


