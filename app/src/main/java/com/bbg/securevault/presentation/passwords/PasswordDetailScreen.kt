package com.bbg.securevault.presentation.passwords

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bbg.securevault.R
import com.bbg.securevault.data.models.PasswordCategory
import com.bbg.securevault.data.models.PasswordEntry
import com.bbg.securevault.presentation.core.utils.InfoRow
import java.text.DateFormat

import java.util.*
/**
 * Created by Enoklit on 04.06.2025.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordDetailScreen(
    id: String,
    navController: NavController,
    getPasswordById: (String) -> PasswordEntry?,
    updatePassword: (String, PasswordEntry) -> Unit,
    deletePassword: (String) -> Unit,
    toggleFavorite: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val password = remember(id) { getPasswordById(id) }

    if (password == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.password_not_found), color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.go_back))
            }
        }
        return
    }

    fun formatDate(timestamp: Long): String {
        return DateFormat.getDateTimeInstance().format(Date(timestamp))
    }

    Scaffold(
        containerColor = colorResource(R.color.background), // Whole screen background
        topBar = {
            TopAppBar(
                title = { Text(text = password.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { toggleFavorite(password.id) }) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = stringResource(R.string.favorite),
                            tint = if (password.favorite) Color.Yellow else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,              // Matches full background
                    titleContentColor = Color.Black,           // Optional: Text color
                    navigationIconContentColor = Color.Black,  // Optional: Icon color
                    actionIconContentColor = Color.Black       // Optional: Action icon color
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Prevent gaps on scroll
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow(
                        label = stringResource(R.string.username_email),
                        value = password.username,
                        copyable = true)
                    InfoRow(
                        label = stringResource(R.string.passwort),
                        value = if (showPassword) password.password else "••••••••••",
                        copyable = true,
                        toggleable = true,
                        onToggle = { showPassword = !showPassword },
                        toggleIcon = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        actualCopyValue = password.password
                    )
                    password.url?.let { url ->
                        InfoRow(
                            label = stringResource(R.string.website),
                            value = url,
                            trailingIcon = Icons.Default.OpenInNew,
                            onTrailingIconClick = {
                                val context = navController.context
                                val input = url.trim()

                                try {
                                    // Try to launch it as an app by package name
                                    val launchIntent = context.packageManager.getLaunchIntentForPackage(input)
                                    if (launchIntent != null) {
                                        context.startActivity(launchIntent)
                                    } else {
                                        // If not found as app, try as a web URL
                                        val webUrl = if (!input.startsWith("http")) "https://$input" else input
                                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
                                        context.startActivity(webIntent)
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context,
                                        context.getString(R.string.could_not_open, input), Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.category),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    val displayCategory = if (password.category == PasswordCategory.OTHER && !password.customCategory.isNullOrEmpty()) {
                        password.customCategory!!
                    } else {
                        password.category.name.lowercase().replaceFirstChar { it.uppercase() }
                    }

                    AssistChip(
                        onClick = { },
                        label = { Text(displayCategory) }
                    )

                    Spacer(Modifier.height(16.dp))
                    password.notes?.let {
                        Text(stringResource(R.string.notes), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(16.dp))
                    Divider()
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.created, formatDate(password.createdAt)), style = MaterialTheme.typography.labelSmall)
                    Text(stringResource(R.string.last_modified, formatDate(password.lastModified)), style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("password/edit/${password.id}") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4266AC))
            ) {
                Text(stringResource(R.string.edit_password))
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { showDeleteDialog = true }, // Trigger dialog instead of direct delete
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(stringResource(R.string.delete_password))
            }
            if (showDeleteDialog) {
                AlertDialog(
                    containerColor = colorResource(R.color.background),
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(stringResource(R.string.delete_password)) },
                    text = { Text(stringResource(R.string.are_you_sure_you_want_to_delete_this_password_this_action_cannot_be_undone)) },
                    confirmButton = {
                        TextButton(onClick = {
                            deletePassword(password.id)               //  Perform delete
                            navController.popBackStack()              //  Navigate back
                            showDeleteDialog = false                  //  Close dialog
                        }) {
                            Text(stringResource(R.string.yes), color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(stringResource(R.string.abbrechen))
                        }
                    }
                )
            }


        }
    }

}