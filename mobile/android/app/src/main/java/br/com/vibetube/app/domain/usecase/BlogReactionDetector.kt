package br.com.vibetube.app.domain.usecase

/**
 * Inspeciona o HTML de um post do Blogger procurando por widgets de reação/curtida
 * embutidos no template (ex.: ReactionsBar1).
 *
 * Por enquanto apenas reporta se há widget — não tenta postar reação porque:
 *   1. Não há endpoint público estável universal.
 *   2. Cada template pode ter widget diferente.
 *
 * Quando flags.likesBlogReaction=true e este detector reportar `available=true`,
 * a UI poderá oferecer o caminho via WebView (mesma lógica de comentários).
 */
class BlogReactionDetector {

    data class Detection(
        val available: Boolean,
        val widgetType: WidgetType,
        val anchor: String?
    )

    enum class WidgetType { NONE, REACTIONS_BAR, GENERIC_PLUSONE }

    fun detect(html: String?): Detection {
        if (html.isNullOrBlank()) {
            return Detection(false, WidgetType.NONE, null)
        }
        // Widget oficial "Reactions" do Blogger
        if (html.contains("ReactionsBar", ignoreCase = true) ||
            html.contains("reactions-iframe", ignoreCase = true)
        ) {
            return Detection(true, WidgetType.REACTIONS_BAR, "#reactions")
        }
        // Plus-one / outro widget
        if (html.contains("g:plusone", ignoreCase = true)) {
            return Detection(true, WidgetType.GENERIC_PLUSONE, null)
        }
        return Detection(false, WidgetType.NONE, null)
    }
}
