package ar.edu.unlam.mobile.scaffolding.ui.screens.postdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.components.post.PostCard
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen

@Composable
fun PostDetailScreen(
    selectedPost: PostResponse,
    postDetailViewModel: PostDetailViewModel,
    onBackAction: () -> Unit,
    onReplyAction: (PostResponse) -> Unit,
    onPostClick: (PostResponse) -> Unit,
) {
    val uiState by postDetailViewModel.uiState.collectAsState()

    LaunchedEffect(selectedPost.id) {
        postDetailViewModel.loadPostDetail(selectedPost)
    }

    when (val state = uiState) {
        is UiState.Idle -> {}

        is UiState.Loading -> {
            ShowLoadingStatusOnScreen()
        }

        is UiState.Success -> {
            PostDetailContent(
                data = state.data,
                onBackAction = onBackAction,
                onReplyAction = onReplyAction,
                onPostClick = onPostClick,
            )
        }

        is UiState.Error -> {
            ShowErrorMessageOnScreen(
                onRestoreStateAction = { postDetailViewModel.loadPostDetail(selectedPost) },
                errorMessage = state.error,
            )
        }
    }
}

@Composable
private fun PostDetailContent(
    data: PostDetailUiModel,
    onBackAction: () -> Unit,
    onReplyAction: (PostResponse) -> Unit,
    onPostClick: (PostResponse) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(PADDING_MEDIUM),
        verticalArrangement = Arrangement.spacedBy(PADDING_MEDIUM),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(onClick = onBackAction) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                    )
                }

                Text(
                    text = "Post",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 12.dp),
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        data.parentPost?.let { parentPost ->
            item {
                Text(
                    text = "En respuesta a",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )

                CompactPostPreview(
                    post = parentPost,
                    onClick = { onPostClick(parentPost) },
                )

                Divider()
            }
        }

        item {
            PostCard(
                post = data.selectedPost,
                isSelectedAsFavorite = false,
                onSelectedAsFavoriteAction = {},
                onReply = { onReplyAction(data.selectedPost) },
                onLike = {},
                onPostClick = {},
            )
        }

        item {
            Divider()

            Text(
                text = "Respuestas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = PADDING_MEDIUM),
            )
        }

        if (data.replies.isEmpty()) {
            item {
                Text(
                    text = "Este post todavía no tiene respuestas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(PADDING_LARGE),
                )
            }
        } else {
            items(data.replies) { reply ->
                PostCard(
                    post = reply,
                    isSelectedAsFavorite = false,
                    onSelectedAsFavoriteAction = {},
                    onReply = { onReplyAction(reply) },
                    onLike = {},
                    onPostClick = { onPostClick(reply) },
                )
            }
        }
    }
}

@Composable
private fun CompactPostPreview(
    post: PostResponse,
    onClick: () -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = PADDING_MEDIUM),
        shape =
            androidx.compose.foundation.shape
                .RoundedCornerShape(20.dp),
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(PADDING_MEDIUM),
        ) {
            Text(
                text = "${post.author} ${generateUserHandle(post.author)}",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = post.message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
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
