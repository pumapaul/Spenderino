package de.paulweber.spenderino.android.screens

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import de.paulweber.spenderino.android.screens.donation.StripeViewModel
import de.paulweber.spenderino.android.theme.SpenderinoTheme
import de.paulweber.spenderino.viewmodel.TabViewModel

@Composable
fun MainScreen(tabViewModel: TabViewModel, stripeViewModel: StripeViewModel, openSettingsClosure: () -> Unit) {
    val navController = rememberNavController()

    SpenderinoTheme {
        Scaffold(
            bottomBar = { TabBarView(navController, viewModel = tabViewModel) }
        ) {
            NavigationHost(
                navController = navController,
                tabViewModel = tabViewModel,
                stripeViewModel = stripeViewModel,
                openSettingsClosure = openSettingsClosure
            )
        }
    }
}
