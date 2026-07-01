package ar.edu.unlam.mobile.scaffolding.ui.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.AVATAR_SIZE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_SMALL

@Composable
fun PostCard(
    post: PostResponse,
    isSelectedAsFavorite: Boolean,
    onSelectedAsFavoriteAction: () -> Unit,
    onReply: () -> Unit = {},
    onLike: () -> Unit = {},
) {
    Row(
        modifier =
            Modifier
                .padding(PADDING_MEDIUM)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(PADDING_LARGE))
                .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        PostAvatar()

        Spacer(modifier = Modifier.width(PADDING_MEDIUM))

        Column(verticalArrangement = Arrangement.spacedBy(PADDING_SMALL), modifier = Modifier.padding(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(PADDING_SMALL)) {
                PostText(
                    post.author,
                    MaterialTheme.typography.titleMedium,
                    MaterialTheme.colorScheme.onSurface,
                )

                PostText(
                    "@usuario",
                    MaterialTheme.typography.bodyMedium,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onSelectedAsFavoriteAction) {
                    Icon(imageVector = if (isSelectedAsFavorite) Icons.Default.Star else Icons.Outlined.Star, contentDescription = null)
                }
            }

            PostText(
                post.message,
                MaterialTheme.typography.bodyMedium,
                MaterialTheme.colorScheme.onSurface,
            )

            PostActions(
                likes = post.likes,
                liked = post.liked,
                onReply = onReply,
                onLike = onLike,
            )
        }
    }
}

@Composable
private fun PostAvatar() {
    Box(
        modifier =
            Modifier
                .size(AVATAR_SIZE)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PostText(
    textToShow: String,
    textStyle: TextStyle,
    textColor: Color,
) {
    Text(
        text = textToShow,
        style = textStyle,
        color = textColor,
        modifier = Modifier.padding(PADDING_SMALL),
    )
}

@Composable
private fun PostActions(
    likes: Int,
    liked: Boolean,
    onReply: () -> Unit,
    onLike: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onLike) {
                Icon(
                    if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Me gusta",
                    tint = if (liked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = "$likes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        IconButton(onClick = onReply) {
            Icon(Icons.Default.Replay, contentDescription = "Responder")
        }
    }
}
