package com.example.farmeraid.snackbar

// reference: https://medium.com/@jurajkunier/how-to-show-snackbar-in-jetpack-compose-3f2d81891f87

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SnackbarDelegate(
    var snackbarHostState: SnackbarHostState? = null,
    var coroutineScope: CoroutineScope? = null
) {

    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        onAction : () -> Unit = {},
        duration: SnackbarDuration = SnackbarDuration.Short,
        withDismissAction: Boolean = true,
    ) {
        coroutineScope?.launch {
            val result = snackbarHostState?.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                withDismissAction = withDismissAction,
                duration = duration,
            )
            when(result) {
                SnackbarResult.ActionPerformed -> onAction()
                else -> {}
            }
        }
    }
}