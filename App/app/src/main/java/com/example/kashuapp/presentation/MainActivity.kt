package com.example.kashuapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.kashuapp.domain.usecase.UserLogic
import com.example.kashuapp.ui.theme.KashuAppTheme
import com.example.kashuapp.core.navigation.Navigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KashuAppTheme {
                var selectedTab by remember { mutableIntStateOf(0) }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {

                        Navigation()


//                        NavigationBar {
//                            NavigationBarItem(
//                                selected = selectedTab == 0,
//                                onClick = { selectedTab = 0 },
//                                icon = { Text("1") },
//                                label = { Text("Inicio") }
//                            )
//                            NavigationBarItem(
//                                selected = selectedTab == 1,
//                                onClick = { selectedTab = 1 },
//                                icon = { Text("2") },
//                                label = { Text("xd") }
//                            )
//                            NavigationBarItem(
//                                selected = selectedTab == 3,
//                                onClick = { selectedTab = 3 },
//                                icon = { Text("3") },
//                                label = { Text("xadsfad") }
//                            )
//                        }
                    }
                ) { innerPadding ->

                    val xd = UserLogic()

                    Column(

                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()

                    ) {
                        Text("xddx")

                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        ) }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KashuAppTheme {
        Greeting("Android")
    }
}
