package br.com.vibetube.app.features.standby

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.vibetube.app.ui.theme.VibeBlack
import br.com.vibetube.app.ui.theme.VibeRed
import br.com.vibetube.app.ui.theme.VibeSurfaceVariant
import br.com.vibetube.app.ui.theme.VibeTextPrimary
import br.com.vibetube.app.ui.theme.VibeTextSecondary

private fun friendlyName(featureName: String): String = when (featureName) {
    "upload" -> "Publicar vídeos"
    "login" -> "Entrar"
    "register" -> "Cadastro"
    "follow" -> "Seguir criadores"
    "notifications" -> "Notificações"
    "chat" -> "Chat"
    "live" -> "Transmissões ao vivo"
    "monetization" -> "Monetização"
    "creatorDashboard" -> "Painel do criador"
    "moderation" -> "Moderação"
    "reports" -> "Denúncias"
    "comments" -> "Comentários completos"
    else -> "Este recurso"
}

@Composable
fun StandbyScreen(
    featureName: String,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(VibeBlack)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(VibeSurfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.HourglassEmpty,
                    contentDescription = null,
                    tint = VibeRed,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "Em breve",
                style = MaterialTheme.typography.headlineLarge,
                color = VibeTextPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "${friendlyName(featureName)} estará disponível na versão oficial do VibeTube, " +
                    "após a migração para a infraestrutura Google Cloud.",
                style = MaterialTheme.typography.bodyMedium,
                color = VibeTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = VibeRed),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entendi")
            }
        }
    }
}
