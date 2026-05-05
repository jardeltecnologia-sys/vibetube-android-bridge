package br.com.vibetube.app.core.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import br.com.vibetube.app.domain.model.VibeVideo

/**
 * Player de alto nível: recebe um VibeVideo e escolhe automaticamente
 * a melhor estratégia para tocar o conteúdo.
 */
@Composable
fun VibeVideoPlayer(
    video: VibeVideo,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true
) {
    val strategy = PlayerSelector.strategyFor(video)
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        when (strategy) {
            PlayerSelector.Strategy.YOUTUBE_WEBVIEW -> {
                video.embedUrl?.let { url ->
                    YouTubeEmbedPlayer(
                        embedUrl = url,
                        autoPlay = autoPlay,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            PlayerSelector.Strategy.DIRECT_EXOPLAYER -> {
                video.videoUrl?.let { url ->
                    DirectExoPlayer(
                        videoUrl = url,
                        autoPlay = autoPlay,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            PlayerSelector.Strategy.THUMBNAIL_ONLY -> {
                video.thumbnailUrl?.let { thumb ->
                    AsyncImage(
                        model = thumb,
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            PlayerSelector.Strategy.NONE -> {
                Text(
                    text = "Sem mídia disponível",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
