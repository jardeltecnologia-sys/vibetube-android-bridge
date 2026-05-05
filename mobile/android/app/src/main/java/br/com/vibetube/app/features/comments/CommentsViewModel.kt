package br.com.vibetube.app.features.comments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.vibetube.app.VibeTubeApp
import br.com.vibetube.app.domain.model.VibeComment
import br.com.vibetube.app.domain.model.VibeResult
import br.com.vibetube.app.domain.model.VibeVideo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class CommentsUiState(
    val video: VibeVideo? = null,
    val comments: List<VibeComment> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val canPostComment: Boolean = true
)

class CommentsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as VibeTubeApp

    private val _uiState = MutableStateFlow(CommentsUiState(isLoading = true))
    val uiState: StateFlow<CommentsUiState> = _uiState.asStateFlow()

    private var observingPostId: String? = null

    fun load(videoId: String) {
        viewModelScope.launch {
            val video = app.videoRepository.getById(videoId)
            _uiState.value = _uiState.value.copy(video = video)
            val postId = video?.postId
            if (postId != null && postId != observingPostId) {
                observingPostId = postId
                app.commentsRepository.observeByPost(postId)
                    .onEach { list ->
                        _uiState.value = _uiState.value.copy(comments = list)
                    }
                    .launchIn(viewModelScope)
            }
            refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val video = _uiState.value.video ?: return@launch
            val postId = video.postId ?: run {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Não foi possível identificar o post."
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = app.commentsRepository.refresh(blogId = video.blogId, postId = postId)
            when (result) {
                is VibeResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = null)
                }
                is VibeResult.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = if (!result.cached) {
                            "Não foi possível atualizar os comentários agora."
                        } else null
                    )
                }
                VibeResult.Loading -> { /* */ }
            }
        }
    }
}
