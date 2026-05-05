package br.com.vibetube.app.features.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.vibetube.app.ui.components.CommentInputBar
import br.com.vibetube.app.ui.components.CommentItem
import br.com.vibetube.app.ui.components.LoadingState
import br.com.vibetube.app.ui.theme.VibeBlack
import br.com.vibetube.app.ui.theme.VibeOutline
import br.com.vibetube.app.ui.theme.VibeSurface
import br.com.vibetube.app.ui.theme.VibeTextPrimary
import br.com.vibetube.app.ui.theme.VibeTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    videoId: String,
    onBack: () -> Unit,
    onOpenWebComment: () -> Unit
) {
    val viewModel: CommentsViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(videoId) {
        viewModel.load(videoId)
    }

    Scaffold(
        containerColor = VibeBlack,
        topBar = {
            TopAppBar(
                title = { Text("Comentários", color = VibeTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, "Voltar", tint = VibeTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Outlined.Refresh, "Atualizar", tint = VibeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VibeSurface)
            )
        },
        bottomBar = {
            CommentInputBar(onOpenWebComment = onOpenWebComment)
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(VibeBlack)) {
            when {
                state.isLoading && state.comments.isEmpty() -> LoadingState()
                state.comments.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nenhum comentário ainda. Seja o primeiro a comentar.",
                            color = VibeTextPrimary,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Os comentários são sincronizados com o blog VibeTube.",
                            color = VibeTextSecondary,
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = "Os comentários são sincronizados com o blog VibeTube.",
                                color = VibeTextSecondary,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                            HorizontalDivider(color = VibeOutline)
                        }
                        items(state.comments, key = { it.id }) { comment ->
                            CommentItem(comment = comment)
                            HorizontalDivider(color = VibeOutline)
                        }
                    }
                }
            }
        }
    }
}
