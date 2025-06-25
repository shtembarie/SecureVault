package com.bbg.securevault.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.bbg.securevault.data.models.PasswordCategory
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import com.bbg.securevault.domain.CategoryStorage
import androidx.compose.foundation.combinedClickable




/**
 * Created by Enoklit on 24.06.2025.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    categories: List<PasswordCategory>,
    customCategories: List<String>,
    selectedCategory: PasswordCategory,
    selectedCustomCategory: String?,
    onCategorySelected: (PasswordCategory, String?) -> Unit,
    onAddCustomCategory: (String) -> Unit,
    onDeleteCustomCategory: (String) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var deleteMode by remember { mutableStateOf(false) }

    Column {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Basis-Kategorien ohne OTHER
            categories.filter { it != PasswordCategory.OTHER }.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = {
                        onCategorySelected(cat, null)
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        deleteMode = true  // Delete-Modus aus wenn andere Kategorie gewählt
                    },
                    label = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }

            // Benutzerdefinierte Kategorien – zeigen Minus, wenn deleteMode an
            customCategories.forEach { cat ->
                Box(
                    modifier = Modifier
                        .padding(end = 12.dp) // ✅ Platz rechts schaffen für das Minus-Icon
                        .wrapContentSize()    // ✅ Box passt sich an den Inhalt an
                ) {
                    FilterChip(
                        selected = selectedCategory == PasswordCategory.OTHER && selectedCustomCategory == cat,
                        onClick = {
                            onCategorySelected(PasswordCategory.OTHER, cat)
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            deleteMode = false  // Delete-Modus aus beim normalen Klick
                        },
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    onCategorySelected(PasswordCategory.OTHER, cat)
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    deleteMode = true
                                },
                                onLongClick = {
                                    deleteMode = true // ✅ Long-Klick aktiviert den Löschmodus
                                }
                            ),
                        label = {
                            Text(cat)
                        }
                    )

                    // ✅ Minus-Icon außerhalb des FilterChip, oben rechts überlagert
                    if (deleteMode) {
                        Icon(
                            imageVector = Icons.Default.RemoveCircle,
                            contentDescription = "Kategorie löschen",
                            tint = Color.Red,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 12.dp, y = (-1).dp) // ✅ Verschiebt das Icon leicht über den Rand
                                .clickable {
                                    onDeleteCustomCategory(cat)
                                    deleteMode = true
                                }
                        )
                    }
                }
            }

            // Button zum Kategorie hinzufügen
            FilterChip(
                selected = false,
                onClick = {
                    showAddDialog = true
                    deleteMode = false  // Delete-Modus aus beim Hinzufügen
                },
                modifier = Modifier.combinedClickable(
                    onClick = {
                        showAddDialog = true
                        deleteMode = true
                    },
                    onLongClick = {
                        //deleteMode = true
                    }
                ),
                label = { Text("Add Category") }
            )
        }
    }


    // Dialog für neue Kategorie
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Neue Kategorie") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Kategorie Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val formatted = newCategoryName.trim().replaceFirstChar { it.uppercase() }
                    if (formatted.isNotBlank()) {
                        onAddCustomCategory(formatted)
                        newCategoryName = ""
                        showAddDialog = false
                    }
                }) {
                    Text("Hinzufügen")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    newCategoryName = ""
                    showAddDialog = false
                }) {
                    Text("Abbrechen")
                }
            }
        )
    }

    // Speichere Kategorien bei Änderung automatisch
    LaunchedEffect(customCategories) {
        CategoryStorage.saveCustomCategories(context, customCategories)
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CategorySelector(
//    categories: List<PasswordCategory>,
//    customCategories: List<String>,
//    selectedCategory: PasswordCategory,
//    selectedCustomCategory: String?,
//    onCategorySelected: (PasswordCategory, String?) -> Unit,
//    onAddCustomCategory: (String) -> Unit,
//    onDeleteCustomCategory: (String) -> Unit
//) {
//    val haptics = LocalHapticFeedback.current
//    val context = LocalContext.current
//    var showAddDialog by remember { mutableStateOf(false) }
//    var newCategoryName by remember { mutableStateOf("") }
//    var deleteMode by remember { mutableStateOf(false) }
//
//    Column {
//        FlowRow(
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp),
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            categories.filter { it != PasswordCategory.OTHER }.forEach { cat ->
//                FilterChip(
//                    selected = selectedCategory == cat,
//                    onClick = {
//                        onCategorySelected(cat, null)
//                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
//                    },
//                    label = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }) }
//                )
//            }
//
//            customCategories.forEach { cat ->
//                Box(
//                    modifier = Modifier
//                        .combinedClickable(
//                            onClick = {
//                                onCategorySelected(PasswordCategory.OTHER, cat)
//                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
//                            },
//                            onLongClick = {
//                                deleteMode = true
//                            }
//                        )
//                ) {
//                    FilterChip(
//                        selected = selectedCategory == PasswordCategory.OTHER && selectedCustomCategory == cat,
//                        onClick = {}, // handled above
//                        label = {
//                            Row(verticalAlignment = Alignment.CenterVertically) {
//                                Text(cat)
//                                if (deleteMode) {
//                                    Spacer(modifier = Modifier.width(4.dp))
//                                    Icon(
//                                        imageVector = Icons.Default.RemoveCircle,
//                                        contentDescription = "Delete",
//                                        tint = Color.Red,
//                                        modifier = Modifier
//                                            .size(18.dp)
//                                            .clickable {
//                                                onDeleteCustomCategory(cat)
//                                                deleteMode = false
//                                            }
//                                    )
//                                }
//                            }
//                        }
//                    )
//                }
//            }
//
//            FilterChip(
//                selected = false,
//                onClick = { showAddDialog = true },
//                label = { Text("Add Category") }
//            )
//        }
//    }
//
//    if (showAddDialog) {
//        AlertDialog(
//            onDismissRequest = { showAddDialog = false },
//            title = { Text("New Category") },
//            text = {
//                OutlinedTextField(
//                    value = newCategoryName,
//                    onValueChange = { newCategoryName = it },
//                    label = { Text("Category Name") },
//                    singleLine = true
//                )
//            },
//            confirmButton = {
//                TextButton(onClick = {
//                    val formatted = newCategoryName.trim().replaceFirstChar { it.uppercase() }
//                    if (formatted.isNotBlank()) {
//                        onAddCustomCategory(formatted)
//                        newCategoryName = ""
//                        showAddDialog = false
//                    }
//                }) {
//                    Text("Add")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = {
//                    newCategoryName = ""
//                    showAddDialog = false
//                }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//    LaunchedEffect(customCategories) {
//        CategoryStorage.saveCustomCategories(context, customCategories)
//    }
//
//}