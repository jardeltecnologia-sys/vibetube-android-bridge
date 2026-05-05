package br.com.vibetube.app.core.webview

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Composable que embrulha uma WebView com defaults seguros.
 *
 * @param url URL inicial — DEVE estar na whitelist da `surface`.
 * @param surface Determina qual whitelist aplicar (comentários ou player YouTube).
 * @param onPageStarted callback quando uma navegação inicia.
 * @param onPageFinished callback quando termina.
 * @param onBlocked callback para URLs bloqueadas (UI pode oferecer abrir externo).
 * @param jsBridge bridge opcional. Só aplique quando absolutamente necessário.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SafeWebView(
    url: String,
    surface: SafeWebViewConfig.Surface,
    modifier: Modifier = Modifier,
    onPageStarted: (String) -> Unit = {},
    onPageFinished: (String) -> Unit = {},
    onBlocked: (String) -> Unit = {},
    jsBridge: Pair<Any, String>? = null
) {
    // Pré-checa a URL inicial — se for bloqueada, nem cria a WebView.
    val safeStartUrl = remember(url, surface) {
        if (SafeWebViewConfig.isAllowed(url, surface)) url else "about:blank"
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                WebViewSecurityDefaults.apply(this, javascriptEnabled = true)

                webViewClient = when (surface) {
                    SafeWebViewConfig.Surface.COMMENTS -> BloggerCommentWebViewClient(
                        onPageStarted = onPageStarted,
                        onPageFinished = onPageFinished,
                        onBlockedUrl = onBlocked
                    )
                    SafeWebViewConfig.Surface.YOUTUBE_PLAYER -> YouTubeEmbedWebViewClient(
                        onBlocked = onBlocked
                    )
                }
                jsBridge?.let { (obj, name) ->
                    addJavascriptInterface(obj, name)
                }
                loadUrl(safeStartUrl)
            }
        },
        update = { webView ->
            // Não recarregamos automaticamente em recomposições para não interromper.
            // Caller pode forçar reload através de uma key na WebView (recomendado: key {url}).
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            // Cleanup mínimo (a WebView é destruída pelo Compose)
        }
    }
}
