package de.paulweber.spenderino.android.screens

import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.navigation.navigateTo
import com.ramcosta.composedestinations.spec.Direction
import de.paulweber.spenderino.android.destinations.DonationScannerScreenDestination
import de.paulweber.spenderino.android.destinations.PreferencesScreenDestination
import de.paulweber.spenderino.android.destinations.RecipientScreenDestination
import de.paulweber.spenderino.utility.L10n
import de.paulweber.spenderino.viewmodel.TabAction
import de.paulweber.spenderino.viewmodel.TabEnum
import de.paulweber.spenderino.viewmodel.TabViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private sealed class TabItem(val icon: ImageVector, val title: String) {
    object Donate : TabItem(Icons.Outlined.PhotoCamera, L10n.get("tab_donation"))
    object Receive : TabItem(Icons.Outlined.QrCode, L10n.get("tab_recipient"))
    object Account : TabItem(Icons.Outlined.Settings, L10n.get("tab_preferences"))
}

private fun TabEnum.getItem(): TabItem {
    return when (this) {
        TabEnum.DONATION -> TabItem.Donate
        TabEnum.PREFERENCES -> TabItem.Account
        TabEnum.RECIPIENT -> TabItem.Receive
    }
}

@Composable
fun TabBarView(navController: NavHostController, viewModel: TabViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val stateFlow = remember(viewModel.state, lifecycleOwner) {
        viewModel.state.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }
    val state by stateFlow.collectAsState(viewModel.state.value)

    fun navigateToTabDestination(destination: Direction) {
        navController.navigateTo(destination) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    LaunchedEffect(Unit) {
        stateFlow.onEach {
            when (it.selectedTab) {
                TabEnum.DONATION -> navigateToTabDestination(DonationScannerScreenDestination)
                TabEnum.RECIPIENT -> navigateToTabDestination(RecipientScreenDestination)
                TabEnum.PREFERENCES -> navigateToTabDestination(PreferencesScreenDestination)
            }
        }.launchIn(this)
    }

    val tabs = listOf(TabEnum.DONATION, TabEnum.RECIPIENT, TabEnum.PREFERENCES)

    Surface(
        elevation = 8.dp,
        color = MaterialTheme.colors.surface
    ) {
        TabRow(
            selectedTabIndex = state.selectedTab.ordinal,
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = contentColorFor(MaterialTheme.colors.surface)
        ) {
            tabs.forEach { tab ->
                val item = tab.getItem()
                Tab(
                    text = { Text(item.title) },
                    icon = { Icon(item.icon, contentDescription = "${item.title} tab") },
                    selected = state.selectedTab == tab,
                    onClick = { viewModel.perform(TabAction.SelectTab(tab)) }
                )
            }
        }
    }
}
