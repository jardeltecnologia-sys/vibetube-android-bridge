package br.com.vibetube.app.domain.usecase

import br.com.vibetube.app.core.config.FeatureFlagManager
import br.com.vibetube.app.core.config.VibeTubeConfig
import br.com.vibetube.app.domain.model.VibeVideo
import org.junit.Assert.assertTrue
import org.junit.Test

class ShareVideoUseCaseTest {

    private fun makeFlags(
        inviteUrl: String = "https://appdistribution.firebase.dev/i/538c49560ae4a639",
        shareEnabled: Boolean = true
    ): FeatureFlagManager {
        return FeatureFlagManager(
            VibeTubeConfig(
                firebaseApkInviteUrl = inviteUrl,
                features = mapOf(
                    FeatureFlagManager.Flags.SHARE to shareEnabled,
                    FeatureFlagManager.Flags.INVITE_APK to true
                )
            )
        )
    }

    private fun makeVideo(): VibeVideo = VibeVideo(
        id = "post-1",
        blogId = "111",
        postId = "222",
        source = "blogger",
        title = "Vídeo de teste",
        intro = "Introdução curta do vídeo.",
        description = "desc",
        videoUrl = null,
        embedUrl = "https://www.youtube.com/embed/abc",
        thumbnailUrl = null,
        postUrl = "https://www.vibetube.com.br/2024/01/post.html",
        commentsUrl = "https://www.vibetube.com.br/2024/01/post.html#comments",
        authorName = "VibeTube",
        publishedAt = "2024-01-01T10:00:00.000-03:00",
        updatedAt = "",
        labels = emptyList(),
        commentCount = 0,
        localLikeCount = 0,
        isLikedLocally = false,
        isSavedLocally = false,
        canLike = true,
        canComment = true,
        canFollow = false,
        canShare = true,
        canInvite = true
    )

    @Test
    fun `texto contem titulo intro postUrl e link do APK`() {
        val text = ShareVideoUseCase(makeFlags()).buildShareText(makeVideo())
        assertTrue("falta titulo: $text", text.contains("Vídeo de teste"))
        assertTrue("falta intro: $text", text.contains("Introdução curta"))
        assertTrue(
            "falta postUrl: $text",
            text.contains("https://www.vibetube.com.br/2024/01/post.html")
        )
        assertTrue(
            "falta link APK: $text",
            text.contains("https://appdistribution.firebase.dev/i/538c49560ae4a639")
        )
    }

    @Test
    fun `texto comeca com Assista no VibeTube`() {
        val text = ShareVideoUseCase(makeFlags()).buildShareText(makeVideo())
        assertTrue(text.startsWith("Assista no VibeTube:"))
    }

    @Test
    fun `link APK respeita URL configurada`() {
        val flags = makeFlags(inviteUrl = "https://example.com/app")
        val text = ShareVideoUseCase(flags).buildShareText(makeVideo())
        assertTrue(text.contains("https://example.com/app"))
    }

    @Test
    fun `ShareApkInviteUseCase compoe convite`() {
        val flags = makeFlags(inviteUrl = "https://x.com/app")
        val text = ShareApkInviteUseCase(flags).buildInviteText()
        assertTrue(text.startsWith("Instale o VibeTube"))
        assertTrue(text.contains("https://x.com/app"))
    }
}
