package de.paulweber.spenderino.android.views

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.theme.SpenderinoTheme

@Composable
fun QRCodeView(byteArray: ByteArray?) {
    val border = if (MaterialTheme.colors.isLight) {
        BorderStroke(0.5.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
    } else null

    Surface(
        elevation = 2.dp,
        color = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(12.dp),
        border = border,
        modifier = Modifier
            .size(300.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (byteArray == null) {
                CircularProgressIndicator(color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
            } else {
                Image(
                    bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                        .asImageBitmap(),
                    contentDescription = "qr code to receive donations",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    SpenderinoTheme(darkTheme = true) {
        QRCodeView(byteArray = null)
    }
}
