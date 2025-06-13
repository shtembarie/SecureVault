package com.bbg.securevault.domain.sealedClasses

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Settings

/**
 * Created by Enoklit on 12.06.2025.
 */

sealed class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Passwords : BottomNavItem("passwords", "Passwords", Icons.Default.Home)
    object Generator : BottomNavItem("generator", "Generator", Icons.Default.Key)
    object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
}