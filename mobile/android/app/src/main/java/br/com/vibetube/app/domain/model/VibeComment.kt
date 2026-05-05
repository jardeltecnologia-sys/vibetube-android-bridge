package br.com.vibetube.app.domain.model

data class VibeComment(
    val id: String,
    val postId: String,
    val blogId: String?,
    val authorName: String,
    val authorAvatarUrl: String?,
    val content: String,
    val publishedAt: String,
    val updatedAt: String?,
    val status: String,
    val source: String
)

data class VibeIntro(
    val title: String,
    val subtitle: String,
    val body: String,
    val updatedAt: String?,
    val sourceUrl: String
)
