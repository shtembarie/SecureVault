package com.bbg.securevault.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Enoklit on 12.06.2025.
 */
object InactivityManager {
    var lastActivityTime = System.currentTimeMillis()

    fun updateActivity() {
        lastActivityTime = System.currentTimeMillis()
    }

    fun hasTimedOut(): Boolean {
        return System.currentTimeMillis() - lastActivityTime > 2 * 60 * 1000 // 2 minutes
    }
}