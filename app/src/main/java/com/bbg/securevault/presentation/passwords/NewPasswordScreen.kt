package com.bbg.securevault.presentation.passwords

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bbg.securevault.domain.models.PasswordCategory
import com.bbg.securevault.domain.models.PasswordEntry
import com.bbg.securevault.presentation.components.PasswordInput
import com.bbg.securevault.presentation.components.PasswordStrengthIndicator
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.bbg.securevault.R
import java.util.UUID

/**
 * Created by Enoklit on 04.06.2025.
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewPasswordScreen(
    navController: NavController,
    addPassword: (PasswordEntry) -> Unit,
    updatePassword: (String, PasswordEntry) -> Unit,
    generatePassword: () -> String,
    passwordToEdit: PasswordEntry? = null
) {
    val isEditing = passwordToEdit != null
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current

    var title by remember { mutableStateOf(passwordToEdit?.title ?: "") }
    var username by remember { mutableStateOf(passwordToEdit?.username ?: "") }
    var password by remember { mutableStateOf(passwordToEdit?.password ?: "") }
    var url by remember { mutableStateOf(passwordToEdit?.url ?: "") }
    var notes by remember { mutableStateOf(passwordToEdit?.notes ?: "") }
    var category by remember { mutableStateOf(passwordToEdit?.category ?: PasswordCategory.PERSONAL) }

    val categories = listOf(
        PasswordCategory.PERSONAL,
        PasswordCategory.WORK,
        PasswordCategory.FINANCE,
        PasswordCategory.SOCIAL,
        PasswordCategory.OTHER
    )

    Scaffold(
        containerColor = colorResource(R.color.background),
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Password" else "New Password") },
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.white),
                    titleContentColor = colorResource(R.color.black),
                    navigationIconContentColor = colorResource(R.color.danger)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                placeholder = { Text("e.g. Gmail, Netflix, Bank") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username / Email") },
                placeholder = { Text("username@example.com") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Password", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = {
                    password = generatePassword()
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Generate")
                }
            }

            PasswordInput(value = password, onValueChange = { password = it })
            PasswordStrengthIndicator(password = password)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Website URL (optional)") },
                placeholder = { Text("https://example.com") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Category", style = MaterialTheme.typography.bodyMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = {
                            category = cat
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        label = {
                            Text(cat.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Add any additional information here") },
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isBlank() || username.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "All required fields must be filled", Toast.LENGTH_SHORT).show()
                    } else {
                        val entry = PasswordEntry(
                            id = passwordToEdit?.id ?: UUID.randomUUID().toString(),
                            title = title.trim(),
                            username = username.trim(),
                            password = password,
                            url = url.trim().ifBlank { null },
                            notes = notes.trim().ifBlank { null },
                            category = category,
                            favorite = passwordToEdit?.favorite ?: false,
                            createdAt = passwordToEdit?.createdAt ?: System.currentTimeMillis(),
                            lastModified = System.currentTimeMillis()
                        )

                        if (isEditing) {
                            updatePassword(entry.id, entry)
                        } else {
                            addPassword(entry)
                        }

                        navController.popBackStack()
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Update Password" else "Save Password")
            }
        }
    }
}


//@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
//@Composable
//fun NewPasswordScreen(
//    navController: NavController,
//    addPassword: (PasswordEntry) -> Unit,
//    generatePassword: () -> String
//) {
//    var title by remember { mutableStateOf("") }
//    var username by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var url by remember { mutableStateOf("") }
//    var notes by remember { mutableStateOf("") }
//    var category by remember { mutableStateOf(PasswordCategory.PERSONAL) }
//    val haptics = LocalHapticFeedback.current
//    val context = LocalContext.current
//
//    val categories = listOf(
//        PasswordCategory.PERSONAL,
//        PasswordCategory.WORK,
//        PasswordCategory.FINANCE,
//        PasswordCategory.SOCIAL,
//        PasswordCategory.OTHER
//    )
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("New Password") },
//                actions = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.Red)
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .verticalScroll(rememberScrollState())
//                .padding(16.dp)
//        ) {
//            OutlinedTextField(
//                value = title,
//                onValueChange = { title = it },
//                label = { Text("Title") },
//                placeholder = { Text("e.g. Gmail, Netflix, Bank") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = username,
//                onValueChange = { username = it },
//                label = { Text("Username / Email") },
//                placeholder = { Text("username@example.com") },
//                modifier = Modifier.fillMaxWidth(),
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//
//
//                Text("Password", style = MaterialTheme.typography.bodyMedium)
//                TextButton(onClick = {
//                    password = generatePassword()
//                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
//                }) {
//                    Icon(Icons.Default.Refresh, contentDescription = null)
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text("Generate")
//                }
//            }
//
//            PasswordInput(value = password, onValueChange = { newValue -> password = newValue })
//            PasswordStrengthIndicator(password = password)
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = url,
//                onValueChange = { url = it },
//                label = { Text("Website URL (optional)") },
//                placeholder = { Text("https://example.com") },
//                modifier = Modifier.fillMaxWidth(),
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text("Category", style = MaterialTheme.typography.bodyMedium)
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                categories.forEach { cat ->
//                    FilterChip(
//                        selected = category == cat,
//                        onClick = {
//                            category = cat
//                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
//                        },
//                        label = {
//                            Text(cat.name.lowercase().replaceFirstChar { it.uppercase() })
//                        }
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = notes,
//                onValueChange = { notes = it },
//                label = { Text("Notes (optional)") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(120.dp),
//                placeholder = { Text("Add any additional information here") },
//                maxLines = 4
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Button(
//                onClick = {
//                    if (title.isBlank() || username.isBlank() || password.isBlank()) {
//                        Toast.makeText(context, "All required fields must be filled", Toast.LENGTH_SHORT).show()
//                    } else {
//                        addPassword(
//                            PasswordEntry(
//                                id = UUID.randomUUID().toString(),
//                                title = title.trim(),
//                                username = username.trim(),
//                                password = password,
//                                url = url.trim().ifBlank { null },
//                                notes = notes.trim().ifBlank { null },
//                                category = category,
//                                favorite = false,
//                                createdAt = System.currentTimeMillis(),
//                                lastModified = System.currentTimeMillis()
//                            )
//                        )
//                        navController.popBackStack()
//                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
//                    }
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Save Password")
//            }
//
//        }
//    }
//}