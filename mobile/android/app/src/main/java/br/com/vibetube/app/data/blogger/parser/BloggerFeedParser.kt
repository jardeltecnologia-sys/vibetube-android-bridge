package br.com.vibetube.app.data.blogger.parser

import kotlinx.serialization.json.Json

/**
 * Parser do JSON do feed do Blogger.
 * Extrai blogId, postId, postUrl e outros campos estruturais.
 */
class BloggerFeedParser(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
) {

    fun parse(rawJson: String): BloggerFeed? {
        return try {
            json.decodeFromString(BloggerFeedRoot.serializer(), rawJson).feed
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extrai blogId de strings tipo:
     *   tag:blogger.com,1999:blog-1234567890
     */
    fun extractBlogId(feedId: String?): String? {
        if (feedId.isNullOrBlank()) return null
        val regex = Regex("""tag:blogger\.com,\d+:blog-(\d+)""")
        return regex.find(feedId)?.groupValues?.getOrNull(1)
    }

    /**
     * Extrai postId de strings tipo:
     *   tag:blogger.com,1999:blog-1234.post-5678
     */
    fun extractPostId(entryId: String?): String? {
        if (entryId.isNullOrBlank()) return null
        val regex = Regex("""tag:blogger\.com,\d+:blog-\d+\.post-(\d+)""")
        return regex.find(entryId)?.groupValues?.getOrNull(1)
    }

    /**
     * Encontra o link público (rel=alternate) do post.
     */
    fun extractPostUrl(links: List<BloggerLink>): String? {
        return links.firstOrNull { it.rel == "alternate" && (it.type == null || it.type.contains("html", true)) }?.href
            ?: links.firstOrNull { it.rel == "alternate" }?.href
    }

    /**
     * Constrói URL para a âncora de comentários do post.
     */
    fun buildCommentsAnchorUrl(postUrl: String?): String? {
        if (postUrl.isNullOrBlank()) return null
        return if (postUrl.contains("#")) postUrl else "$postUrl#comments"
    }
}
