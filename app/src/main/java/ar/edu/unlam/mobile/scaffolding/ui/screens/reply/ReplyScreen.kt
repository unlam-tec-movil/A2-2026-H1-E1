package ar.edu.unlam.mobile.scaffolding.ui.screens.reply

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.components.post.PostReplyCard
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@Composable
fun PostReplyScreen(
    replyViewModel: ReplyViewModel,
    onReturnClickAction: () -> Unit,
    replyPostId: Int,
) {
    val uiState by replyViewModel.uiState.collectAsState()
    val restoreState = { replyViewModel.reloadPostRepliesList(replyPostId) }

    LaunchedEffect(true) {
        replyViewModel.loadPostRepliesInfo(replyPostId)
    }

    when (val state = uiState) {
        is UiState.Idle -> {}

        is UiState.Loading -> {
            ShowLoadingStatusOnScreen()
        }

        is UiState.Success<ReplyScreenUiData> -> {
            ShowPostReplyForm(onReturnClickAction, state.data)
        }

        is UiState.Error -> {
            ShowErrorMessageOnScreen(
                restoreState,
                state.error,
            )
        }
    }
}

@Composable
private fun ShowPostReplyForm(
    onReturnClickAction: () -> Unit,
    data: ReplyScreenUiData,
) {
    Card(
        modifier = Modifier.padding(PADDING_MEDIUM),
        shape = RoundedCornerShape(PADDING_MEDIUM),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(horizontal = PADDING_LARGE),
            ) {
                IconButton(
                    onClick = onReturnClickAction,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
                Text(
                    text = "Comentarios",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            ParentPostCard(data.parentPost)

            if (data.postReplies.isNullOrEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Sé el primero en comentar",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                LazyColumn {
                    items(data.postReplies) { reply ->

                        PostReplyCard(
                            reply,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParentPostCard(parentPost: PostResponse) {
    Card(
        modifier = Modifier.padding(PADDING_MEDIUM).fillMaxWidth(),
        shape = RoundedCornerShape(PADDING_LARGE),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(modifier = Modifier.padding(PADDING_MEDIUM)) {
            Text(
                text = parentPost.author,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = parentPost.date,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(PADDING_MEDIUM))

        Text(
            text = parentPost.message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(PADDING_MEDIUM),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ParentPostCardPreview() {
    ScaffoldingV2Theme {
        ParentPostCard(
            PostResponse(
                id = 1,
                author = "Usuario1",
                message = "Este es un post de ejemplo para la preview del feed",
                likes = 10,
                liked = false,
                avatarUrl = "",
                parentId = 0,
                authorId = 1,
                date = "2024-01-01",
            ),
        )
    }
}
