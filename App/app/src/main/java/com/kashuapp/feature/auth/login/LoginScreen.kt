package com.kashuapp.feature.auth.login
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashuapp.R
import com.kashuapp.ui.theme.KashuTheme
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KashuTheme.colors.mainBackground)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
       Spacer(modifier = Modifier.weight(0.2f))

        Surface(
            modifier = Modifier.padding(top = 40.dp),

            shape = RoundedCornerShape(4.dp),
            color = KashuTheme.colors.surface ,
            border = BorderStroke(
                width = 1.dp,
                color = KashuTheme.colors.surface
            ),
            shadowElevation = 1.dp
        ) {
            LogoText(fontsize = 40)
        }








        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome to Kashu",
            color = KashuTheme.colors.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Subtitle("Log in to continue", KashuTheme.colors.subtitle)
        Spacer(modifier = Modifier.weight(0.9f))







        CustomTextField(
            label = "Email",
            type = email,
            onType = { email = it },
            placeholder = "user@email.com",
            iconInput = Icons.Default.Email,
            colorPlaceHolder = Color.Gray,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(14.dp))
        CustomTextField(
            label = "Password",
            extraLabel = "Forgot your password?",
            type = password,
            onType = { password = it },
            placeholder = "••••••••",
            iconInput = Icons.Default.Lock,
            colorPlaceHolder = Color.Gray,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),




            trailingIcon = {
                IconButton (onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Color.Gray
                    )
                }
            }
            )
        Spacer(modifier = Modifier.height(14.dp))

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
                    containerColor = KashuTheme.colors.mainColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary
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
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline
                ),

                ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Biometric Login",
                    tint = MaterialTheme.colorScheme.onSurface
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
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline
                ),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )

            }





        }





        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't Have a Account? ",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
            Text(
                text = "Sign Up",
                color = KashuTheme.colors.mainColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { /* Navegar a registro */ }
            )



        }







        Spacer(modifier = Modifier.weight(0.2f))
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



//
//@Composable
//fun PlatformButton(){
//
//    OutlinedButton(
//        onClick = {},
//        modifier = Modifier
//            .width(100.dp)
//            .height(48.dp),
//        shape = RoundedCornerShape(12.dp),
//        colors = ButtonDefaults.outlinedButtonColors(
//            containerColor = inputSurfaceColor,
//            contentColor = textPrimary
//        ),
//        border = BorderStroke(
//            width = 2.dp,
//            color = if (isDark) Color(0xFF37393F) else Color(0xFFE0E0E0)
//        ),
//    ) {
//        Icon(
//            painter = painterResource(id = R.drawable.ic_google),
//            contentDescription = "Google Logo",
//            tint = Color.Unspecified,
//            modifier = Modifier.size(20.dp)
//        )
//
//    }
//
//}
