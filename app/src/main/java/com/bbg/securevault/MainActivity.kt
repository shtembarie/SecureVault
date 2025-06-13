package com.bbg.securevault

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bbg.securevault.data.PasswordStore
import com.bbg.securevault.presentation.loginScreen.LoginScreen
import com.bbg.securevault.presentation.passwords.NewPasswordScreen
import com.bbg.securevault.presentation.passwords.PasswordDetailScreen
import com.bbg.securevault.presentation.tabs.MainScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PasswordManagerApp()
        }
    }
}


@Composable
fun PasswordManagerApp() {
    var isSplashVisible by remember { mutableStateOf(true) }
    var isAuthenticated by remember { mutableStateOf(true) } // true = eingelogt

    LaunchedEffect(Unit) {
        // SplashScreen simulieren (z. B. für Font-Loading)
        delay(300) // optional
        isSplashVisible = false
    }

    if (isSplashVisible) {
        Box(modifier = Modifier.fillMaxSize()) // optional leerer Splash
    } else {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) "login" else "tabs"
        ) {
            composable("login") {
                LoginScreen(navController = navController)
            }

            composable("tabs") {
                MainScreen(navController = navController)
            }

            composable("password/new") {
                val coroutineScope = rememberCoroutineScope()
                NewPasswordScreen(
                    navController = navController,
                    addPassword = { entry ->
                        coroutineScope.launch {
                            PasswordStore.addPassword(entry)
                        }
                    },
                    updatePassword = { _, _ -> }, // not used in create mode
                    generatePassword = PasswordStore::generateNewPassword
                )
            }

            composable("password/edit/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                val passwordToEdit = PasswordStore.passwords.find { it.id == id }

                if (passwordToEdit != null) {
                    val coroutineScope = rememberCoroutineScope()
                    NewPasswordScreen(
                        navController = navController,
                        addPassword = { entry ->
                            coroutineScope.launch {
                                PasswordStore.addPassword(entry)
                            }
                        },
                        updatePassword = { entryId, entry ->
                            coroutineScope.launch {
                                PasswordStore.updatePassword(entryId, entry)
                            }
                        },
                        generatePassword = PasswordStore::generateNewPassword,
                        passwordToEdit = passwordToEdit
                    )
                } else {
                    Text("Password not found.")
                }
            }

            composable("password/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""

                PasswordDetailScreen(
                    id = id,
                    navController = navController,
                    getPasswordById = { targetId -> PasswordStore.passwords.find { it.id == targetId } },
                    updatePassword = { entryId, entry ->
                        CoroutineScope(Dispatchers.IO).launch {
                            PasswordStore.updatePassword(entryId, entry)
                        }
                    },
                    deletePassword = { entryId ->
                        CoroutineScope(Dispatchers.IO).launch {
                            PasswordStore.deletePassword(entryId)
                        }
                    },
                    toggleFavorite = { entryId ->
                        CoroutineScope(Dispatchers.IO).launch {
                            PasswordStore.toggleFavorite(entryId)
                        }
                    }
                )
            }
        }
    }
}



