package br.com.vibetube.app.core.webview

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Cliente do WebView usado SOMENTE para tocar embed do YouTube.
 * Nada de comentários, nada de blog: apenas player.
 */
class YouTubeEmbedWebViewClient(
    private val onBlocked: (String) -> Unit = {}
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url?.toString()
        return if (SafeWebViewConfig.isAllowed(url, SafeWebViewConfig.Surface.YOUTUBE_PLAYER)) {
            false
        } else {
            url?.let(onBlocked)
            true
        }
    }
}
