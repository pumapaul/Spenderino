package de.paulweber.spenderino.android.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingsIcon(
    icon: ImageVector,
    contentDescription: String,
    backgroundColor: Color
) {
    Box(
        Modifier
            .clip(CircleShape)
            .background(backgroundColor)
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colors.background,
            modifier = Modifier.padding(5.dp)
        )
    }
}
