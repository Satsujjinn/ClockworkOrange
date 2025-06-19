package com.clockworkred.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@AndroidEntryPoint
@RuntimePermissions
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockworkRedAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavGraph(navController)
                }
            }
        }
    }

    /** Example permission gated call. */
    @NeedsPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun exportFiles() {
        // TODO implement export logic
    }
}
