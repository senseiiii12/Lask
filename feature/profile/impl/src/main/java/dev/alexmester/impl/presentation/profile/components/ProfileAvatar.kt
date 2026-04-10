package dev.alexmester.impl.presentation.profile.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.alexmester.ui.components.avatars.AuthorAvatar
import dev.alexmester.ui.desing_system.LaskColors

@Composable
fun ProfileAvatar(
    modifier: Modifier = Modifier,
    displayUri: Uri?,
    name: String,
    isEdit: Boolean,
    onClick: () -> Unit,
) {
    val colorStops = arrayOf(
        0.0f to MaterialTheme.LaskColors.brand_blue10,
        0.4f to MaterialTheme.LaskColors.brand_blue10.copy(alpha = 0.4f),
        1f to Color.Transparent
    )

    Box(
        modifier = modifier
            .size(120.dp)
            .then(
                if (isEdit) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.LaskColors.textLink,
                    shape = CircleShape,
                ) else Modifier
            )
            .clip(CircleShape)
            .clickable(enabled = isEdit) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (isEdit) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .zIndex(2f),
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = MaterialTheme.LaskColors.textLink,
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colorStops = colorStops
                        )
                    )
                    .clip(CircleShape)
                    .zIndex(1f)
            )
        }
        if (displayUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(displayUri)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
            )
        } else {
            AuthorAvatar(
                modifier = Modifier.size(120.dp),
                char = name.firstOrNull()?.uppercase() ?: "A",
            )
        }
    }
}