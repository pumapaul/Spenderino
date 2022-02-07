package de.paulweber.spenderino.android.screens.recipient

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.views.QRCodeView
import de.paulweber.spenderino.viewmodel.RecipientState
import java.io.File

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun RecipientProfileView(state: RecipientState.QRState) {
    val scrollState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(20.dp),
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(32.dp)
    ) {
        Text(
            state.text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6
        )

        QRCodeView(state.qrCode)

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                stringResource(R.string.recipient_qrcode_label),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1
            )
        }
        if (state.qrCode != null) {
            val context = LocalContext.current
            IconButton(onClick = { shareImage(context, state.qrCode!!) }) {
                Icon(
                    Icons.Outlined.Share,
                    contentDescription = "Share button",
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}

private fun shareImage(context: Context, image: ByteArray) {
    val cachePath = File(context.externalCacheDir, "my_images/")
    cachePath.mkdirs()
    val file = File(cachePath, "donation_qr_code.png")
    val fileOutputStream: FileOutputStream

    try {
        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
        fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    } catch (e: FileNotFoundException) {
        Log.e(null, "$e")
    } catch (e: IOException) {
        Log.e(null, "$e")
    }

    val cacheImageUri: Uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        clipData = ClipData.newRawUri(null, cacheImageUri)
        putExtra(Intent.EXTRA_STREAM, cacheImageUri)
        type = "image/*"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, null))
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    RecipientProfileView(
        state = RecipientState.QRState("Some text", null)
    )
}
