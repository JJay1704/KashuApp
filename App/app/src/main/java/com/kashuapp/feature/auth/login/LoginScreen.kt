package com.kashuapp.feature.auth.login

import android.content.Context
import android.credentials.CredentialManager
import android.credentials.GetCredentialRequest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashuapp.R
import com.kashuapp.ui.theme.KashuDarkBackground
import com.kashuapp.ui.theme.KashuDarkSubtitle
import com.kashuapp.ui.theme.KashuDarkSurface
import com.kashuapp.ui.theme.KashuDarkTitle
import com.kashuapp.ui.theme.KashuGreenPrimary
import com.kashuapp.ui.theme.KashuLightBackground
import com.kashuapp.ui.theme.KashuLightSubtitle
import com.kashuapp.ui.theme.KashuLightSurface
import com.kashuapp.ui.theme.KashuLightTitle
import kotlinx.coroutines.launch
import org.w3c.dom.Text

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
//            email: String,
//    password: String,
//    passwordVisible: Boolean,
//    onEmailChange: (String) -> Unit,
//    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit = {},
//    onLoginClick: () -> Unit,
//    onBiometricClick: () -> Unit,
//    onForgotPasswordClick: () -> Unit,
//    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) KashuDarkBackground else KashuLightBackground
    val inputSurfaceColor = if (isDark) KashuDarkSurface else KashuLightSurface
    val textPrimary = if (isDark) KashuDarkTitle else KashuLightTitle
    val subtitleColor = if (isDark) KashuDarkSubtitle else KashuLightSubtitle

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
       Spacer(modifier = Modifier.weight(0.2f))

        Surface(
            modifier = Modifier.padding(top = 40.dp),

            shape = RoundedCornerShape(4.dp),
            color = if (isDark) Color(0xFF24262B) else Color.White,
            border = BorderStroke(
                width = 1.dp,
                color = if (isDark) Color(0xFF37393F) else Color(0xFFE0E0E0)
            ),
            shadowElevation = 1.dp
        ) {
            Text(
                text = "KASHU",
                color = KashuGreenPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        // 2. Títulos
        Text(
            text = "Welcome to Kashu",
            color = textPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        Subtitle("Log in to continue", subtitleColor)


        Spacer(modifier = Modifier.weight(0.9f))
        // 3. Campo de Correo Electrónico
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Email",
                color = textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("user@email.com", color = Color.Gray) },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Email", tint = Color.Gray)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputSurfaceColor,
                unfocusedContainerColor = inputSurfaceColor,
                focusedBorderColor = KashuGreenPrimary,
                unfocusedBorderColor = if (isDark) Color(0xFF374151) else Color(0xFFD1D5DB),
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary
            )
        )
        Spacer(modifier = Modifier.height(14.dp))
        // 4. Campo de Contraseña y enlace de recuperación
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password",
                color = textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Forget your Password?",
                color = KashuGreenPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { /* Acción recuperar contraseña */ }
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("••••••••", color = Color.Gray) },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "password", tint = Color.Gray)


            },
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Color.Gray
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputSurfaceColor,
                unfocusedContainerColor = inputSurfaceColor,
                focusedBorderColor = KashuGreenPrimary,
                unfocusedBorderColor = if (isDark) Color(0xFF374151) else Color(0xFFD1D5DB),
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Button(
                onClick = onLoginSuccess,
                modifier = Modifier
                    .weight(0.8f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KashuGreenPrimary,
                    contentColor = if (isDark) Color.White else Color.Black
                )
            ) {
                Text(
                    text = "Log In →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            // 6. Botón Biometría

            OutlinedButton(
                onClick = { /* Acción huella digital */ },
                modifier = Modifier
                    .weight(0.2f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = inputSurfaceColor,
                    contentColor = textPrimary
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (isDark) Color(0xFF37393F) else Color(0xFFE0E0E0)
                ),

                ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Biometric Login",
                    tint = textPrimary
                )

            }


        }


        Spacer(modifier = Modifier.height(20.dp))

        Row (modifier =  Modifier.fillMaxWidth())
        {
            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .width(100.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = inputSurfaceColor,
                    contentColor = textPrimary
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (isDark) Color(0xFF37393F) else Color(0xFFE0E0E0)
                ),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
//                Spacer(modifier = Modifier.width(10.dp))
//                Text(
//                    text = "Continue with Google",
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Medium
//                )
            }
        }





        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't Have a Account? ",
                color = subtitleColor,
                fontSize = 14.sp
            )
            Text(
                text = "Sign Up",
                color = KashuGreenPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { /* Navegar a registro */ }
            )



        }







        Spacer(modifier = Modifier.weight(0.2f))
    }
}


@Composable
fun Subtitle(Text: String, SubtitleColor: Color ){

    Text(
        text = Text,
        color = SubtitleColor,
        fontSize = 15.sp
    )
}


//@Composable
//fun InputField(
//
//
//    Label: String,
//    LabelColor: Color,
//    PlaceHolderText: String,
//    PlaceHolderColor: Color,
//
//    ) {
//    var password by remember { mutableStateOf("") }
//
//    Column(
//
//    ) {
//
//        Row(
//
//
//        ) {
//
//
//            Text(
//                text = Label,
//                color = LabelColor,
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Medium
//            )
//            Text(
//                text = "xd",
//                color = KashuGreenPrimary,
//                fontSize = 13.sp,
//                fontWeight = FontWeight.Medium,
//                modifier = Modifier.clickable { /* Acción recuperar contraseña */ }
//            )
//
//
//        }
//
//        Spacer(modifier = Modifier.height(6.dp))
//
//        OutlinedTextField(
//
//            value = password,
//            onValueChange = { password = it },
//            placeholder = { Text("••••••••", color = Color.Gray) },
//            leadingIcon = {
//                Icon(Icons.Default.Lock, contentDescription = "password", tint = Color.Gray)
//
//
//            }
//
//
//    }
//







