package com.bbg.securevault.presentation.passwords

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bbg.securevault.data.models.PasswordCategory
import com.bbg.securevault.data.models.PasswordEntry
import com.bbg.securevault.presentation.components.PasswordInput
import com.bbg.securevault.presentation.components.PasswordStrengthIndicator
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.bbg.securevault.R
import com.bbg.securevault.domain.CategoryStorage
import com.bbg.securevault.domain.PasswordStore
import com.bbg.securevault.presentation.components.CategorySelector
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

    // Kategorie-Status:
    var category by remember { mutableStateOf(passwordToEdit?.category ?: PasswordCategory.PERSONAL) }
    var customCategory by remember { mutableStateOf(passwordToEdit?.customCategory ?: "") }

    // Benutzerdefinierte Kategorienliste (kann später persistent gemacht werden)
    var customCategories by remember { mutableStateOf(listOf<String>()) }
    LaunchedEffect(Unit) {
        val savedCategories = CategoryStorage.getCustomCategories(context)
        customCategories = savedCategories

        if (passwordToEdit != null && passwordToEdit.category == PasswordCategory.OTHER) {
            val cat = passwordToEdit.customCategory ?: ""
            if (!savedCategories.contains(cat)) {
                customCategories = savedCategories + cat  // Ensure old one still shows up
            }
            category = PasswordCategory.OTHER
            customCategory = cat
        }
    }

    val baseCategories = listOf(
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
                title = { Text(if (isEditing) stringResource(R.string.edit_password) else stringResource(
                    R.string.new_password
                )
                ) },
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.abbrechen), tint = Color.Red)
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
                label = { Text(stringResource(R.string.title)) },
                placeholder = { Text(stringResource(R.string.e_g_gmail_netflix_bank)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.username_email)) },
                placeholder = { Text(stringResource(R.string.username_example_com)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.passwort), style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = {
                    password = generatePassword()
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.generate))
                }
            }

            PasswordInput(value = password, onValueChange = { password = it })
            PasswordStrengthIndicator(password = password)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text(stringResource(R.string.website_url_optional)) },
                placeholder = { Text(stringResource(R.string.https_example_com)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(stringResource(R.string.category), style = MaterialTheme.typography.bodyMedium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CategorySelector(
                    categories = baseCategories,
                    customCategories = customCategories,
                    selectedCategory = category,
                    selectedCustomCategory = customCategory,
                    onCategorySelected = { newCategory, newCustomCat ->
                        category = newCategory
                        customCategory = newCustomCat ?: ""
                    },
                    onAddCustomCategory = { newCat ->
                        if (!customCategories.contains(newCat)) {
                            customCategories = customCategories + newCat
                            category = PasswordCategory.OTHER
                            customCategory = newCat
                        }
                    },
                    onDeleteCustomCategory = { cat ->
                        customCategories = customCategories.filter { it != cat }
                        if (customCategory == cat) {
                            category = PasswordCategory.PERSONAL
                            customCategory = ""
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.notes_optional)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text(stringResource(R.string.add_any_additional_information_here)) },
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isBlank() || username.isBlank() || password.isBlank()) {
                        Toast.makeText(context,
                            context.getString(R.string.all_required_fields_must_be_filled), Toast.LENGTH_SHORT).show()
                    } else {
                        val entry = PasswordEntry(
                            id = passwordToEdit?.id ?: UUID.randomUUID().toString(),
                            title = title.trim(),
                            username = username.trim(),
                            password = password,
                            url = url.trim().ifBlank { null },
                            notes = notes.trim().ifBlank { null },
                            category = category,
                            customCategory = if (category == PasswordCategory.OTHER) customCategory else null,
                            favorite = passwordToEdit?.favorite ?: false,
                            createdAt = passwordToEdit?.createdAt ?: System.currentTimeMillis(),
                            lastModified = System.currentTimeMillis(),
                            userId = passwordToEdit?.userId ?: PasswordStore.currentUserId ?: ""
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
                Text(if (isEditing) stringResource(R.string.update_password) else stringResource(R.string.save_password))
            }
        }
    }
}