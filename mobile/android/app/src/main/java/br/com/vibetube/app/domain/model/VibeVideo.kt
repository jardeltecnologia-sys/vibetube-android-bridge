package br.com.vibetube.app.domain.model

/**
 * Modelo unificado de vídeo. Vem do Blogger hoje, do Cloud no futuro.
 */
data class VibeVideo(
    val id: String,
    val blogId: String?,
    val postId: String?,
    val source: String,
    val title: String,
    val intro: String,
    val description: String,
    val videoUrl: String?,
    val embedUrl: String?,
    val thumbnailUrl: String?,
    val postUrl: String,
    val commentsUrl: String?,
    val authorName: String,
    val publishedAt: String,
    val updatedAt: String,
    val labels: List<String>,
    val commentCount: Int,
    val localLikeCount: Int,
    val isLikedLocally: Boolean,
    val isSavedLocally: Boolean,
    val canLike: Boolean,
    val canComment: Boolean,
    val canFollow: Boolean,
    val canShare: Boolean,
    val canInvite: Boolean
) {
    /**
     * Tipo do conteúdo de mídia detectado pelo parser.
     */
    val mediaKind: MediaKind get() = when {
        embedUrl?.contains("youtube", ignoreCase = true) == true ||
            embedUrl?.contains("youtu.be", ignoreCase = true) == true -> MediaKind.YOUTUBE_EMBED
        videoUrl?.endsWith(".m3u8", ignoreCase = true) == true -> MediaKind.HLS
        videoUrl?.endsWith(".mp4", ignoreCase = true) == true ||
            videoUrl?.endsWith(".webm", ignoreCase = true) == true -> MediaKind.DIRECT
        !thumbnailUrl.isNullOrBlank() -> MediaKind.THUMBNAIL_ONLY
        else -> MediaKind.NONE
    }

    enum class MediaKind { YOUTUBE_EMBED, DIRECT, HLS, THUMBNAIL_ONLY, NONE }
}
