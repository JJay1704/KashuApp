package com.example.kashuapp.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen() {
    // Variables locales temporales SOLO para que puedas escribir en la vista previa
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }


    Scaffold(

        modifier = Modifier


    ) { innerPadding ->


        Box(

            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFC8C6C5))

                .padding(innerPadding)
        ){

            Text(
                text = "Kashu",
                style = MaterialTheme.typography.headlineLarge, // Le da estilo de título grande
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(Color(0xFFF3F0EF))


                    .padding(top = 24.dp, start = 24.dp, end = 24.dp)
            )


        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),

//        horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centrado verticalmente
        ){




            Text("Email")
            InputEmail()
            Spacer(modifier =   Modifier.height(20.dp))
            Text("Password")
            InputPswd()




        }

    }






}


@Composable
fun InputEmail(){
    var textState by remember { mutableStateOf("") }
    OutlinedTextField(
        value = textState,
        onValueChange ={newText -> textState = newText},
        placeholder = {Text("User@email.com")},
        modifier = Modifier.fillMaxWidth()
    )
}
@Composable
fun InputPswd() {
    var textState by remember { mutableStateOf("") }
    OutlinedTextField(
        value = textState,
        onValueChange = { newText -> textState = newText },
        placeholder = { Text("*****") },
                modifier = Modifier.fillMaxWidth(),

    )
}


    // 8. LA VISTA PREVIA (Para verlo en Android Studio a la derecha)
    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun LoginScreenPreview() {
        MaterialTheme {
            LoginScreen()
        }
    }
