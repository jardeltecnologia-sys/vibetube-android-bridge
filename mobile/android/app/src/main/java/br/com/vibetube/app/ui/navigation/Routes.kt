package br.com.vibetube.app.ui.navigation

/**
 * Todas as rotas do app em um só lugar.
 * Facilita manutenção e refatoração.
 */
object Routes {
    const val FEED = "feed"
    const val EXPLORE = "explore"
    const val ACTIVITY = "activity"
    const val PROFILE = "profile"
    const val INVITE = "invite"

    // Rotas com parâmetros
    const val COMMENTS = "comments/{videoId}"
    const val COMMENT_WEB = "commentWeb/{videoId}"
    const val STANDBY = "standby/{featureName}"

    // Helpers
    fun comments(videoId: String) = "comments/${videoId.encodeForRoute()}"
    fun commentWeb(videoId: String) = "commentWeb/${videoId.encodeForRoute()}"
    fun standby(featureName: String) = "standby/$featureName"

    // Argumentos
    const val ARG_VIDEO_ID = "videoId"
    const val ARG_FEATURE_NAME = "featureName"

    /**
     * IDs de vídeos do Blogger podem conter caracteres como ":", "/", ".".
     * Codificamos com Base64-URL pra não quebrar a rota.
     */
    private fun String.encodeForRoute(): String {
        return android.util.Base64.encodeToString(
            this.toByteArray(Charsets.UTF_8),
            android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP or android.util.Base64.NO_PADDING
        )
    }

    fun decodeFromRoute(encoded: String): String {
        return try {
            String(
                android.util.Base64.decode(
                    encoded,
                    android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP or android.util.Base64.NO_PADDING
                ),
                Charsets.UTF_8
            )
        } catch (_: Exception) {
            encoded
        }
    }
}
