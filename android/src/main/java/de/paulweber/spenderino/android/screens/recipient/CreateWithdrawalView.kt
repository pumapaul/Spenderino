package de.paulweber.spenderino.android.screens.recipient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.views.PrimaryLoadingButton
import de.paulweber.spenderino.utility.toEuroString
import de.paulweber.spenderino.viewmodel.RecipientAction
import de.paulweber.spenderino.viewmodel.RecipientState
import de.paulweber.spenderino.viewmodel.RecipientViewModel
import kotlin.math.min

@Composable
fun CreateWithdrawalView(
    state: RecipientState.BalanceState.StateHolder,
    viewModel: RecipientViewModel
) {
    if (state.currentBalance < viewModel.minWithdrawalSum) {
        Text(
            viewModel.withdrawalUnderMinText,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center
        )
    } else {
        val pickerValue = remember { mutableStateOf(viewModel.minWithdrawalSum) }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Absolute.spacedBy(12.dp),
        ) {
            Text(
                stringResource(R.string.balance_withdrawal_create),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.balance_withdrawal_amount_label),
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.width(12.dp))
                Picker(state.currentBalance, viewModel, pickerValue)
            }

            PrimaryLoadingButton(
                isLoading = state.isCreatingWithdrawal,
                onClick = { viewModel.perform(RecipientAction.CreateWithdrawal(pickerValue.value)) },
                enabled = !state.isCreatingWithdrawal,
            ) { Text(stringResource(R.string.balance_withdrawal_create_button)) }
        }
    }
}

@Composable
private fun Picker(
    currentBalance: Long,
    viewModel: RecipientViewModel,
    pickerValue: MutableState<Long>
) {
    var isPickerExpanded by remember { mutableStateOf(false) }

    val pickerMin = viewModel.minWithdrawalSum / 100
    val pickerMax = min(currentBalance, viewModel.maxWithdrawalSum) / 100
    val pickerRange = pickerMin..pickerMax

    Button(onClick = { isPickerExpanded = !isPickerExpanded }) {
        Text(pickerValue.value.toEuroString())
        Icon(Icons.Outlined.ArrowDropDown, contentDescription = "drop down icon")
    }
    Box {
        DropdownMenu(
            expanded = isPickerExpanded,
            onDismissRequest = { isPickerExpanded = false },
        ) {
            pickerRange.forEach { integerAmount ->
                DropdownMenuItem(onClick = {
                    isPickerExpanded = false
                    pickerValue.value = integerAmount * 100
                }) {
                    Text((integerAmount * 100).toEuroString())
                }
            }
        }
    }
}
