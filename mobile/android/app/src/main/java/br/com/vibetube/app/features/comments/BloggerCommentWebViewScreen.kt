package br.com.vibetube.app.features.comments

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.vibetube.app.core.webview.SafeWebView
import br.com.vibetube.app.core.webview.SafeWebViewConfig
import br.com.vibetube.app.ui.theme.VibeBlack
import br.com.vibetube.app.ui.theme.VibeSurface
import br.com.vibetube.app.ui.theme.VibeTextPrimary
import br.com.vibetube.app.ui.theme.VibeTextSecondary
import kotlinx.coroutines.launch

/**
 * Tela full-screen para o usuário comentar usando o formulário oficial do Blogger.
 *
 * SEGURANÇA:
 *  - URLs fora da whitelist (SafeWebViewConfig.Surface.COMMENTS) são bloqueadas
 *  - Bridge JS NÃO é registrada nesta versão (não é necessária; reduzimos superfície)
 *  - Top bar: Fechar / Atualizar / Abrir no navegador
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloggerCommentWebViewScreen(
    videoId: String,
    onClose: () -> Unit
) {
    val viewModel: CommentsViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var reloadKey by remember { mutableIntStateOf(0) }
    var blockedUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(videoId) {
        viewModel.load(videoId)
    }

    val targetUrl: String? = remember(state.video, reloadKey) {
        // Usa commentsUrl (postUrl#comments) se houver; fallback pro postUrl simples
        state.video?.commentsUrl ?: state.video?.postUrl
    }

    Scaffold(
        containerColor = VibeBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Comentar no blog",
                            color = VibeTextPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Sessão oficial do Blogger",
                            color = VibeTextSecondary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Outlined.Close, "Fechar", tint = VibeTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { reloadKey++ }) {
                        Icon(Icons.Outlined.Refresh, "Atualizar", tint = VibeTextPrimary)
                    }
                    IconButton(
                        onClick = {
                            val url = targetUrl ?: return@IconButton
                            try {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            } catch (_: Exception) { /* ignore */ }
                        }
                    ) {
                        Icon(Icons.Outlined.OpenInBrowser, "Abrir no navegador", tint = VibeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VibeSurface)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(VibeBlack)
        ) {
            if (targetUrl == null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(
                        "URL do post não disponível.",
                        color = VibeTextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // A `key` força recriação da WebView quando o usuário pedir refresh.
                androidx.compose.runtime.key(targetUrl, reloadKey) {
                    SafeWebView(
                        url = targetUrl,
                        surface = SafeWebViewConfig.Surface.COMMENTS,
                        modifier = Modifier.fillMaxSize().background(Color.White),
                        onBlocked = { url ->
                            blockedUrl = url
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "URL bloqueada por segurança."
                                )
                            }
                        }
                    )
                }
            }

            // Aviso de moderação
            Text(
                text = "Se o blog exigir moderação, seu comentário aparecerá após aprovação.",
                color = VibeTextSecondary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VibeBlack.copy(alpha = 0.85f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .align(androidx.compose.ui.Alignment.BottomCenter),
                textAlign = TextAlign.Center
            )
        }
    }
}
