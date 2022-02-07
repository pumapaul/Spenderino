package de.paulweber.spenderino.android.screens.recipient

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.viewmodel.RecipientState
import de.paulweber.spenderino.viewmodel.RecipientViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecipientPagerView(
    recipientViewModel: RecipientViewModel,
    state: RecipientState.Pager
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = contentColorFor(MaterialTheme.colors.surface),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            Tab(
                text = { Text(stringResource(R.string.recipient_tab_qr)) },
                selected = pagerState.currentPage == 0,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0, 0f) } },
            )
            Tab(
                text = { Text(stringResource(R.string.recipient_tab_balance)) },
                selected = pagerState.currentPage == 1,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1, 0f) } },
            )
        }

        HorizontalPager(
            count = 2,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            when (page) {
                0 -> RecipientProfileView(state.qrState)
                1 -> RecipientBalanceView(state.balanceState, recipientViewModel)
            }
        }
    }
}
