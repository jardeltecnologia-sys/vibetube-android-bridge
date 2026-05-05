package br.com.vibetube.app.core.webview

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Cliente WebView que aplica whitelist de domínios para o fluxo de comentários
 * do Blogger. Qualquer URL fora da whitelist é bloqueada — caller pode abrir
 * no navegador externo se desejar.
 */
class BloggerCommentWebViewClient(
    private val onPageStarted: (String) -> Unit = {},
    private val onPageFinished: (String) -> Unit = {},
    private val onBlockedUrl: (String) -> Unit = {}
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url?.toString()
        return if (SafeWebViewConfig.isAllowed(url, SafeWebViewConfig.Surface.COMMENTS)) {
            // Permitir carregar dentro da própria WebView
            false
        } else {
            // Bloqueia e notifica
            url?.let(onBlockedUrl)
            true
        }
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        val url = request?.url?.toString()
        return if (!SafeWebViewConfig.isAllowed(url, SafeWebViewConfig.Surface.COMMENTS)) {
            // Resposta vazia para subrecursos não permitidos
            WebResourceResponse("text/plain", "utf-8", emptyBody())
        } else {
            null
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        url?.let(onPageStarted)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        url?.let(onPageFinished)
    }

    private fun emptyBody() = "".byteInputStream(Charsets.UTF_8)
}
