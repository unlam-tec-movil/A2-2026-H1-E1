package ar.edu.unlam.mobile.scaffolding.ui.components.post

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.AVATAR_SIZE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_SMALL
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PostCard(
    post: PostResponse,
    isSelectedAsFavorite: Boolean,
    replyCount: Int = 0,
    onSelectedAsFavoriteAction: () -> Unit,
    onClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    onReply: () -> Unit = {},
    onLike: () -> Unit = {},
) {
    Surface(
        modifier =
            Modifier
                .padding(horizontal = PADDING_LARGE, vertical = PADDING_SMALL)
                .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
    ) {
        Column(modifier = Modifier.padding(PADDING_MEDIUM)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PostAvatar(onClick = onUserClick)

                Spacer(modifier = Modifier.width(PADDING_SMALL))

                PostText(
                    post.author,
                    MaterialTheme.typography.titleMedium,
                    MaterialTheme.colorScheme.onSurface,
                    onClick = onUserClick,
                    modifier = Modifier.weight(1f),
                )

                IconButton(onClick = onSelectedAsFavoriteAction) {
                    Icon(
                        imageVector = if (isSelectedAsFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                    )
                }
            }

            PostText(
                post.message,
                MaterialTheme.typography.bodyMedium,
                MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable(onClick = onClick),
            )

            PostText(
                formatDate(post.date),
                MaterialTheme.typography.bodySmall,
                MaterialTheme.colorScheme.onSurfaceVariant,
            )

            PostActions(
                likes = post.likes,
                liked = post.liked,
                replyCount = replyCount,
                onReply = onReply,
                onLike = onLike,
            )
        }
    }
}

@Composable
private fun PostAvatar(onClick: () -> Unit = {}) {
    Box(
        modifier =
            Modifier
                .size(AVATAR_SIZE)
                .clip(CircleShape)
                .clickable(onClick = onClick)
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
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Text(
        text = textToShow,
        style = textStyle,
        color = textColor,
        modifier =
            modifier.padding(PADDING_SMALL).then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
            ),
    )
}

@Composable
private fun PostActions(
    likes: Int,
    liked: Boolean,
    replyCount: Int = 0,
    onReply: () -> Unit,
    onLike: () -> Unit,
) {
    var likedState by remember { mutableStateOf(liked) }
    val scale by animateFloatAsState(
        targetValue = if (likedState) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 600f),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    likedState = !likedState
                    onLike()
                },
            ) {
                Icon(
                    modifier = Modifier.scale(scale),
                    imageVector = if (likedState) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Me gusta",
                    tint = if (likedState) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = "$likes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onReply) {
                Icon(Icons.Default.Replay, contentDescription = "Responder")
            }

            if (replyCount > 0) {
                Text(
                    text = "$replyCount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostCardPreview() {
    ScaffoldingV2Theme {
        PostCard(
            post =
                PostResponse(
                    id = 1,
                    author = "Usuario",
                    message = "Este es un post de ejemplo",
                    likes = 5,
                    liked = false,
                    avatarUrl = "tomas.com",
                    parentId = 10,
                    authorId = 7,
                    date = "2026-1-1",
                ),
            isSelectedAsFavorite = true,
            onSelectedAsFavoriteAction = {},
            onReply = {},
            onLike = {},
        )
    }
}

private fun formatDate(dateString: String): String =
    try {
        val inputFormats =
            listOf(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd",
            )
        val parsed =
            inputFormats.firstNotNullOfOrNull { format ->
                try {
                    SimpleDateFormat(format, Locale.getDefault()).parse(dateString)
                } catch (_: Exception) {
                    null
                }
            }
        if (parsed != null) {
            SimpleDateFormat("dd MMM yyyy HH:mm", Locale("es")).format(parsed)
        } else {
            dateString
        }
    } catch (_: Exception) {
        dateString
    }
