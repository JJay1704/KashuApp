package com.kashuapp.ui.category

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryVisuals {
    val icons = linkedMapOf(
        "category" to Icons.Default.Category,
        "restaurant" to Icons.Default.Restaurant,
        "shopping_cart" to Icons.Default.ShoppingCart,
        "directions_bus" to Icons.Default.DirectionsBus,
        "home" to Icons.Default.Home,
        "medical_services" to Icons.Default.MedicalServices,
        "school" to Icons.Default.School,
        "sports_esports" to Icons.Default.SportsEsports,
        "work" to Icons.Default.Work,
        "savings" to Icons.Default.Savings
    )
    val colors = listOf("#34D399", "#3B82F6", "#A855F7", "#F59E0B", "#EF4444", "#EC4899")
    fun icon(name: String?): ImageVector = icons[name] ?: Icons.Default.Category
    fun color(hex: String?): Color = runCatching {
        Color(android.graphics.Color.parseColor(hex))
    }.getOrDefault(Color(0xFF34D399))
}
