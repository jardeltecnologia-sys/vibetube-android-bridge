package br.com.vibetube.app.core.player

import br.com.vibetube.app.domain.model.VibeVideo

/**
 * Resolve qual estratégia de player usar para um VibeVideo.
 */
object PlayerSelector {

    enum class Strategy { DIRECT_EXOPLAYER, YOUTUBE_WEBVIEW, THUMBNAIL_ONLY, NONE }

    fun strategyFor(video: VibeVideo): Strategy = when (video.mediaKind) {
        // Versão estável: vídeos do YouTube ficam como thumbnail dentro do feed.
        // Isso evita o erro 153 do player embutido em WebView.
        // O usuário ainda pode acessar o vídeo pelo link/post/compartilhamento.
        VibeVideo.MediaKind.YOUTUBE_EMBED -> Strategy.THUMBNAIL_ONLY
        VibeVideo.MediaKind.DIRECT, VibeVideo.MediaKind.HLS -> Strategy.DIRECT_EXOPLAYER
        VibeVideo.MediaKind.THUMBNAIL_ONLY -> Strategy.THUMBNAIL_ONLY
        VibeVideo.MediaKind.NONE -> Strategy.NONE
    }
}
