package com.bbg.securevault.domain.inactivity


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.bbg.securevault.domain.AuthSessionStore
import com.bbg.securevault.domain.InactivityManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
/**
 * Created by Enoklit on 26.06.2025.
 */

@Composable
fun InactivityWrapper(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // 1. Start inactivity checking coroutine
    LaunchedEffect(Unit) {
        while (true) {
            delay(10_000L) // check every 10s
            if (InactivityManager.hasTimedOut()) {
                FirebaseAuth.getInstance().signOut()
                AuthSessionStore.setLoggedIn(context, false)
                navController.navigate("login") {
                    popUpTo(0)
                }
                break
            }
        }
    }

    // 2. Listen for user interaction (touch)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                while (true) {
                    awaitPointerEventScope {
                        awaitPointerEvent()
                        InactivityManager.updateActivity()
                    }
                }
            }
    ) {
        content()
    }
}
