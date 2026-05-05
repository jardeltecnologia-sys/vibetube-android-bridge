package br.com.vibetube.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.vibetube.app.core.utils.DateFormatter
import br.com.vibetube.app.domain.model.VibeVideo
import br.com.vibetube.app.ui.theme.VibeTextPrimary
import br.com.vibetube.app.ui.theme.VibeTextSecondary

/**
 * Overlay no canto inferior esquerdo com título, autor, data e intro do vídeo.
 */
@Composable
fun VideoMetadataOverlay(
    video: VibeVideo,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(gradient)
            .padding(start = 16.dp, end = 96.dp, top = 24.dp, bottom = 16.dp)
    ) {
        Text(
            text = "@${video.authorName}",
            style = MaterialTheme.typography.titleMedium,
            color = VibeTextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = video.title,
            style = MaterialTheme.typography.titleLarge,
            color = VibeTextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (video.intro.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = video.intro,
                style = MaterialTheme.typography.bodyMedium,
                color = VibeTextPrimary.copy(alpha = 0.85f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (video.publishedAt.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = DateFormatter.toRelative(video.publishedAt),
                style = MaterialTheme.typography.labelMedium,
                color = VibeTextSecondary
            )
        }
    }
}
