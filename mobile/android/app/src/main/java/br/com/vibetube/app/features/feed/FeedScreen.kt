package br.com.vibetube.app.features.feed

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.vibetube.app.VibeTubeApp
import br.com.vibetube.app.core.config.FeatureFlagManager
import br.com.vibetube.app.core.player.VibeVideoPlayer
import br.com.vibetube.app.domain.model.VibeVideo
import br.com.vibetube.app.ui.components.EmptyState
import br.com.vibetube.app.ui.components.ErrorState
import br.com.vibetube.app.ui.components.LoadingState
import br.com.vibetube.app.ui.components.LocalLikeNotice
import br.com.vibetube.app.ui.components.OfflineBanner
import br.com.vibetube.app.ui.components.VideoActionRail
import br.com.vibetube.app.ui.components.VideoMetadataOverlay
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedScreen(
    onOpenComments: (videoId: String) -> Unit,
    onOpenStandby: (featureName: String) -> Unit
) {
    val viewModel: FeedViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val app = remember(context) { context.applicationContext as VibeTubeApp }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        when {
            state.isLoading && state.videos.isEmpty() -> {
                LoadingState()
            }
            !state.errorMessage.isNullOrBlank() && state.videos.isEmpty() -> {
                ErrorState(
                    message = state.errorMessage!!,
                    onRetry = { viewModel.refresh() }
                )
            }
            state.videos.isEmpty() -> {
                EmptyState(title = "Nenhum vídeo disponível ainda.")
            }
            else -> {
                FeedPager(
                    videos = state.videos,
                    likedIds = state.likedIds,
                    savedIds = state.savedIds,
                    onLike = viewModel::onToggleLike,
                    onSave = viewModel::onToggleSave,
                    onComment = { v ->
                        if (app.featureFlags.isEnabled(FeatureFlagManager.Flags.COMMENTS_READ)) {
                            onOpenComments(v.id)
                        } else {
                            onOpenStandby("comments")
                        }
                    },
                    onShare = { v -> app.shareManager.shareVideo(context, v) },
                    onFollow = { onOpenStandby("follow") },
                    onInvite = { app.shareManager.inviteToInstall(context) }
                )
            }
        }

        // Overlay com banner offline + intro card no topo
        Column(modifier = Modifier.align(Alignment.TopCenter)) {
            if (state.isOffline && state.showCacheNotice) {
                OfflineBanner()
            }
            // V5 estável: intro/card superior removido para experiência full-screen.
            // A introdução continua sendo carregada internamente, mas não cobre o vídeo.
        }

        // Notice bottom: aviso de like local
        if (state.showLikeNotice) {
            LocalLikeNotice(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp))
            LaunchedEffect(state.showLikeNotice) {
                delay(3500)
                viewModel.dismissLikeNotice()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FeedPager(
    videos: List<VibeVideo>,
    likedIds: Set<String>,
    savedIds: Set<String>,
    onLike: (VibeVideo) -> Unit,
    onSave: (VibeVideo) -> Unit,
    onComment: (VibeVideo) -> Unit,
    onShare: (VibeVideo) -> Unit,
    onFollow: (VibeVideo) -> Unit,
    onInvite: (VibeVideo) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { videos.size })

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { index ->
        val video = videos[index]
        val isCurrent = index == pagerState.currentPage

        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            // Player só toca para o item atual — economia de bateria + bandwidth
            if (isCurrent) {
                VibeVideoPlayer(
                    video = video,
                    autoPlay = true,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Overlay de metadados
            VideoMetadataOverlay(
                video = video,
                modifier = Modifier.align(Alignment.BottomStart)
            )

            // Rail de ações
            VideoActionRail(
                isLiked = video.id in likedIds,
                likeCount = if (video.id in likedIds) 1 else 0,
                commentCount = video.commentCount,
                isSaved = video.id in savedIds,
                onLikeClick = { onLike(video) },
                onCommentClick = { onComment(video) },
                onShareClick = { onShare(video) },
                onSaveClick = { onSave(video) },
                onFollowClick = { onFollow(video) },
                onInviteClick = { onInvite(video) },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}
