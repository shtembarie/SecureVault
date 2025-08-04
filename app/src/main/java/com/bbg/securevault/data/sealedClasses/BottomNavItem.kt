package com.bbg.securevault.data.sealedClasses

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.bbg.securevault.R

/**
 * Created by Enoklit on 12.06.2025.
 */

sealed class BottomNavItem(
    val route: String,
    @SuppressLint("SupportAnnotationUsage") @StringRes val label: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Passwords : BottomNavItem("passwords", R.string.passwords, Icons.Default.Home)
    object Generator : BottomNavItem("generator", R.string.generator, Icons.Default.Key)
    object Settings : BottomNavItem("settings", R.string.settings, Icons.Default.Settings)
}



