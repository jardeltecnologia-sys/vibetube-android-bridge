package br.com.vibetube.app.features.invite

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.vibetube.app.VibeTubeApp
import br.com.vibetube.app.ui.theme.VibeBlack
import br.com.vibetube.app.ui.theme.VibeRed
import br.com.vibetube.app.ui.theme.VibeSurface
import br.com.vibetube.app.ui.theme.VibeSurfaceVariant
import br.com.vibetube.app.ui.theme.VibeTextPrimary
import br.com.vibetube.app.ui.theme.VibeTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as VibeTubeApp

    Scaffold(
        containerColor = VibeBlack,
        topBar = {
            TopAppBar(
                title = { Text("Convidar amigos", color = VibeTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, "Voltar", tint = VibeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VibeSurface)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(VibeBlack)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(VibeSurfaceVariant, RoundedCornerShape(48.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.GroupAdd,
                        contentDescription = null,
                        tint = VibeRed,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    "Convide para o VibeTube",
                    style = MaterialTheme.typography.headlineMedium,
                    color = VibeTextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Compartilhe o app com amigos para descobrir vídeos, tendências e cultura digital.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VibeTextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = app.featureFlags.firebaseApkInviteUrl,
                    style = MaterialTheme.typography.labelMedium,
                    color = VibeTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = { app.shareManager.inviteToInstall(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = VibeRed),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Compartilhar link de instalação")
                }
            }
        }
    }
}
