package de.paulweber.spenderino.android.screens.scanner

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView

@Composable
fun QrCodeScanner(onQrCodeScanned: (String) -> Unit) {
    var isScanningBlocked by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val compoundBarcodeView = remember {
        CompoundBarcodeView(context).apply {
            val capture = CaptureManager(context as Activity, this)
            capture.initializeFromIntent(context.intent, null)
        }
    }

    LaunchedEffect(null) {
        compoundBarcodeView.resume()
        compoundBarcodeView.decodeContinuous { result ->
            val isQrCode = result.barcodeFormat == BarcodeFormat.QR_CODE

            if (!isQrCode || isScanningBlocked) {
                return@decodeContinuous
            }

            isScanningBlocked = true
            result.text?.let {
                onQrCodeScanned(it)
            } ?: run {
                isScanningBlocked = false
            }
        }
    }

    DisposableEffect(null) {
        onDispose {
            compoundBarcodeView.pause()
        }
    }

    AndroidView(
        factory = { compoundBarcodeView },
    )
}
