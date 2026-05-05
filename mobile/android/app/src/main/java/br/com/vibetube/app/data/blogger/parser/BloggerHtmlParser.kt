package br.com.vibetube.app.data.blogger.parser

/**
 * Parser de HTML dos posts do Blogger. Extrai:
 *  - URLs de YouTube (watch, embed, youtu.be, shorts)
 *  - URLs diretas de vídeo (mp4, webm, m3u8)
 *  - Primeira imagem (para thumbnail)
 *  - Texto limpo (para introdução)
 *
 * Implementação por regex propositalmente — é robusta para HTML do Blogger
 * e evita uma dependência pesada de Jsoup só para isso.
 */
class BloggerHtmlParser {

    data class ExtractedMedia(
        val youtubeId: String? = null,
        val youtubeEmbedUrl: String? = null,
        val youtubeWatchUrl: String? = null,
        val directVideoUrl: String? = null,
        val firstImageUrl: String? = null
    )

    /**
     * Extrai todas as mídias possíveis do HTML.
     * Prioridade: YouTube > vídeo direto > imagem.
     */
    fun extractMedia(html: String?): ExtractedMedia {
        if (html.isNullOrBlank()) return ExtractedMedia()

        val youtubeId = extractYouTubeId(html)
        val embedUrl = youtubeId?.let { "https://www.youtube.com/embed/$it" }
        val watchUrl = youtubeId?.let { "https://www.youtube.com/watch?v=$it" }
        val direct = extractDirectVideoUrl(html)
        val image = extractFirstImageUrl(html)

        return ExtractedMedia(
            youtubeId = youtubeId,
            youtubeEmbedUrl = embedUrl,
            youtubeWatchUrl = watchUrl,
            directVideoUrl = direct,
            firstImageUrl = image
        )
    }

    /**
     * Procura ID do YouTube em várias formas:
     *   youtube.com/embed/VIDEOID
     *   youtube.com/watch?v=VIDEOID
     *   youtu.be/VIDEOID
     *   youtube.com/shorts/VIDEOID
     *   youtube-nocookie.com/embed/VIDEOID
     */
    fun extractYouTubeId(html: String): String? {
        val patterns = listOf(
            Regex("""youtube(?:-nocookie)?\.com/embed/([A-Za-z0-9_-]{6,})"""),
            Regex("""youtube\.com/watch\?[^"'\s]*v=([A-Za-z0-9_-]{6,})"""),
            Regex("""youtu\.be/([A-Za-z0-9_-]{6,})"""),
            Regex("""youtube\.com/shorts/([A-Za-z0-9_-]{6,})""")
        )
        for (p in patterns) {
            val m = p.find(html) ?: continue
            val id = m.groupValues.getOrNull(1)?.takeIf { it.isNotBlank() }
            // Trunca em 11 chars (padrão do YT) caso a regex tenha capturado mais
            if (!id.isNullOrBlank()) {
                return if (id.length >= 11) id.substring(0, 11) else id
            }
        }
        return null
    }

    /**
     * Procura .mp4, .webm ou .m3u8 em src= ou href=.
     */
    fun extractDirectVideoUrl(html: String): String? {
        val pattern = Regex(
            """(?:src|href)\s*=\s*["']([^"']+\.(?:mp4|webm|m3u8)(?:\?[^"']*)?)["']""",
            RegexOption.IGNORE_CASE
        )
        return pattern.find(html)?.groupValues?.getOrNull(1)
    }

    /**
     * Pega o primeiro <img src="..."> do HTML.
     */
    fun extractFirstImageUrl(html: String): String? {
        val pattern = Regex(
            """<img[^>]+src\s*=\s*["']([^"']+)["']""",
            RegexOption.IGNORE_CASE
        )
        return pattern.find(html)?.groupValues?.getOrNull(1)
    }

    /**
     * Limpa HTML para texto puro.
     * Remove tags, scripts, styles. Decodifica entidades comuns.
     */
    fun cleanHtml(html: String?, maxLen: Int = 320): String {
        if (html.isNullOrBlank()) return ""
        var s = html

        // Remove blocos completos
        s = s.replace(Regex("""<script\b[^<]*(?:(?!</script>)<[^<]*)*</script>""", RegexOption.IGNORE_CASE), " ")
        s = s.replace(Regex("""<style\b[^<]*(?:(?!</style>)<[^<]*)*</style>""", RegexOption.IGNORE_CASE), " ")
        s = s.replace(Regex("""<!--.*?-->""", setOf(RegexOption.DOT_MATCHES_ALL)), " ")

        // Quebras
        s = s.replace(Regex("""<br\s*/?>""", RegexOption.IGNORE_CASE), "\n")
        s = s.replace(Regex("""</p\s*>""", RegexOption.IGNORE_CASE), "\n")

        // Remove tags
        s = s.replace(Regex("""<[^>]+>"""), " ")

        // Decodifica entidades mais comuns
        s = s
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
            .replace("&hellip;", "…")
            .replace("&ndash;", "–")
            .replace("&mdash;", "—")

        // Decodifica entidades numéricas decimais &#1234;
        s = Regex("""&#(\d+);""").replace(s) { m ->
            try { m.groupValues[1].toInt().toChar().toString() } catch (_: Exception) { " " }
        }
        // Hexadecimais &#x1F600;
        s = Regex("""&#x([0-9A-Fa-f]+);""").replace(s) { m ->
            try { m.groupValues[1].toInt(16).toChar().toString() } catch (_: Exception) { " " }
        }

        // Normaliza whitespace
        s = s.replace(Regex("""[\t\r ]+"""), " ")
        s = s.replace(Regex("""\n{3,}"""), "\n\n")
        s = s.trim()

        if (maxLen > 0 && s.length > maxLen) {
            // Corta na última quebra de palavra próxima
            val cut = s.substring(0, maxLen)
            val lastSpace = cut.lastIndexOf(' ')
            s = if (lastSpace > maxLen / 2) cut.substring(0, lastSpace) else cut
            s = "$s…"
        }
        return s
    }

    /**
     * Constrói thumbnail do YouTube se o ID estiver disponível.
     */
    fun youtubeThumbnail(videoId: String?, quality: YouTubeThumbQuality = YouTubeThumbQuality.HQ): String? {
        if (videoId.isNullOrBlank()) return null
        val q = when (quality) {
            YouTubeThumbQuality.DEFAULT -> "default"
            YouTubeThumbQuality.MQ -> "mqdefault"
            YouTubeThumbQuality.HQ -> "hqdefault"
            YouTubeThumbQuality.SD -> "sddefault"
            YouTubeThumbQuality.MAX -> "maxresdefault"
        }
        return "https://i.ytimg.com/vi/$videoId/$q.jpg"
    }

    enum class YouTubeThumbQuality { DEFAULT, MQ, HQ, SD, MAX }
}
