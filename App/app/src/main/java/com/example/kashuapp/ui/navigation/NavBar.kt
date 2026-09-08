package com.example.kashuapp.ui.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun Navigation(){

    NavigationBar{
        var selectedTab by remember { mutableIntStateOf(0) }


        NavigationBarItem(

            selected = selectedTab  == 0,
            onClick = { selectedTab = 0 },
            icon = { Text("1") },
            label = { Text("Inicio") }

        )

        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            icon = { Text("1") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            icon = { Text("1") },
            label = { Text("Inicio") }
        )

    }

}
class NavBar {


}