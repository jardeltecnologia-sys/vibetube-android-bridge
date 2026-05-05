package br.com.vibetube.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.vibetube.app.ui.theme.VibeBlack
import br.com.vibetube.app.ui.theme.VibeRed
import br.com.vibetube.app.ui.theme.VibeTextPrimary
import br.com.vibetube.app.ui.theme.VibeTextSecondary

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(VibeBlack), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = VibeRed)
    }
}

@Composable
fun EmptyState(
    title: String = "Nenhum vídeo disponível.",
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(VibeBlack), contentAlignment = Alignment.Center) {
        Text(
            text = title,
            color = VibeTextPrimary,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(VibeBlack), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = message,
                color = VibeTextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = VibeRed)
            ) {
                Text("Tentar novamente")
            }
        }
    }
}

@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(VibeRed)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Você está offline. Mostrando conteúdo salvo.",
            color = VibeTextPrimary,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun LocalLikeNotice(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(VibeBlack.copy(alpha = 0.92f))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Curtida salva neste dispositivo. A sincronização será ativada na versão oficial.",
            color = VibeTextSecondary,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
