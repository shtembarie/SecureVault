package com.bbg.securevault.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.bbg.securevault.R
import com.bbg.securevault.presentation.core.PasswordGenerator
/**
 * Created by Enoklit on 04.06.2025.
 */
@Composable
fun PasswordStrengthIndicator(password: String) {
    val strength = PasswordGenerator.calculatePasswordStrength(password)

    val strengthColor = when (strength) {
        "strong" -> colorResource(R.color.strengthStrong)
        "medium" -> colorResource(R.color.strengthMedium)
        else -> colorResource(R.color.strengthWeak)
    }

    val strengthFraction = when (strength) {
        "strong" -> 1f
        "medium" -> 0.66f
        else -> 0.33f
    }

    val barHeight = 6.dp
    val barCornerRadius = 3.dp

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .background(
                    color = colorResource(R.color.border),
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(strengthFraction)
                    .background(strengthColor, shape = MaterialTheme.shapes.small)
            )
        }

        Text(
            text = strength.replaceFirstChar { it.uppercaseChar() },
            color = strengthColor,
            fontSize = MaterialTheme.typography.labelSmall.fontSize,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            textAlign = androidx.compose.ui.text.style.TextAlign.Right
        )
    }
}
