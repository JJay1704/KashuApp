package com.kashuapp.ui.menu

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kashuapp.ui.theme.KashuTheme

@Composable
fun KashuitemMenu(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = title
            )
        },
        label = {
            Text(text = title)
        },
        selected = isSelected,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = KashuTheme.colors.mainColor.copy(alpha = 0.15f),
            selectedIconColor = KashuTheme.colors.mainColor,
            selectedTextColor = KashuTheme.colors.mainColor,
            unselectedIconColor = KashuTheme.colors.subtitle,
            unselectedTextColor = KashuTheme.colors.title
        ),
        onClick = onClick,
        modifier = modifier.padding(vertical = 4.dp)
    )
}