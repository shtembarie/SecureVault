package com.bbg.securevault.shared.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bbg.securevault.data.models.NotificationType

@Composable
fun NotificationBanner(
    type: NotificationType,
    title: String,
    message: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, backgroundColor, iconColor) = when (type) {
        NotificationType.SUCCESS -> Triple(Icons.Default.CheckCircle, Color(0xFF2E7D32), Color.White)
        NotificationType.INFO -> Triple(Icons.Default.Info, Color(0xFF0277BD), Color.White)
        NotificationType.WARNING -> Triple(Icons.Default.Warning, Color(0xFFF9A825), Color.Black)
        NotificationType.ERROR -> Triple(Icons.Default.Error, Color(0xFFD32F2F), Color.White)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    color = iconColor,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = message,
                    color = iconColor.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = iconColor,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onClose() }
            )
        }
    }
}