package br.com.vibetube.app.data.blogger

import br.com.vibetube.app.core.config.FeatureFlagManager
import br.com.vibetube.app.data.blogger.parser.BloggerHtmlParser
import br.com.vibetube.app.domain.model.VibeIntro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Busca o HTML da home do blog e tenta extrair a introdução pública.
 *
 * Estratégia:
 *   1. Busca o HTML.
 *   2. Procura blocos com palavras-chave conhecidas:
 *      - "Experiência App"
 *      - "Feed vertical em tela cheia"
 *      - meta description / og:description
 *   3. Limpa HTML e retorna intro.
 *   4. Se nada for encontrado, devolve fallback local.
 */
class BlogIntroDataSource(
    private val client: OkHttpClient,
    private val flags: FeatureFlagManager,
    private val htmlParser: BloggerHtmlParser = BloggerHtmlParser()
) {

    suspend fun fetchIntro(): Result<VibeIntro> = withContext(Dispatchers.IO) {
        try {
            val req = Request.Builder().url(flags.blogHomeUrl).get().build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    return@use Result.failure(IllegalStateException("HTTP ${resp.code}"))
                }
                val html = resp.body?.string() ?: ""

                val title = extractMetaProperty(html, "og:site_name")
                    ?: extractTagContent(html, "title")
                    ?: "VibeTube"

                val subtitle = extractMetaProperty(html, "og:description")
                    ?: extractMetaName(html, "description")
                    ?: "Vídeos verticais, tendências e cultura digital."

                val body = extractIntroBody(html) ?: subtitle

                val intro = VibeIntro(
                    title = htmlParser.cleanHtml(title, maxLen = 80).ifBlank { "VibeTube" },
                    subtitle = htmlParser.cleanHtml(subtitle, maxLen = 200),
                    body = htmlParser.cleanHtml(body, maxLen = 480),
                    updatedAt = null,
                    sourceUrl = flags.blogHomeUrl
                )
                Result.success(intro)
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    /**
     * Tenta achar bloco com a frase-chave "Experiência App" ou
     * "Feed vertical em tela cheia". Se não achar, devolve null.
     */
    private fun extractIntroBody(html: String): String? {
        val keywords = listOf(
            "Experiência App",
            "Experiencia App",
            "Feed vertical em tela cheia",
            "feed vertical em tela cheia"
        )
        for (kw in keywords) {
            val idx = html.indexOf(kw)
            if (idx >= 0) {
                // Pega ~600 chars ao redor para o cleaner extrair texto
                val start = (idx - 200).coerceAtLeast(0)
                val end = (idx + 600).coerceAtMost(html.length)
                return html.substring(start, end)
            }
        }
        return null
    }

    private fun extractMetaProperty(html: String, prop: String): String? {
        val r = Regex(
            """<meta[^>]+property\s*=\s*["']${Regex.escape(prop)}["'][^>]+content\s*=\s*["']([^"']+)["']""",
            RegexOption.IGNORE_CASE
        )
        return r.find(html)?.groupValues?.getOrNull(1)
    }

    private fun extractMetaName(html: String, name: String): String? {
        val r = Regex(
            """<meta[^>]+name\s*=\s*["']${Regex.escape(name)}["'][^>]+content\s*=\s*["']([^"']+)["']""",
            RegexOption.IGNORE_CASE
        )
        return r.find(html)?.groupValues?.getOrNull(1)
    }

    private fun extractTagContent(html: String, tag: String): String? {
        val r = Regex("""<${tag}[^>]*>([^<]+)</${tag}>""", RegexOption.IGNORE_CASE)
        return r.find(html)?.groupValues?.getOrNull(1)
    }
}
