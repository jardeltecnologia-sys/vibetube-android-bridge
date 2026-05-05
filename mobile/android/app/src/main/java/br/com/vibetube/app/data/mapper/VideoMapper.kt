package br.com.vibetube.app.data.mapper

import br.com.vibetube.app.data.blogger.parser.BloggerEntry
import br.com.vibetube.app.data.blogger.parser.BloggerFeed
import br.com.vibetube.app.data.blogger.parser.BloggerFeedParser
import br.com.vibetube.app.data.blogger.parser.BloggerHtmlParser
import br.com.vibetube.app.domain.model.VibeVideo

/**
 * Converte BloggerEntry em VibeVideo, extraindo vídeo/imagem/intro.
 *
 * Regras de fallback:
 *   - intro: summary > primeiros 320 chars do content (limpo)
 *   - thumbnail: media$thumbnail > YouTube hq > primeira img do content
 *   - vídeo: youtube embed > direct mp4/webm/m3u8
 */
class VideoMapper(
    private val htmlParser: BloggerHtmlParser = BloggerHtmlParser(),
    private val feedParser: BloggerFeedParser = BloggerFeedParser()
) {

    fun mapEntries(feed: BloggerFeed): List<VibeVideo> {
        val blogId = feedParser.extractBlogId(feed.id?.t)
        return feed.entries.mapNotNull { entry -> mapSingle(entry, blogId) }
    }

    fun mapSingle(entry: BloggerEntry, blogIdHint: String?): VibeVideo? {
        val postUrl = feedParser.extractPostUrl(entry.links) ?: return null
        val postId = feedParser.extractPostId(entry.id?.t)
        val blogId = blogIdHint ?: feedParser.extractBlogId(entry.id?.t)
        val id = entry.id?.t ?: postUrl

        val title = htmlParser.cleanHtml(entry.title?.t, maxLen = 200).ifBlank { "Sem título" }
        val rawContent = entry.content?.t.orEmpty()
        val rawSummary = entry.summary?.t.orEmpty()
        val description = htmlParser.cleanHtml(rawContent, maxLen = 1200)
        val intro = when {
            rawSummary.isNotBlank() -> htmlParser.cleanHtml(rawSummary, maxLen = 280)
            else -> htmlParser.cleanHtml(rawContent, maxLen = 280)
        }

        val media = htmlParser.extractMedia(rawContent)
        val embedUrl = media.youtubeEmbedUrl
        val videoUrl = if (embedUrl == null) media.directVideoUrl else null

        val thumb = entry.thumbnail?.url
            ?.let { upgradeBloggerThumb(it) }
            ?: htmlParser.youtubeThumbnail(media.youtubeId)
            ?: media.firstImageUrl

        val labels = entry.categories.mapNotNull { it.term }.distinct()
        val authorName = entry.authors.firstOrNull()?.name?.t ?: "VibeTube"
        val commentCount = entry.total?.t?.toIntOrNull() ?: 0
        val commentsUrl = feedParser.buildCommentsAnchorUrl(postUrl)

        return VibeVideo(
            id = id,
            blogId = blogId,
            postId = postId,
            source = "blogger",
            title = title,
            intro = intro,
            description = description,
            videoUrl = videoUrl,
            embedUrl = embedUrl,
            thumbnailUrl = thumb,
            postUrl = postUrl,
            commentsUrl = commentsUrl,
            authorName = authorName,
            publishedAt = entry.published?.t.orEmpty(),
            updatedAt = entry.updated?.t.orEmpty(),
            labels = labels,
            commentCount = commentCount,
            localLikeCount = 0,
            isLikedLocally = false,
            isSavedLocally = false,
            canLike = true,
            canComment = true,
            canFollow = false,
            canShare = true,
            canInvite = true
        )
    }

    /**
     * Blogger costuma servir thumbs em /sNN-c/. Aumentamos o tamanho para s640.
     */
    private fun upgradeBloggerThumb(url: String): String {
        return url.replace(Regex("""/s\d+(-c)?/"""), "/s640/")
    }
}
