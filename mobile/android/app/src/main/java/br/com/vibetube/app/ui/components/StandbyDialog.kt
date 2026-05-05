package br.com.vibetube.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import br.com.vibetube.app.ui.theme.VibeRed
import br.com.vibetube.app.ui.theme.VibeSurface

@Composable
fun StandbyDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Em breve") },
        text = {
            Text(
                "Este recurso será ativado na versão oficial do VibeTube, " +
                    "após a migração para a infraestrutura Google Cloud."
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendi", color = VibeRed)
            }
        },
        containerColor = VibeSurface
    )
}
