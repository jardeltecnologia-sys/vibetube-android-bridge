package br.com.vibetube.app.domain.usecase

import android.content.Context
import android.content.Intent
import br.com.vibetube.app.core.config.FeatureFlagManager

/**
 * Compartilha o link de instalação do APK (Firebase App Distribution).
 *
 * Texto:
 *   Instale o VibeTube e acompanhe os vídeos pelo app:
 *   {firebaseApkInviteUrl}
 */
class ShareApkInviteUseCase(
    private val flags: FeatureFlagManager
) {

    fun buildInviteText(): String {
        return "Instale o VibeTube e acompanhe os vídeos pelo app:\n\n${flags.firebaseApkInviteUrl}"
    }

    fun share(context: Context) {
        if (!flags.isEnabled(FeatureFlagManager.Flags.INVITE_APK)) return
        val text = buildInviteText()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "VibeTube")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(intent, "Convidar para o VibeTube").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
