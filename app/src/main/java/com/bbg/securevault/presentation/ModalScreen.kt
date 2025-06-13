package com.bbg.securevault.presentation

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.activity.ComponentActivity
/**
 * Created by Enoklit on 05.06.2025.
 */

@Composable
fun ModalScreen() {
    val context = LocalContext.current

    // Set status bar style for iOS-like appearance
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && context is ComponentActivity) {
        WindowCompat.setDecorFitsSystemWindows(context.window, false)
        val insetsController = WindowInsetsControllerCompat(context.window, context.window.decorView)
        insetsController.isAppearanceLightStatusBars = false // dark content on light background
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Modal",
                fontSize = 20.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .height(1.dp)
                    .width(250.dp)
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "This is an example modal. You can edit it in app/modal.tsx.",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}