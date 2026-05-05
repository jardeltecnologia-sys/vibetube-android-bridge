package br.com.vibetube.app.core.webview

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView

/**
 * Aplica defaults seguros a um WebView.
 *
 * Princípios:
 *  - file access desabilitado
 *  - content access desabilitado
 *  - mixed content bloqueado
 *  - JS habilitado SOMENTE quando explicitamente requerido
 *  - geolocation/storage só quando necessário (false aqui)
 */
object WebViewSecurityDefaults {

    @SuppressLint("SetJavaScriptEnabled")
    fun apply(webView: WebView, javascriptEnabled: Boolean) {
        val s: WebSettings = webView.settings
        s.javaScriptEnabled = javascriptEnabled
        s.domStorageEnabled = javascriptEnabled  // necessário para login Google e player YT

        // File access — sempre off
        s.allowFileAccess = false
        s.allowContentAccess = false
        @Suppress("DEPRECATION")
        s.allowFileAccessFromFileURLs = false
        @Suppress("DEPRECATION")
        s.allowUniversalAccessFromFileURLs = false

        // Cookies (exigido pelo login Google quando comentando)
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        // Mixed content
        s.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW

        // Geolocation: off
        s.setGeolocationEnabled(false)

        // Cache padrão
        s.cacheMode = WebSettings.LOAD_DEFAULT

        // Save form/password — off
        @Suppress("DEPRECATION")
        s.saveFormData = false

        // User-agent: deixar default do WebView para máxima compatibilidade
        // (o User-Agent customizado fica só nas chamadas OkHttp)

        // Algoritmo de zoom — desligado pra evitar layout quebrado
        s.builtInZoomControls = false
        s.displayZoomControls = false

        // Proteção contra navegação por overlay malicioso
        webView.isLongClickable = false
        webView.setOnLongClickListener { true }

        // Limpa qualquer JS interface previamente registrada
        // (chamadores que precisam adicionam de novo após este apply)
    }
}
