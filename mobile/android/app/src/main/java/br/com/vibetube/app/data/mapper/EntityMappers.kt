package br.com.vibetube.app.data.mapper

import br.com.vibetube.app.data.cache.entity.CommentEntity
import br.com.vibetube.app.data.cache.entity.IntroEntity
import br.com.vibetube.app.data.cache.entity.LikeEntity
import br.com.vibetube.app.data.cache.entity.VideoEntity
import br.com.vibetube.app.domain.model.VibeComment
import br.com.vibetube.app.domain.model.VibeIntro
import br.com.vibetube.app.domain.model.VibeVideo
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

object EntityMappers {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val stringListSerializer = ListSerializer(String.serializer())

    // ---- Video ----

    fun VibeVideo.toEntity(now: Long = System.currentTimeMillis()): VideoEntity = VideoEntity(
        id = id,
        blogId = blogId,
        postId = postId,
        source = source,
        title = title,
        intro = intro,
        description = description,
        videoUrl = videoUrl,
        embedUrl = embedUrl,
        thumbnailUrl = thumbnailUrl,
        postUrl = postUrl,
        commentsUrl = commentsUrl,
        authorName = authorName,
        publishedAt = publishedAt,
        updatedAt = updatedAt,
        labelsJson = json.encodeToString(stringListSerializer, labels),
        commentCount = commentCount,
        cachedAt = now
    )

    fun VideoEntity.toDomain(
        like: LikeEntity? = null,
        isSaved: Boolean = false
    ): VibeVideo {
        val labels = try {
            json.decodeFromString(stringListSerializer, labelsJson)
        } catch (_: Exception) {
            emptyList()
        }
        return VibeVideo(
            id = id,
            blogId = blogId,
            postId = postId,
            source = source,
            title = title,
            intro = intro,
            description = description,
            videoUrl = videoUrl,
            embedUrl = embedUrl,
            thumbnailUrl = thumbnailUrl,
            postUrl = postUrl,
            commentsUrl = commentsUrl,
            authorName = authorName,
            publishedAt = publishedAt,
            updatedAt = updatedAt,
            labels = labels,
            commentCount = commentCount,
            localLikeCount = like?.localLikeCount ?: 0,
            isLikedLocally = like?.isLiked ?: false,
            isSavedLocally = isSaved,
            canLike = true,
            canComment = true,
            canFollow = false,
            canShare = true,
            canInvite = true
        )
    }

    // ---- Comment ----

    fun VibeComment.toEntity(now: Long = System.currentTimeMillis()): CommentEntity = CommentEntity(
        id = id,
        postId = postId,
        blogId = blogId,
        authorName = authorName,
        authorAvatarUrl = authorAvatarUrl,
        content = content,
        publishedAt = publishedAt,
        updatedAt = updatedAt,
        status = status,
        source = source,
        cachedAt = now
    )

    fun CommentEntity.toDomain(): VibeComment = VibeComment(
        id = id,
        postId = postId,
        blogId = blogId,
        authorName = authorName,
        authorAvatarUrl = authorAvatarUrl,
        content = content,
        publishedAt = publishedAt,
        updatedAt = updatedAt,
        status = status,
        source = source
    )

    // ---- Intro ----

    fun VibeIntro.toEntity(now: Long = System.currentTimeMillis()): IntroEntity = IntroEntity(
        id = "current",
        title = title,
        subtitle = subtitle,
        body = body,
        sourceUrl = sourceUrl,
        updatedAt = updatedAt,
        cachedAt = now
    )

    fun IntroEntity.toDomain(): VibeIntro = VibeIntro(
        title = title,
        subtitle = subtitle,
        body = body,
        updatedAt = updatedAt,
        sourceUrl = sourceUrl
    )
}
