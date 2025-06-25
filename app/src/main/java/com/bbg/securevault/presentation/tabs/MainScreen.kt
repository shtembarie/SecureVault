package com.bbg.securevault.presentation.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bbg.securevault.R
import com.bbg.securevault.domain.AuthSessionStore
import com.bbg.securevault.data.sealedClasses.BottomNavItem
import com.bbg.securevault.presentation.generatorScreen.GeneratorScreen
import com.bbg.securevault.presentation.passwordScreen.PasswordsScreen
import com.bbg.securevault.presentation.settingScreen.SettingsScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


/**
 * Created by Enoklit on 04.06.2025.
 */



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    var currentScreen by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Passwords) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val items = listOf(
        BottomNavItem.Passwords,
        BottomNavItem.Generator,
        BottomNavItem.Settings
    )
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = currentScreen.label) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colorResource(R.color.background),
                        scrolledContainerColor = colorResource(R.color.background),
                        titleContentColor = colorResource(R.color.text)
                    ),
                    actions = {
                        if (currentScreen == BottomNavItem.Passwords) {
                            IconButton(onClick = {
                                navController.navigate("password/new")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Password",
                                    tint = colorResource(R.color.primary)
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = colorResource(R.color.background),
                    tonalElevation = 0.dp
                ) {
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                            label = { Text(text = item.label) },
                            selected = currentScreen.route == item.route,
                            onClick = { currentScreen = item },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colorResource(R.color.primary),
                                selectedTextColor = colorResource(R.color.primary),
                                unselectedIconColor = colorResource(R.color.textSecondary),
                                unselectedTextColor = colorResource(R.color.textSecondary)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    is BottomNavItem.Passwords -> PasswordsScreen(
                        navController = navController,
                        onAddPassword = { navController.navigate("password/new") }
                    )
                    is BottomNavItem.Generator -> GeneratorScreen()
                    is BottomNavItem.Settings -> {
                        SettingsScreen(
                            navController = navController,
                            logout = {
                                FirebaseAuth.getInstance().signOut()
                                coroutineScope.launch {
                                    AuthSessionStore.setLoggedIn(context, false)
                                }
                                navController.navigate("login") {
                                    popUpTo(0)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

