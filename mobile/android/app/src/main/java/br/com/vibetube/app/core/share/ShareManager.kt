package br.com.vibetube.app.core.share

import android.content.Context
import br.com.vibetube.app.core.config.FeatureFlagManager
import br.com.vibetube.app.domain.model.VibeVideo
import br.com.vibetube.app.domain.usecase.ShareApkInviteUseCase
import br.com.vibetube.app.domain.usecase.ShareVideoUseCase

/**
 * Facade simples para a UI. Mantém uma única dependência exposta a Composables.
 */
class ShareManager(flags: FeatureFlagManager) {

    private val shareVideo = ShareVideoUseCase(flags)
    private val shareInvite = ShareApkInviteUseCase(flags)

    fun shareVideo(context: Context, video: VibeVideo) = shareVideo.share(context, video)

    fun inviteToInstall(context: Context) = shareInvite.share(context)
}
