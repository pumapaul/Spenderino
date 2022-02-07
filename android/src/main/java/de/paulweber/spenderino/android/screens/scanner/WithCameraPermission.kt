package de.paulweber.spenderino.android.screens.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.PermissionState
import de.paulweber.spenderino.android.R

@ExperimentalPermissionsApi
@Composable
fun WithCameraPermission(
    cameraPermissionState: PermissionState,
    openSettingsClosure: () -> Unit,
    content: @Composable () -> Unit
) {
    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Absolute.spacedBy(12.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    stringResource(R.string.donation_scanner_permission),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center
                )
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text(stringResource(R.string.donation_scanner_permission_button))
                }
            }
        },
        permissionNotAvailableContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Absolute.spacedBy(12.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    stringResource(R.string.donation_scanner_permission_denied),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center
                )
                Button(onClick = openSettingsClosure) {
                    Text(stringResource(R.string.donation_scanner_permission_denied_button))
                }
            }
        }
    ) {
        content()
    }
}
