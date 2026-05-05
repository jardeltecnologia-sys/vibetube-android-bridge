package br.com.vibetube.app.domain.model

/**
 * Wrapper simples para operações que podem falhar.
 * Não usamos Result<T> do Kotlin para manter API explícita.
 */
sealed class VibeResult<out T> {
    data class Success<T>(val data: T) : VibeResult<T>()
    data class Failure(val error: Throwable, val cached: Boolean = false) : VibeResult<Nothing>()
    object Loading : VibeResult<Nothing>()
}
