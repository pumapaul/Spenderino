package de.paulweber.spenderino.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.paulweber.spenderino.android.screens.MainScreen
import de.paulweber.spenderino.android.screens.donation.StripeViewModel
import de.paulweber.spenderino.viewmodel.AppAction
import de.paulweber.spenderino.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {

    private val appViewModel = AppViewModel.bootstrap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stripeViewModel = StripeViewModel(this)

        setContent {
            MainScreen(appViewModel.tabViewModel, stripeViewModel) { openSettings() }
        }

        val data: Uri? = intent?.data

        data?.let {
            appViewModel.perform(AppAction.DeepLink(it.toString()))
        }
    }

    private fun openSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
        )
    }
}
