package ar.edu.unlam.mobile.scaffolding.ui.components.post

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.AVATAR_SIZE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_SMALL
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PostCard(
    post: PostResponse,
    isSelectedAsFavorite: Boolean,
    onSelectedAsFavoriteAction: () -> Unit,
    repliesCount: Int = 0,
    onReply: () -> Unit = {},
    onLike: () -> Unit = {},
    onPostClick: () -> Unit = {},
) {
    Surface(
        modifier =
            Modifier
                .padding(PADDING_MEDIUM)
                .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        onClick = onPostClick,
    ) {
        Row(modifier = Modifier.padding(PADDING_MEDIUM)) {
            PostAvatar(
                avatarUrl = post.avatarUrl,
                author = post.author,
            )

            Spacer(modifier = Modifier.width(PADDING_MEDIUM))

            Column(
                verticalArrangement = Arrangement.spacedBy(PADDING_SMALL),
                modifier = Modifier.weight(1f),
            ) {
                PostHeader(
                    author = post.author,
                    isSelectedAsFavorite = isSelectedAsFavorite,
                    onSelectedAsFavoriteAction = onSelectedAsFavoriteAction,
                )

                PostText(
                    textToShow = post.message,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    textColor = MaterialTheme.colorScheme.onSurface,
                )

                PostText(
                    textToShow = formatDate(post.date),
                    textStyle = MaterialTheme.typography.bodySmall,
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                PostActions(
                    likes = post.likes,
                    liked = post.liked,
                    repliesCount = repliesCount,
                    onReply = onReply,
                    onLike = onLike,
                )
            }
        }
    }
}

@Composable
private fun PostHeader(
    author: String,
    isSelectedAsFavorite: Boolean,
    onSelectedAsFavoriteAction: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = author,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = generateUserHandle(author),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        IconButton(onClick = onSelectedAsFavoriteAction) {
            Icon(
                imageVector = if (isSelectedAsFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Marcar usuario como favorito",
                tint = if (isSelectedAsFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PostAvatar(
    avatarUrl: String,
    author: String,
) {
    val isValidAvatarUrl =
        avatarUrl.startsWith("http") || avatarUrl.startsWith("data:image")

    Box(
        modifier =
            Modifier
                .size(AVATAR_SIZE)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (avatarUrl.isBlank() || !isValidAvatarUrl) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar de $author",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            GlideImage(
                model = avatarUrl,
                contentDescription = "Avatar de $author",
                modifier =
                    Modifier
                        .size(AVATAR_SIZE)
                        .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }
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
        modifier = Modifier.padding(top = PADDING_SMALL),
    )
}

@Composable
private fun PostActions(
    likes: Int,
    liked: Boolean,
    repliesCount: Int,
    onReply: () -> Unit,
    onLike: () -> Unit,
) {
    var likedState by remember { mutableStateOf(liked) }
    val scale by animateFloatAsState(
        targetValue = if (likedState) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 600f),
    )

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = PADDING_SMALL),
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
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onReply) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Responder",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = "$repliesCount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun generateUserHandle(author: String): String =
    "@${
        author
            .trim()
            .lowercase()
            .replace(" ", ".")
    }"

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

@Preview(showBackground = true)
@Composable
private fun PostCardPreview() {
    ScaffoldingV2Theme {
        PostCard(
            post =
                PostResponse(
                    id = 1,
                    message = "Este es un post de ejemplo",
                    parentId = 10,
                    authorId = 7,
                    author = "Paloma Aguirre",
                    avatarUrl = "https://ui-avatars.com/api/?name=Paloma Aguirre",
                    likes = 5,
                    liked = false,
                    date = "2026-01-01",
                ),
            isSelectedAsFavorite = true,
            onSelectedAsFavoriteAction = {},
            repliesCount = 3,
            onReply = {},
            onLike = {},
        )
    }
}