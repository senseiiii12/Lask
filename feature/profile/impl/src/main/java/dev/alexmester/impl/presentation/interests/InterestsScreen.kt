package dev.alexmester.impl.presentation.interests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.impl.presentation.interests.components.InterestsTopBar
import dev.alexmester.impl.presentation.interests.mvi.InterestsIntent
import dev.alexmester.impl.presentation.interests.mvi.InterestsSideEffect
import dev.alexmester.impl.presentation.interests.mvi.InterestsState
import dev.alexmester.impl.presentation.interests.mvi.InterestsViewModel
import dev.alexmester.ui.components.buttons.LaskChipButton
import dev.alexmester.ui.components.buttons.LaskChipButtonVariants
import dev.alexmester.ui.components.buttons.LaskTextButton
import dev.alexmester.ui.components.input_field.LaskTextField
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun InterestsScreen(
    onBack: () -> Unit,
    viewModel: InterestsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                InterestsSideEffect.NavigateBack -> onBack()
            }
        }
    }

    InterestsScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@Composable
internal fun InterestsScreenContent(
    state: InterestsState,
    onIntent: (InterestsIntent) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            InterestsTopBar(onClick = { onIntent(InterestsIntent.Back) })
        },
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LaskTextField(
                    modifier = Modifier.weight(1f).focusRequester(focusRequester),
                    text = state.inputText,
                    placeholderText = "Write you interests",
                    leadingIcon = Icons.Default.Interests,
                    onValueChange = { onIntent(InterestsIntent.OnInputChange(it)) },
                    onClearClick = { onIntent(InterestsIntent.OnInputChange("")) },
                    onDone = {
                        if (state.canAdd) {
                            onIntent(InterestsIntent.Add)
                            keyboard?.hide()
                        }
                    }
                )
                LaskTextButton(
                    modifier = Modifier,
                    text = "Add",
                    textColor = if (state.canAdd) MaterialTheme.LaskColors.brand_blue
                    else MaterialTheme.LaskColors.textSecondary,
                    onClick = {
                        if (state.canAdd) {
                            onIntent(InterestsIntent.Add)
                            keyboard?.hide()
                        }
                    },
                    isEnable = state.canAdd
                )
            }

            if (state.interests.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = "You Interests",
                    style = MaterialTheme.LaskTypography.footnote,
                    color = MaterialTheme.LaskColors.textSecondary,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.interests.forEach { keyword ->
                        LaskChipButton(
                            modifier = Modifier,
                            text = keyword,
                            variant = LaskChipButtonVariants.Interests,
                            onDismiss = { onIntent(InterestsIntent.Remove(keyword)) }
                        )
                    }
                }
            }
        }
    }
}
