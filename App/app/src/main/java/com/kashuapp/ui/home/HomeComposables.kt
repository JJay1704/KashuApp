package com.kashuapp.ui.home


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow

import androidx.compose.ui.unit.dp

import androidx.compose.ui.window.Popup
import com.kashuapp.ui.theme.KashuTheme

@Composable
fun TransactionDialog(

   onDismiss : () -> Unit,
   onSave: ()-> Unit
){

    Popup (

        onDismissRequest = {onDismiss},


    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .shadow(12.dp, shape = RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = KashuTheme.colors.surface
            )) {

                Text("xddx")
            }

    }

}