package dev.alexmester.ui.components.pull_to_refresh_box

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.alexmester.ui.desing_system.LaskColors

@Composable
fun LaskPullToRefreshBox(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    state: PullToRefreshState,
    content: @Composable ( BoxScope.()-> Unit ),
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                containerColor = MaterialTheme.LaskColors.brand_blue10,
                color = MaterialTheme.LaskColors.brand_blue,
                state = state
            )
        },
        modifier = modifier,
    ){
        content()
    }
}