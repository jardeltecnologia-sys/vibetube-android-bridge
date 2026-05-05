package br.com.vibetube.app.core.webview

import android.net.Uri

/**
 * Política de segurança da WebView.
 *
 * Princípio: a WebView só serve para dois fluxos específicos —
 *  1) embed do player do YouTube
 *  2) formulário oficial de comentários do Blogger
 *
 * Tudo fora dessa lista é bloqueado (com fallback opcional para abrir
 * em navegador externo).
 */
object SafeWebViewConfig {

    // Domínios permitidos para o WebView de COMENTÁRIOS do Blogger.
    private val COMMENTS_ALLOWED_HOSTS = listOf(
        "vibetube.com.br",
        "www.vibetube.com.br",
        "blogger.com",
        "www.blogger.com",
        "draft.blogger.com",
        "accounts.google.com",
        "accounts.youtube.com",
        "www.google.com",
        "www.gstatic.com",
        "ssl.gstatic.com",
        "fonts.gstatic.com",
        "fonts.googleapis.com",
        "ajax.googleapis.com",
        "apis.google.com",
        "www.googletagservices.com"
    )

    // Domínios permitidos para o WebView do PLAYER YouTube embed.
    private val YOUTUBE_ALLOWED_HOSTS = listOf(
        "youtube.com",
        "www.youtube.com",
        "m.youtube.com",
        "youtube-nocookie.com",
        "www.youtube-nocookie.com",
        "youtu.be",
        "i.ytimg.com",
        "ytimg.com",
        "s.ytimg.com",
        "yt3.ggpht.com",
        "googlevideo.com",
        "www.gstatic.com",
        "fonts.gstatic.com",
        "fonts.googleapis.com"
    )

    enum class Surface { COMMENTS, YOUTUBE_PLAYER }

    fun isAllowed(url: String?, surface: Surface): Boolean {
        if (url.isNullOrBlank()) return false
        // about:blank é usado pra reset; permitido.
        if (url == "about:blank") return true
        val host = try { Uri.parse(url).host?.lowercase() } catch (_: Exception) { null }
            ?: return false

        // Sempre exigir HTTPS (about:blank tratado acima)
        val scheme = try { Uri.parse(url).scheme?.lowercase() } catch (_: Exception) { null }
        if (scheme != "https") return false

        val list = when (surface) {
            Surface.COMMENTS -> COMMENTS_ALLOWED_HOSTS
            Surface.YOUTUBE_PLAYER -> YOUTUBE_ALLOWED_HOSTS
        }
        // Match exato OU subdomínio de algum host permitido (ex.: foo.gstatic.com)
        return list.any { allowed ->
            host == allowed || host.endsWith(".$allowed")
        }
    }
}
