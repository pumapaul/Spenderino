package de.paulweber.spenderino.android.utility

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.paulweber.spenderino.android.destinations.DirectionDestination
import de.paulweber.spenderino.viewmodel.ViewModel

@Composable
fun <A, R, State> Screen(
    title: String,
    viewModel: ViewModel<A, R, State>,
    hasBackButton: Boolean,
    destination: DirectionDestination,
    navigator: NavHostController,
    content: @Composable (state: State) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = contentColorFor(backgroundColor),
                elevation = 5.dp,
                navigationIcon = {
                    if (hasBackButton) {
                        IconButton(
                            onClick = {
                                viewModel.onBackButton()
                                navigator.popBackStack(destination.route, inclusive = true)
                            }
                        ) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "back button")
                        }
                    }
                },
                title = { Text(title) }
            )
        }
    ) {
        ObserveState(viewModel = viewModel) {
            content(it)
        }
    }
}
