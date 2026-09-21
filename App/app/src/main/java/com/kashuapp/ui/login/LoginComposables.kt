package com.kashuapp.ui.login



import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashuapp.R
import com.kashuapp.ui.theme.KashuTheme




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


