package br.com.vibetube.app.core.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureFlagManagerTest {

    @Test
    fun `valores default em modo bridge`() {
        val flags = FeatureFlagManager(VibeTubeConfig())
        assertTrue(flags.isBridgeMode())
        assertFalse(flags.isCloudMode())
        assertEquals("https://www.vibetube.com.br/", flags.blogHomeUrl)
    }

    @Test
    fun `feature ausente retorna false`() {
        val flags = FeatureFlagManager(VibeTubeConfig(features = emptyMap()))
        assertFalse(flags.isEnabled(FeatureFlagManager.Flags.UPLOAD))
        assertFalse(flags.isEnabled(FeatureFlagManager.Flags.LOGIN))
    }

    @Test
    fun `feature true respeitada`() {
        val flags = FeatureFlagManager(
            VibeTubeConfig(
                features = mapOf(
                    FeatureFlagManager.Flags.FEED to true,
                    FeatureFlagManager.Flags.UPLOAD to false
                )
            )
        )
        assertTrue(flags.isEnabled(FeatureFlagManager.Flags.FEED))
        assertFalse(flags.isEnabled(FeatureFlagManager.Flags.UPLOAD))
    }

    @Test
    fun `cloud mode detectado`() {
        val flags = FeatureFlagManager(VibeTubeConfig(mode = "cloud"))
        assertTrue(flags.isCloudMode())
        assertFalse(flags.isBridgeMode())
    }
}
