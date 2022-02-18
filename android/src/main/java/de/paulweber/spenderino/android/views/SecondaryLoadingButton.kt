package de.paulweber.spenderino.android.views

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SecondaryLoadingButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    SecondaryButton(onClick = onClick, enabled = enabled, modifier = modifier) {
        if (isLoading) {
            ButtonLoadingIndicator(color = MaterialTheme.colors.secondary)
        } else {
            content()
        }
    }
}
