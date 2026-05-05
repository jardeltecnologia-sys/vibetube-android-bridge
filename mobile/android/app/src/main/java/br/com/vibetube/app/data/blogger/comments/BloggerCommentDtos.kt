package br.com.vibetube.app.data.blogger.comments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTOs para Blogger API v3 — endpoint de comentários:
 *   GET /blogger/v3/blogs/{blogId}/posts/{postId}/comments
 *
 * Esse endpoint pode requerer chave de API ou OAuth dependendo do blog.
 * Em produção, configure a apiKey via BuildConfig ou repository remoto.
 */

@Serializable
data class CommentsListResponse(
    @SerialName("kind") val kind: String? = null,
    @SerialName("nextPageToken") val nextPageToken: String? = null,
    @SerialName("items") val items: List<BloggerCommentDto> = emptyList()
)

@Serializable
data class BloggerCommentDto(
    @SerialName("id") val id: String? = null,
    @SerialName("post") val post: PostRef? = null,
    @SerialName("blog") val blog: BlogRef? = null,
    @SerialName("published") val published: String? = null,
    @SerialName("updated") val updated: String? = null,
    @SerialName("content") val content: String? = null,
    @SerialName("author") val author: AuthorDto? = null,
    @SerialName("status") val status: String? = null
)

@Serializable
data class PostRef(@SerialName("id") val id: String? = null)

@Serializable
data class BlogRef(@SerialName("id") val id: String? = null)

@Serializable
data class AuthorDto(
    @SerialName("id") val id: String? = null,
    @SerialName("displayName") val displayName: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("image") val image: AuthorImage? = null
)

@Serializable
data class AuthorImage(@SerialName("url") val url: String? = null)
