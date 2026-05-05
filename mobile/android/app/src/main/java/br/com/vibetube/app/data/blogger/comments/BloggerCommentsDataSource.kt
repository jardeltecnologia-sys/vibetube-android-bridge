package br.com.vibetube.app.data.blogger.comments

import br.com.vibetube.app.domain.model.VibeComment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Leitura de comentários públicos via Blogger API v3.
 *
 * Endpoint:
 *   GET https://www.googleapis.com/blogger/v3/blogs/{blogId}/posts/{postId}/comments
 *       ?fetchBodies=true&maxResults=50&key={apiKey}
 *
 * Sem apiKey/OAuth, alguns blogs respondem com 403. Nesse caso, devolvemos lista
 * vazia e a UI mostra o estado "Nenhum comentário ainda" + CTA pra comentar via
 * WebView no formulário oficial do Blogger.
 *
 * A API key pode ser injetada via `BuildConfig.BLOGGER_API_KEY` (não obrigatório).
 */
class BloggerCommentsDataSource(
    private val client: OkHttpClient,
    private val apiKey: String? = null,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
) {

    suspend fun fetchComments(
        blogId: String,
        postId: String,
        maxResults: Int = 50
    ): Result<List<VibeComment>> = withContext(Dispatchers.IO) {
        try {
            val url = buildEndpoint(blogId, postId, maxResults)
            val req = Request.Builder().url(url).get().build()
            client.newCall(req).execute().use { resp ->
                if (resp.code == 403 || resp.code == 401) {
                    // Acesso restrito — devolvemos lista vazia (UI lida com isso).
                    return@use Result.success(emptyList())
                }
                if (!resp.isSuccessful) {
                    return@use Result.failure(IllegalStateException("HTTP ${resp.code}"))
                }
                val body = resp.body?.string()
                    ?: return@use Result.failure(IllegalStateException("Resposta vazia"))
                val parsed = json.decodeFromString(CommentsListResponse.serializer(), body)
                val mapped = parsed.items.mapNotNull { it.toDomain(blogId, postId) }
                Result.success(mapped)
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    /**
     * Constrói URL do endpoint público de comentários.
     * O método é exposto (internal) para facilitar testes unitários.
     */
    fun buildEndpoint(blogId: String, postId: String, maxResults: Int): String {
        val base = "https://www.googleapis.com/blogger/v3/blogs/$blogId/posts/$postId/comments"
        val params = mutableListOf("fetchBodies=true", "maxResults=$maxResults")
        if (!apiKey.isNullOrBlank()) params += "key=$apiKey"
        return base + "?" + params.joinToString("&")
    }

    private fun BloggerCommentDto.toDomain(blogIdFallback: String, postIdFallback: String): VibeComment? {
        val rawId = id ?: return null
        return VibeComment(
            id = rawId,
            postId = post?.id ?: postIdFallback,
            blogId = blog?.id ?: blogIdFallback,
            authorName = author?.displayName ?: "Anônimo",
            authorAvatarUrl = author?.image?.url,
            content = stripHtml(content ?: ""),
            publishedAt = published ?: "",
            updatedAt = updated,
            status = status ?: "live",
            source = "blogger"
        )
    }

    private fun stripHtml(s: String): String {
        var x = s
        x = x.replace(Regex("""<br\s*/?>""", RegexOption.IGNORE_CASE), "\n")
        x = x.replace(Regex("""<[^>]+>"""), "")
        x = x
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
        return x.trim()
    }
}
