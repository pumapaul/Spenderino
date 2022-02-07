package de.paulweber.spenderino.android.screens.donation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.paulweber.spenderino.utility.toEuroString
import de.paulweber.spenderino.viewmodel.DonationAction
import de.paulweber.spenderino.viewmodel.DonationState
import de.paulweber.spenderino.viewmodel.DonationViewModel

@Composable
fun DonationValuePicker(
    isInteractionEnabled: Boolean,
    state: DonationState.Base,
    viewModel: DonationViewModel
) {
    var isPickerExpanded by remember { mutableStateOf(false) }
    val pickerRange = 1L..50L

    val modifier = if (isInteractionEnabled) {
        Modifier.clickable { isPickerExpanded = !isPickerExpanded }
    } else Modifier

    val color = if (isInteractionEnabled) {
        MaterialTheme.colors.primary
    } else MaterialTheme.colors.onSurface.copy(alpha = 0.5f)

    Text(
        state.donationValue.toEuroString(),
        style = MaterialTheme.typography.body1,
        color = color,
        modifier = modifier
    )
    Box {
        DropdownMenu(
            expanded = isPickerExpanded,
            onDismissRequest = { isPickerExpanded = false },
        ) {
            pickerRange.forEach { integerAmount ->
                DropdownMenuItem(onClick = {
                    isPickerExpanded = false
                    val newValue = integerAmount * 100
                    viewModel.perform(DonationAction.ChangeDonationValue(newValue))
                }) {
                    Text((integerAmount * 100).toEuroString())
                }
            }
        }
    }
}
