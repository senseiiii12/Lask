package dev.alexmester.impl.presentation.interests.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.alexmester.impl.presentation.interests.mvi.InterestsIntent
import dev.alexmester.ui.R
import dev.alexmester.ui.components.buttons.LaskBackButton
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestsTopBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            LaskBackButton(onClick = onClick)
        },
        title = {
            Text(
                text = stringResource(R.string.profile_menu_interests),
                style = MaterialTheme.LaskTypography.h5,
                color = MaterialTheme.LaskColors.textPrimary,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.LaskColors.brand_blue10,
        ),
    )
}