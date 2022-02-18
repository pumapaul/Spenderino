package de.paulweber.spenderino.android.views

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PrimaryLoadingButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    PrimaryButton(onClick = onClick, enabled = enabled, modifier = modifier) {
        if (isLoading) {
            ButtonLoadingIndicator()
        } else {
            content()
        }
    }
}
