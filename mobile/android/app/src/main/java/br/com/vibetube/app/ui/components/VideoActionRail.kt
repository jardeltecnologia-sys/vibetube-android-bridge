package br.com.vibetube.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import br.com.vibetube.app.ui.theme.VibeRed
import br.com.vibetube.app.ui.theme.VibeTextPrimary

/**
 * Rail vertical de ações sobreposto ao vídeo no FeedScreen.
 */
@Composable
fun VideoActionRail(
    isLiked: Boolean,
    likeCount: Int,
    commentCount: Int,
    isSaved: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit,
    onFollowClick: () -> Unit,
    onInviteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isLiked) 1.15f else 1f,
        label = "like_scale"
    )

    Column(
        modifier = modifier.padding(end = 12.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        ActionItem(
            icon = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            label = if (likeCount > 0) likeCount.toString() else "Curtir",
            tint = if (isLiked) VibeRed else VibeTextPrimary,
            onClick = onLikeClick,
            scale = scale
        )
        Spacer(Modifier.height(20.dp))
        ActionItem(
            icon = Icons.Outlined.ChatBubbleOutline,
            label = if (commentCount > 0) commentCount.toString() else "Comentar",
            onClick = onCommentClick
        )
        Spacer(Modifier.height(20.dp))
        ActionItem(
            icon = Icons.Outlined.Share,
            label = "Compartilhar",
            onClick = onShareClick
        )
        Spacer(Modifier.height(20.dp))
        ActionItem(
            icon = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            label = "Salvar",
            tint = if (isSaved) VibeRed else VibeTextPrimary,
            onClick = onSaveClick
        )
        Spacer(Modifier.height(20.dp))
        ActionItem(
            icon = Icons.Outlined.PersonAdd,
            label = "Seguir",
            onClick = onFollowClick
        )
        Spacer(Modifier.height(20.dp))
        ActionItem(
            icon = Icons.Outlined.GroupAdd,
            label = "Convidar",
            onClick = onInviteClick
        )
    }
}

@Composable
private fun ActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = VibeTextPrimary,
    scale: Float = 1f
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier
                .size(36.dp)
                .scale(scale)
                .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                .padding(6.dp)
                .clickable(onClick = onClick)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = VibeTextPrimary
        )
    }
}
