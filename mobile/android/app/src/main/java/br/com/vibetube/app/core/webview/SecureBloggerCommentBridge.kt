package br.com.vibetube.app.core.webview

import android.webkit.JavascriptInterface

/**
 * Bridge JS minimalista para o WebView de comentários.
 *
 * SUPERFÍCIE INTENCIONALMENTE PEQUENA:
 *   - Apenas dois métodos, ambos só recebem strings curtas e atuam em callbacks Kotlin.
 *   - Nada que retorne dados sensíveis para o JS.
 *   - Nada que execute código nativo via reflexão.
 *
 * Nunca expor essa bridge em WebViews que carregam URLs arbitrárias — só
 * use no fluxo de comentários do Blogger, onde a whitelist é aplicada por
 * BloggerCommentWebViewClient.
 *
 * Em teoria, mesmo sem essa bridge o fluxo funciona — o usuário comenta
 * pelo formulário do Blogger e nós só refazemos o fetch dos comentários
 * quando ele fechar a WebView. Mantemos a bridge para pequenas notificações
 * úteis (ex.: "comentário enviado") que possam ser injetadas pelo template
 * do blog se desejado no futuro.
 */
class SecureBloggerCommentBridge(
    private val onCommentSubmitHint: () -> Unit = {},
    private val onCloseRequested: () -> Unit = {}
) {

    @JavascriptInterface
    fun notifyCommentSubmitted() {
        // Sem parâmetros; sem retorno sensível.
        onCommentSubmitHint()
    }

    @JavascriptInterface
    fun requestClose() {
        onCloseRequested()
    }

    companion object {
        const val NAME = "VibeTubeCommentBridge"
    }
}
