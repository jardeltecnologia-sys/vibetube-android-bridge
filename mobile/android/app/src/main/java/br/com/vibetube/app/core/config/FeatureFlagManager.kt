package br.com.vibetube.app.core.config

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.charset.Charset

@Serializable
data class VibeTubeConfig(
    val mode: String = "blogger_bridge",
    val blogHomeUrl: String = "https://www.vibetube.com.br/",
    val blogFeedUrl: String = "https://www.vibetube.com.br/feeds/posts/default?alt=json&max-results=50",
    val firebaseApkInviteUrl: String = "https://appdistribution.firebase.dev/i/538c49560ae4a639",
    val cloudApiBaseUrl: String = "",
    val features: Map<String, Boolean> = emptyMap()
)

/**
 * Lê vibetube_config.json em assets e expõe flags.
 * Cache em memória — o arquivo só muda quando rebuildamos o app.
 */
class FeatureFlagManager private constructor(
    private val context: Context?,
    private val injectedConfig: VibeTubeConfig?
) {

    /** Construtor de produção: lê do assets do Context. */
    constructor(context: Context) : this(context, null)

    /** Construtor de teste/injeção: usa um VibeTubeConfig direto. */
    constructor(config: VibeTubeConfig) : this(null, config)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val config: VibeTubeConfig by lazy { injectedConfig ?: loadConfig() }

    private fun loadConfig(): VibeTubeConfig = try {
        context?.assets?.open("vibetube_config.json")?.use { input ->
            val text = input.readBytes().toString(Charset.forName("UTF-8"))
            json.decodeFromString(VibeTubeConfig.serializer(), text)
        } ?: VibeTubeConfig()
    } catch (e: Exception) {
        // Fallback seguro com defaults bridge.
        VibeTubeConfig()
    }

    open val mode: String get() = config.mode
    open val blogHomeUrl: String get() = config.blogHomeUrl
    open val blogFeedUrl: String get() = config.blogFeedUrl
    open val firebaseApkInviteUrl: String get() = config.firebaseApkInviteUrl
    open val cloudApiBaseUrl: String get() = config.cloudApiBaseUrl

    open fun isEnabled(feature: String): Boolean {
        return config.features[feature] ?: false
    }

    fun isBridgeMode(): Boolean = mode == "blogger_bridge"
    fun isCloudMode(): Boolean = mode == "cloud"

    object Flags {
        const val INTRO_FROM_BLOG = "introFromBlog"
        const val FEED = "feed"
        const val VIDEO_PLAYER = "videoPlayer"
        const val SHARE = "share"
        const val INVITE_APK = "inviteApk"
        const val SEARCH = "search"
        const val CATEGORIES = "categories"
        const val COMMENTS_READ = "commentsRead"
        const val COMMENTS_WRITE_WEBVIEW = "commentsWriteViaBloggerWebView"
        const val COMMENTS_WRITE_API = "commentsWriteViaApi"
        const val LIKES_LOCAL = "likesLocal"
        const val LIKES_BLOG_REACTION = "likesBlogReaction"
        const val LIKES_CLOUD_SYNC = "likesCloudSync"
        const val SAVE_LOCAL = "saveLocal"
        const val LOGIN = "login"
        const val REGISTER = "register"
        const val UPLOAD = "upload"
        const val FOLLOW = "follow"
        const val NOTIFICATIONS = "notifications"
        const val CHAT = "chat"
        const val LIVE = "live"
        const val MONETIZATION = "monetization"
        const val CREATOR_DASHBOARD = "creatorDashboard"
        const val MODERATION = "moderation"
        const val REPORTS = "reports"
    }
}
