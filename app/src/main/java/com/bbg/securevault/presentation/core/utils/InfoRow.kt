package com.bbg.securevault.presentation.core.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Created by Enoklit on 13.06.2025.
 */

@Composable
fun InfoRow(
    label: String,
    value: String,
    copyable: Boolean = false,
    toggleable: Boolean = false,
    clickable: Boolean = false,
    onClick: (() -> Unit)? = null,
    onToggle: (() -> Unit)? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    toggleIcon: ImageVector? = null,
    actualCopyValue: String? = null
) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (clickable && onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
        if (toggleable && onToggle != null) {
            IconButton(onClick = onToggle) {
                // Use toggleIcon if available, otherwise default
                Icon(toggleIcon ?: Icons.Default.Visibility, contentDescription = "Toggle Visibility")
            }
        }
        if (copyable) {
            IconButton(onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                //  Use actualCopyValue if provided, otherwise fall back to visible value
                val textToCopy = actualCopyValue ?: value

                clipboard.setPrimaryClip(ClipData.newPlainText(label, textToCopy))
                Toast.makeText(context, "$label copied", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy $label")
            }
        }

        if (trailingIcon != null && onTrailingIconClick != null) {
            IconButton(onClick = onTrailingIconClick) {
                Icon(trailingIcon, contentDescription = "Open $label")
            }
        }


    }
}