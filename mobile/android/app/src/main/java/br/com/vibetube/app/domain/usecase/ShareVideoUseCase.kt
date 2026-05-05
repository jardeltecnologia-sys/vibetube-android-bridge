package br.com.vibetube.app.domain.usecase

import android.content.Context
import android.content.Intent
import br.com.vibetube.app.core.config.FeatureFlagManager
import br.com.vibetube.app.domain.model.VibeVideo

/**
 * Compõe o texto de compartilhamento e dispara o Android Sharesheet.
 *
 * Texto:
 *   Assista no VibeTube: {title}
 *
 *   {intro}
 *
 *   Ver no blog: {postUrl}
 *
 *   Instale o APK do VibeTube: {firebaseApkInviteUrl}
 */
class ShareVideoUseCase(
    private val flags: FeatureFlagManager
) {

    fun buildShareText(video: VibeVideo): String {
        val intro = video.intro.ifBlank { video.description.take(180) }
        return buildString {
            append("Assista no VibeTube: ").append(video.title).append("\n\n")
            if (intro.isNotBlank()) append(intro).append("\n\n")
            append("Ver no blog: ").append(video.postUrl).append("\n\n")
            append("Instale o APK do VibeTube: ").append(flags.firebaseApkInviteUrl)
        }
    }

    fun share(context: Context, video: VibeVideo) {
        if (!flags.isEnabled(FeatureFlagManager.Flags.SHARE)) return
        val text = buildShareText(video)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, video.title)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(intent, "Compartilhar vídeo").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
