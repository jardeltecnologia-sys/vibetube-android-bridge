package br.com.vibetube.app.features.profile

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
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

@Composable
fun ProfileScreen(
    onOpenStandby: (featureName: String) -> Unit,
    onOpenInvite: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(VibeBlack)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(VibeSurfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    tint = VibeTextSecondary,
                    modifier = Modifier.size(72.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Visitante",
                style = MaterialTheme.typography.headlineMedium,
                color = VibeTextPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Cadastro e perfil estarão disponíveis na versão oficial.",
                style = MaterialTheme.typography.bodyMedium,
                color = VibeTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onOpenStandby("login") },
                colors = ButtonDefaults.buttonColors(containerColor = VibeRed),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Login, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Entrar")
            }
            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onOpenInvite,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.GroupAdd, contentDescription = null, tint = VibeTextPrimary)
                Spacer(Modifier.size(8.dp))
                Text("Convidar amigos", color = VibeTextPrimary)
            }
        }
    }
}
