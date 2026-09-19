package com.kashuapp.ui.home.view

import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.core.composables.KashuLogo
import com.kashuapp.ui.home.TransactionDialog
import com.kashuapp.ui.theme.KashuTheme
@Composable
fun HomeScreen(){

    var mostrarDialogo by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KashuTheme.colors.mainBackground)
            .padding(horizontal = 24.dp, vertical = 24.dp),

    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,) {

            Box(){
                KashuLogo(fontsize = 20,
                    horizontalPadding = 0,
                    verticalPadding = 0,
                    )
            }
            KashuButton(
                onClickFun = {mostrarDialogo = true},
                text = "+"
            )

        }

        if (mostrarDialogo){

            TransactionDialog(

                onDismiss = {mostrarDialogo = false},
                onSave = {mostrarDialogo =  false}

            )

        }


    }
    }

