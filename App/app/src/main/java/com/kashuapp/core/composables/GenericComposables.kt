package com.kashuapp.core.composables


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        contentPadding = PaddingValues(0.dp), // <-- AGREGA ESTO PARA ELIMINAR EL PADDING POR DEFECTO

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


