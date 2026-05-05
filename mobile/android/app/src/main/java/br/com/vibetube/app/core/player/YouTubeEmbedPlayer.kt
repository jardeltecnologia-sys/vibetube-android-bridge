package br.com.vibetube.app.core.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import br.com.vibetube.app.core.webview.SafeWebView
import br.com.vibetube.app.core.webview.SafeWebViewConfig

/**
 * Player para vídeos do YouTube via embed.
 *
 * Usamos a forma `youtube-nocookie.com/embed/{id}` com parâmetros que pedem
 * autoplay, sem related videos do canal, sem branding e com playsinline.
 *
 * Importante: alguns navegadores bloqueiam autoplay com áudio. O usuário pode
 * precisar tocar uma vez para iniciar.
 */
@Composable
fun YouTubeEmbedPlayer(
    embedUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true
) {
    val finalUrl = remember(embedUrl, autoPlay) {
        buildEmbedUrl(embedUrl, autoPlay)
    }

    SafeWebView(
        url = finalUrl,
        surface = SafeWebViewConfig.Surface.YOUTUBE_PLAYER,
        modifier = modifier
    )
}

private fun buildEmbedUrl(input: String, autoPlay: Boolean): String {
    // Normaliza para nocookie
    val base = if (input.contains("youtube.com/embed/")) {
        input.replace("//www.youtube.com/", "//www.youtube-nocookie.com/")
            .replace("//youtube.com/", "//www.youtube-nocookie.com/")
    } else {
        input
    }
    val params = mutableListOf(
        "rel=0",
        "modestbranding=1",
        "playsinline=1"
    )
    if (autoPlay) params += "autoplay=1"
    val sep = if (base.contains("?")) "&" else "?"
    return base + sep + params.joinToString("&")
}
