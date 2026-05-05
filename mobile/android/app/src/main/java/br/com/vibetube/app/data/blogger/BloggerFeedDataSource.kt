package br.com.vibetube.app.data.blogger

import br.com.vibetube.app.core.config.FeatureFlagManager
import br.com.vibetube.app.data.blogger.parser.BloggerFeed
import br.com.vibetube.app.data.blogger.parser.BloggerFeedParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Busca o feed JSON do Blogger e devolve o objeto BloggerFeed cru.
 * O mapeamento para VibeVideo acontece no Repository.
 */
class BloggerFeedDataSource(
    private val client: OkHttpClient,
    private val flags: FeatureFlagManager,
    private val parser: BloggerFeedParser = BloggerFeedParser()
) {

    suspend fun fetchFeed(startIndex: Int = 1, maxResults: Int = 50): Result<BloggerFeed> =
        withContext(Dispatchers.IO) {
            try {
                val url = buildUrl(startIndex, maxResults)
                val req = Request.Builder().url(url).get().build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        return@use Result.failure(
                            IllegalStateException("HTTP ${resp.code} ao buscar feed")
                        )
                    }
                    val body = resp.body?.string()
                        ?: return@use Result.failure(IllegalStateException("Resposta vazia"))
                    val feed = parser.parse(body)
                        ?: return@use Result.failure(IllegalStateException("Falha ao decodificar feed"))
                    Result.success(feed)
                }
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }

    private fun buildUrl(startIndex: Int, maxResults: Int): String {
        val base = flags.blogFeedUrl
        // Se o usuário já passou parâmetros, respeitamos. Caso contrário, anexamos start-index.
        return if (startIndex <= 1) {
            base
        } else {
            val sep = if (base.contains("?")) "&" else "?"
            "$base${sep}start-index=$startIndex&max-results=$maxResults"
        }
    }

    val parserRef: BloggerFeedParser get() = parser
}
