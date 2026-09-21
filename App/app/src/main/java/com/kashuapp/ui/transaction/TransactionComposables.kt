package com.kashuapp.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashuapp.core.composables.KashuButton
import com.kashuapp.ui.theme.KashuTheme


@Composable

fun CardAddTran(){


    Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),


    ) {

        Row (
            modifier =  Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween



        ) {
            Box{
                Column {

                    Text(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        text = "Add Transaction")
                    Text("xdxd")

                }


            }


            KashuButton(

                modifier = Modifier.size(30.dp),
                rounded =10,
                onClickFun = {},


                text = "X"
                )


        }


        Row(
            modifier =  Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center

        ) {


            KashuButton(

                modifier = Modifier.weight(0.4f),
                rounded = 6,
                onClickFun = {},
                backColor = Color.Black,
                text = "Gasto",
                textColor = Color.White   )
            KashuButton(
                modifier = Modifier.weight(0.4f),
                rounded = 6,
                onClickFun = {},
                backColor = Color.White,
                text = "Ingreso",)



        }
        Spacer(Modifier.padding(10.dp))
        BalanceContainer(modifier = Modifier.padding(20.dp))



        Container(modifier = Modifier.padding(20.dp))
        Container(modifier = Modifier.padding(20.dp))
        Container(modifier = Modifier.padding(20.dp))







    }






}


@Composable
fun Container(modifier: Modifier){

    Box{
        Modifier.background(color = Color.Gray)
            .padding(30.dp)

        Column {
            Row { }
            Row { }

        }
    }


}

@Composable
fun BalanceContainer(modifier: Modifier){



    var amount by remember { mutableStateOf("") }
    Box(

        Modifier.background(color = KashuTheme.colors.surface)
            .padding(30.dp)
            .fillMaxWidth(),

        contentAlignment = Alignment.Center
    ){
        Column{
            Text(
                color = KashuTheme.colors.subtitle,
                text= "Monto a registrar",
                 )


            Box{

                Row {

                    Text(text= "S/.", fontSize = 40.sp)
                    OutlinedTextField(
                            value = amount,
                    onValueChange = {amount = it},
                    placeholder ={Text("0.00")}

                    )


                }


            }

        }








    }

}