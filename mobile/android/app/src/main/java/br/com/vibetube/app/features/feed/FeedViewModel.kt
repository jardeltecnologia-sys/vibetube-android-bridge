package br.com.vibetube.app.features.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.vibetube.app.VibeTubeApp
import br.com.vibetube.app.domain.model.VibeIntro
import br.com.vibetube.app.domain.model.VibeResult
import br.com.vibetube.app.domain.model.VibeVideo
import br.com.vibetube.app.domain.usecase.ToggleLikeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class FeedUiState(
    val videos: List<VibeVideo> = emptyList(),
    val intro: VibeIntro? = null,
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val showCacheNotice: Boolean = false,
    val errorMessage: String? = null,
    val showLikeNotice: Boolean = false,
    val showIntroCard: Boolean = true,
    val likedIds: Set<String> = emptySet(),
    val savedIds: Set<String> = emptySet()
)

class FeedViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as VibeTubeApp

    private val _uiState = MutableStateFlow(FeedUiState(isLoading = true))
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private var firstLikeOfSession = true

    init {
        observeData()
        observeNetwork()
        loadInitial()
    }

    private fun observeData() {
        // Observa vídeos do Room
        app.videoRepository.observeVideos()
            .onEach { videos ->
                _uiState.value = _uiState.value.copy(videos = videos)
            }
            .launchIn(viewModelScope)

        // Observa intro
        app.introRepository.observeIntro()
            .onEach { intro ->
                _uiState.value = _uiState.value.copy(intro = intro)
            }
            .launchIn(viewModelScope)

        // Observa curtidas e salvos
        combine(
            app.database.likeDao().observeAllLiked(),
            app.database.savedVideoDao().observeAll()
        ) { likes, saves ->
            _uiState.value = _uiState.value.copy(
                likedIds = likes.filter { it.isLiked }.map { it.videoId }.toSet(),
                savedIds = saves.map { it.videoId }.toSet()
            )
        }.launchIn(viewModelScope)
    }

    private fun observeNetwork() {
        app.networkMonitor.observe()
            .onEach { online ->
                _uiState.value = _uiState.value.copy(isOffline = !online)
            }
            .launchIn(viewModelScope)
    }

    private fun loadInitial() {
        viewModelScope.launch {
            // Refresh intro em paralelo
            launch { app.introRepository.refresh() }
            // Refresh feed
            refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = app.videoRepository.refresh()
            when (result) {
                is VibeResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showCacheNotice = false,
                        errorMessage = null
                    )
                }
                is VibeResult.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showCacheNotice = result.cached,
                        errorMessage = if (!result.cached) {
                            "Não foi possível carregar agora. " +
                                "Verifique sua conexão e tente novamente."
                        } else null
                    )
                }
                VibeResult.Loading -> { /* nothing */ }
            }
        }
    }

    fun onToggleLike(video: VibeVideo) {
        viewModelScope.launch {
            val result = app.toggleLikeUseCase(
                videoId = video.id,
                postId = video.postId,
                firstLikeOfSession = firstLikeOfSession
            )
            if (result is ToggleLikeUseCase.Result.Liked && result.showLocalNotice) {
                firstLikeOfSession = false
                _uiState.value = _uiState.value.copy(showLikeNotice = true)
                app.userPreferences.markLikeNoticeShown()
            }
        }
    }

    fun onToggleSave(video: VibeVideo) {
        viewModelScope.launch {
            val dao = app.database.savedVideoDao()
            if (dao.isSaved(video.id)) {
                dao.delete(video.id)
            } else {
                dao.upsert(
                    br.com.vibetube.app.data.cache.entity.SavedVideoEntity(
                        videoId = video.id,
                        postId = video.postId,
                        savedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun dismissLikeNotice() {
        _uiState.value = _uiState.value.copy(showLikeNotice = false)
    }

    fun dismissIntroCard() {
        _uiState.value = _uiState.value.copy(showIntroCard = false)
    }
}
