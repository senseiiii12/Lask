package dev.alexmester.lask.welcome_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskPalette
import dev.alexmester.ui.desing_system.LaskTheme
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun WelcomeScreen(
    onExploreClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(1f)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.LaskColors.brand_blue,
                            LaskPalette.Brand_BlueLight10,
                        ),
                    )
                )
        )

        Image(
            painter = painterResource(id = R.drawable.welcome_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.TopCenter),
        )


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter)
                .background(
                    color = MaterialTheme.LaskColors.backgroundPrimary,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                )
                .navigationBarsPadding()
                .padding(horizontal = 32.dp)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.weight(1f, fill = false),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.welcome_title),
                style = MaterialTheme.LaskTypography.h4,
                color = MaterialTheme.LaskColors.textPrimary,
                maxLines = 3,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 20.sp,
                    maxFontSize = MaterialTheme.LaskTypography.h4.fontSize.value.sp,
                    stepSize = 1.sp,
                ),
            )
            Text(
                modifier = Modifier.weight(1f, fill = false),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.LaskTypography.body2,
                color = MaterialTheme.LaskColors.textSecondary,
                maxLines = 5,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 8.sp,
                    maxFontSize = MaterialTheme.LaskTypography.body2.fontSize.value.sp,
                    stepSize = 1.sp,
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier.weight(1f, fill = false),
                onClick = onExploreClick,
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.LaskColors.brand_blue,
                    contentColor = Color.White,
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.welcome_explore),
                    style = MaterialTheme.LaskTypography.button1,
                )
            }
        }

    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D0D)
@Composable
private fun WelcomeScreenPreviewDark() {
    LaskTheme(darkTheme = true) {
        WelcomeScreen(onExploreClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D0D)
@Composable
private fun WelcomeScreenPreview() {
    LaskTheme(darkTheme = false) {
        WelcomeScreen(onExploreClick = {})
    }
}