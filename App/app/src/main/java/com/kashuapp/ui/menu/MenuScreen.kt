package com.kashuapp.ui.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashuapp.core.composables.KashuMenuButton
import com.kashuapp.core.navBar.MenuTab


import com.kashuapp.ui.theme.KashuTheme

@Composable
fun KashuHomeMenu(
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    onNavigateToCategory: () -> Unit = {},
    onTabSelected: (String) -> Unit = {},
    viewModel: MenuVM = viewModel()


) {


    var isMenuOpen by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf<MenuTab?>(null) }
    val userTexts = viewModel.getUserTexts()
    val userName = userTexts[0]
    val userEmail = userTexts[2]
    val initials = userTexts[1]

    KashuMenuButton(
        initials = initials,
        size = size,
        modifier = modifier,
        onClick = { isMenuOpen = true }
    )
    if (isMenuOpen) {
        Dialog(
            onDismissRequest = { isMenuOpen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isMenuOpen = false }
            ) {
                AnimatedVisibility(
                    visible = isMenuOpen,
                    enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    ModalDrawerSheet(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(200.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { },
                        drawerContainerColor = KashuTheme.colors.surface,
                        drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Kashu Menu",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KashuTheme.colors.title
                                )
                                IconButton(onClick = { isMenuOpen = false }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Cerrar menú",
                                        tint = KashuTheme.colors.subtitle
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                KashuMenuButton(
                                    initials = initials,
                                    size = 50.dp,
                                    onClick = {}
                                )
                                Column {
                                    Text(
                                        text = userName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KashuTheme.colors.title
                                    )

                                }


                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = userEmail,
                                fontSize = 13.sp,
                                color = KashuTheme.colors.subtitle
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = KashuTheme.colors.subtitle.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(16.dp))




                            MenuTab.tabs.forEach {tab -> KashuitemMenu(
                                title = tab.title,
                                icon = tab.icon,
                                isSelected = selectedTab== tab,
                                onClick = {

                                    selectedTab = tab
                                    isMenuOpen = false
                                    when (tab){

                                        is MenuTab.Categories -> onNavigateToCategory()
                                        is MenuTab.Profile -> { }
                                        is MenuTab.Settings -> { }
                                    }
                                }
                            )}







                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = KashuTheme.colors.subtitle.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))

                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        Icons.Default.ExitToApp,
                                        contentDescription = "Cerrar Sesión"
                                    )
                                },
                                label = { Text("Cerrar Sesión") },
                                selected = false,
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedIconColor = Color(0xFFEF4444),
                                    unselectedTextColor = Color(0xFFEF4444)
                                ),
                                onClick = {
                                    isMenuOpen = false
                                    onTabSelected("Cerrar Sesión")
                                },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}



