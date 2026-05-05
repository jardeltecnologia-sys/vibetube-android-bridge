package br.com.vibetube.app.domain.usecase

import br.com.vibetube.app.core.config.FeatureFlagManager
import br.com.vibetube.app.core.config.VibeTubeConfig
import br.com.vibetube.app.domain.repository.LikesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class ToggleLikeUseCaseTest {

    /** Fake simples para LikesRepository. */
    private class FakeLikes : LikesRepository {
        var liked = false
        override suspend fun isLiked(videoId: String): Boolean = liked
        override fun observeLiked(videoId: String): Flow<Boolean> = flowOf(liked)
        override suspend fun toggle(videoId: String, postId: String?): Boolean {
            liked = !liked
            return liked
        }
    }

    @Test
    fun `Disabled quando todas as flags off`() = runTest {
        val flags = FeatureFlagManager(VibeTubeConfig(features = emptyMap()))
        val useCase = ToggleLikeUseCase(FakeLikes(), flags)
        val result = useCase("v1", null, firstLikeOfSession = true)
        assertTrue(result is ToggleLikeUseCase.Result.Disabled)
    }

    @Test
    fun `Liked com showNotice quando first e likesLocal=true`() = runTest {
        val flags = FeatureFlagManager(
            VibeTubeConfig(features = mapOf(FeatureFlagManager.Flags.LIKES_LOCAL to true))
        )
        val useCase = ToggleLikeUseCase(FakeLikes(), flags)
        val result = useCase("v1", null, firstLikeOfSession = true)
        assertTrue(result is ToggleLikeUseCase.Result.Liked)
        assertTrue((result as ToggleLikeUseCase.Result.Liked).showLocalNotice)
    }

    @Test
    fun `Liked sem showNotice quando nao first`() = runTest {
        val flags = FeatureFlagManager(
            VibeTubeConfig(features = mapOf(FeatureFlagManager.Flags.LIKES_LOCAL to true))
        )
        val useCase = ToggleLikeUseCase(FakeLikes(), flags)
        val result = useCase("v1", null, firstLikeOfSession = false)
        assertTrue(result is ToggleLikeUseCase.Result.Liked)
        assertTrue(!(result as ToggleLikeUseCase.Result.Liked).showLocalNotice)
    }

    @Test
    fun `Unliked apos segundo toggle`() = runTest {
        val flags = FeatureFlagManager(
            VibeTubeConfig(features = mapOf(FeatureFlagManager.Flags.LIKES_LOCAL to true))
        )
        val likes = FakeLikes()
        val useCase = ToggleLikeUseCase(likes, flags)
        useCase("v1", null, firstLikeOfSession = true)
        val second = useCase("v1", null, firstLikeOfSession = false)
        assertTrue(second is ToggleLikeUseCase.Result.Unliked)
    }

    @Test
    fun `BlogReactionDetector identifica ReactionsBar`() {
        val det = BlogReactionDetector()
        val result = det.detect("""<div id="ReactionsBar1">stuff</div>""")
        assertTrue(result.available)
        assertTrue(result.widgetType == BlogReactionDetector.WidgetType.REACTIONS_BAR)
    }

    @Test
    fun `BlogReactionDetector none para html sem widget`() {
        val det = BlogReactionDetector()
        val result = det.detect("""<p>texto comum</p>""")
        assertTrue(!result.available)
        assertTrue(result.widgetType == BlogReactionDetector.WidgetType.NONE)
    }

    @Test
    fun `BlogReactionDetector none para html nulo`() {
        val det = BlogReactionDetector()
        val result = det.detect(null)
        assertTrue(!result.available)
    }
}
