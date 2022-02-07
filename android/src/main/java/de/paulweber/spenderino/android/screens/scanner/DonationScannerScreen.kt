package de.paulweber.spenderino.android.screens.scanner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigateTo
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.destinations.AlertDestination
import de.paulweber.spenderino.android.destinations.DonationScannerScreenDestination
import de.paulweber.spenderino.android.destinations.DonationScreenDestination
import de.paulweber.spenderino.android.utility.ConfigureRouting
import de.paulweber.spenderino.android.utility.Screen
import de.paulweber.spenderino.viewmodel.AlertViewModel
import de.paulweber.spenderino.viewmodel.DonationScannerAction
import de.paulweber.spenderino.viewmodel.DonationScannerRoute
import de.paulweber.spenderino.viewmodel.DonationScannerViewModel
import de.paulweber.spenderino.viewmodel.DonationViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun DonationScannerScreen(
    navigator: NavHostController,
    viewModel: DonationScannerViewModel,
    donationRoute: (DonationViewModel) -> Unit,
    alertRoute: (AlertViewModel) -> Unit,
    openSettingsClosure: () -> Unit
) {
    ConfigureRouting(
        viewModel = viewModel,
        navigator = navigator,
        destination = DonationScannerScreenDestination,
        handleBackButton = false
    ) {
        when (it) {
            is DonationScannerRoute.Donation -> {
                donationRoute(it.viewModel)
                navigator.navigateTo(DonationScreenDestination)
            }
            is DonationScannerRoute.Alert -> {
                alertRoute(it.alert)
                navigator.navigateTo(AlertDestination)
            }
        }
    }
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(null) {
        if (!cameraPermissionState.permissionRequested) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    Screen(
        title = stringResource(R.string.donation_scanner_title),
        viewModel = viewModel,
        hasBackButton = false,
        destination = DonationScannerScreenDestination,
        navigator = navigator
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            WithCameraPermission(
                cameraPermissionState = cameraPermissionState,
                openSettingsClosure = openSettingsClosure
            ) {
                QrCodeScanner {
                    viewModel.perform(DonationScannerAction.CodeScanned(it))
                }
            }
        }
    }
}
