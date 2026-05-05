package br.com.vibetube.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import br.com.vibetube.app.core.utils.DateFormatter
import br.com.vibetube.app.domain.model.VibeComment
import br.com.vibetube.app.ui.theme.VibeRed
import br.com.vibetube.app.ui.theme.VibeSurface
import br.com.vibetube.app.ui.theme.VibeSurfaceVariant
import br.com.vibetube.app.ui.theme.VibeTextPrimary
import br.com.vibetube.app.ui.theme.VibeTextSecondary

@Composable
fun CommentItem(
    comment: VibeComment,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (!comment.authorAvatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = comment.authorAvatarUrl,
                contentDescription = comment.authorName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(VibeSurfaceVariant)
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = comment.authorName,
                tint = VibeTextSecondary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.labelLarge,
                    color = VibeTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = DateFormatter.toRelative(comment.publishedAt),
                    style = MaterialTheme.typography.labelMedium,
                    color = VibeTextSecondary
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = VibeTextPrimary
            )
        }
    }
}

/**
 * Barra inferior do CommentsScreen — não é um TextField real porque o envio
 * acontece via WebView no formulário oficial do Blogger. É um CTA grande.
 */
@Composable
fun CommentInputBar(
    onOpenWebComment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(VibeSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Comentar abre o formulário oficial do blog.",
            style = MaterialTheme.typography.labelMedium,
            color = VibeTextSecondary,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.size(12.dp))
        Button(
            onClick = onOpenWebComment,
            colors = ButtonDefaults.buttonColors(containerColor = VibeRed),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Comentar no blog")
        }
    }
}
