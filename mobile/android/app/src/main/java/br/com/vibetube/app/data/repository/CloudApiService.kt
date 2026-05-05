package br.com.vibetube.app.data.repository

/**
 * Interface stub do serviço Cloud futuro.
 * NÃO está ativada — quando estiver, basta criar uma implementação Retrofit/OkHttp
 * e injetá-la em CloudVideoRepository / LikesCloudRepository.
 *
 * Endpoints planejados (ver README_APK_BRIDGE.md):
 *   GET    /api/intro
 *   GET    /api/videos
 *   GET    /api/videos/{id}
 *   GET    /api/videos/{id}/comments
 *   POST   /api/videos/{id}/comments
 *   POST   /api/videos/{id}/like
 *   POST   /api/auth/login
 *   POST   /api/auth/register
 *   POST   /api/videos/upload
 *   POST   /api/users/{id}/follow
 *   GET    /api/notifications
 *   POST   /api/invite
 */
interface CloudApiService {

    suspend fun getIntro(): Result<Unit> = Result.failure(NotImplementedError())
    suspend fun listVideos(page: Int = 1, pageSize: Int = 20): Result<Unit> = Result.failure(NotImplementedError())
    suspend fun getVideo(id: String): Result<Unit> = Result.failure(NotImplementedError())
    suspend fun listComments(videoId: String): Result<Unit> = Result.failure(NotImplementedError())
    suspend fun postComment(videoId: String, content: String): Result<Unit> = Result.failure(NotImplementedError())
    suspend fun likeVideo(videoId: String): Result<Unit> = Result.failure(NotImplementedError())

    companion object {
        /** Implementação real será criada quando ativarmos mode="cloud". */
        fun disabled(): CloudApiService = object : CloudApiService {}
    }
}
