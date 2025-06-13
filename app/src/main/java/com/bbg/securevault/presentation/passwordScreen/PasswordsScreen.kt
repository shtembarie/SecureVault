package com.bbg.securevault.presentation.passwordScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bbg.securevault.R
import com.bbg.securevault.data.PasswordStore
import com.bbg.securevault.domain.models.PasswordEntry
import com.bbg.securevault.presentation.components.CustomButton
import com.bbg.securevault.presentation.components.PasswordListItem

import java.util.*

/**
 * Created by Enoklit on 04.06.2025.
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordsScreen(onAddPassword: () -> Unit, navController: NavController) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    var searchQuery by remember { mutableStateOf(TextFieldValue()) }
    var showSearch by remember { mutableStateOf(false) }
    //val passwords by remember { mutableStateOf(PasswordStore.passwords) }
    val passwords = PasswordStore.passwords


    val filtered = passwords.filter {
        val query = searchQuery.text.lowercase(Locale.getDefault())
        it.title.lowercase().contains(query) ||
                it.username.lowercase().contains(query) ||
                it.category.name.lowercase().contains(query)
    }.sortedWith(compareByDescending<PasswordEntry> { it.favorite }.thenBy { it.title })

    Column(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.background))) {

        if (showSearch) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(colorResource(R.color.card), shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = colorResource(R.color.textSecondary))
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search passwords...",
                            color = colorResource(R.color.placeholder)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = colorResource(R.color.text)
                    ),
                    textStyle = LocalTextStyle.current.copy(color = colorResource(R.color.text))
                )
                IconButton(onClick = {
                    showSearch = false
                    searchQuery = TextFieldValue()
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = colorResource(R.color.textSecondary))
                }
            }
        }

        if (passwords.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filtered.size} ${if (filtered.size == 1) "Password" else "Passwords"}",
                    color = colorResource(R.color.textSecondary),
                    style = MaterialTheme.typography.labelMedium
                )
                Row {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = colorResource(R.color.textSecondary))
                    }
                    IconButton(onClick = { /* Future filter feature */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = null, tint = colorResource(R.color.textSecondary))
                    }
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (filtered.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(top = 100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    color = colorResource(R.color.primary).copy(alpha = 0.1f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = colorResource(R.color.primary)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("No passwords yet", style = MaterialTheme.typography.titleMedium, color = colorResource(R.color.text))
                        Text(
                            "Add your first password by tapping the + button",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorResource(R.color.textSecondary),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        CustomButton(title = "Add Password", onClick = onAddPassword)
                    }
                }
            } else {
                items(filtered, key = { it.id }) { password ->
                    PasswordListItem(
                        password = password,
                        navController = navController,
                        onCopyUsername = {
                            clipboardManager.setText(AnnotatedString(password.username))
                            Toast.makeText(context, "Username copied", Toast.LENGTH_SHORT).show()
                        },
                        onCopyPassword = {
                            clipboardManager.setText(AnnotatedString(password.password))
                            Toast.makeText(context, "Password copied", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
