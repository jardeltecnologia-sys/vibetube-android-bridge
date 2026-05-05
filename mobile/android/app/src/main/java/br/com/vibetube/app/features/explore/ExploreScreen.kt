package br.com.vibetube.app.features.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import br.com.vibetube.app.VibeTubeApp
import br.com.vibetube.app.domain.model.VibeVideo
import br.com.vibetube.app.ui.components.EmptyState
import br.com.vibetube.app.ui.theme.VibeBlack
import br.com.vibetube.app.ui.theme.VibeSurface
import br.com.vibetube.app.ui.theme.VibeSurfaceVariant
import br.com.vibetube.app.ui.theme.VibeTextPrimary
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onVideoClick: (VibeVideo) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as VibeTubeApp
    val videosFlow = remember { app.videoRepository.observeVideos() }
    val videos by videosFlow.collectAsState(initial = emptyList())

    Scaffold(
        containerColor = VibeBlack,
        topBar = {
            TopAppBar(
                title = { Text("Explorar", color = VibeTextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VibeSurface)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(VibeBlack)) {
            if (videos.isEmpty()) {
                EmptyState(title = "Nada para explorar ainda.")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                ) {
                    items(videos, key = { it.id }) { v ->
                        VideoTile(video = v, onClick = { onVideoClick(v) })
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoTile(video: VibeVideo, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(6.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
                .clip(RoundedCornerShape(12.dp))
                .background(VibeSurfaceVariant)
        ) {
            if (!video.thumbnailUrl.isNullOrBlank()) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Icon(
                Icons.Outlined.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(8.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = video.title,
            style = MaterialTheme.typography.labelLarge,
            color = VibeTextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
