package com.bbg.securevault.presentation.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bbg.securevault.R
import com.bbg.securevault.data.models.PasswordEntry
import com.bbg.securevault.domain.PasswordStore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
/**
 * Created by Enoklit on 04.06.2025.
 */

@Composable
fun PasswordListItem(
    password: PasswordEntry,
    navController: NavController,
    onCopyUsername: (() -> Unit)? = null,
    onCopyPassword: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { navController.navigate("password/${password.id}") },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.card)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .background(color = colorResource(R.color.primary).copy(alpha = 0.08f), shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = password.title,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                        color = colorResource(R.color.text),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = {
                        coroutineScope.launch {
                            PasswordStore.toggleFavorite(password.id)
                        }
                        // Haptic feedback could be added with `HapticFeedbackType.LongPress`
                    }) {
                        Icon(
                            imageVector = if (password.favorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (password.favorite) colorResource(R.color.accent) else colorResource(R.color.textSecondary)
                        )
                    }
                }

                Text(
                    text = password.username,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = colorResource(R.color.textSecondary),
                    modifier = Modifier.padding(bottom = 6.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                        color = colorResource(R.color.primary).copy(alpha = 0.08f),
                        shape = RoundedCornerShape(4.dp)
                    )
                        .padding(horizontal = 8.dp, vertical = 2.dp)

                ) {
                    Text(
                        text = formatDate(password.lastModified),
                        color = colorResource(R.color.textSecondary),
                        fontSize = MaterialTheme.typography.labelSmall.fontSize
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            onCopyUsername?.invoke()
                            toast(context, "Username copied")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Username",
                        tint = colorResource(R.color.textSecondary)
                    )
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            onCopyPassword?.invoke()
                            toast(context, "Password copied")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Copy Password",
                        tint = colorResource(R.color.textSecondary)
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
