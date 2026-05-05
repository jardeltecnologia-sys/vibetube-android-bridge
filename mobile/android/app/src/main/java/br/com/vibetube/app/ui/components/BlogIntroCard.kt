package br.com.vibetube.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.vibetube.app.domain.model.VibeIntro
import br.com.vibetube.app.ui.theme.VibeSurface
import br.com.vibetube.app.ui.theme.VibeTextPrimary
import br.com.vibetube.app.ui.theme.VibeTextSecondary

/**
 * Card sobreposto ao topo do feed mostrando a introdução dinâmica vinda do blog.
 * Visível na primeira sessão; usuário pode fechar.
 */
@Composable
fun BlogIntroCard(
    intro: VibeIntro,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VibeSurface.copy(alpha = 0.92f))
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = intro.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = VibeTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (intro.subtitle.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = intro.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = VibeTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (intro.body.isNotBlank() && intro.body != intro.subtitle) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = intro.body,
                        style = MaterialTheme.typography.bodySmall,
                        color = VibeTextPrimary.copy(alpha = 0.8f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Fechar",
                    tint = VibeTextSecondary
                )
            }
        }
    }
}
