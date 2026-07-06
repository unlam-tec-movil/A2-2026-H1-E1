package ar.edu.unlam.mobile.scaffolding.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.components.post.PostCard
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: Int,
    postDetailViewModel: PostDetailViewModel,
    onBack: () -> Unit,
    onReply: (Int) -> Unit,
    onShowSnackbar: (String) -> Unit,
    onLike: (PostResponse) -> Unit = {},
    onUnlike: (PostResponse) -> Unit = {},
) {
    val uiState by postDetailViewModel.uiState.collectAsState()

    LaunchedEffect(postId) {
        postDetailViewModel.loadPostDetail(postId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            )
        },
    ) { paddingValues ->
        when (uiState) {
            is UiState.Idle -> {}

            is UiState.Loading -> {
                ShowLoadingStatusOnScreen()
            }

            is UiState.Success -> {
                val detailState = (uiState as UiState.Success<PostDetailUiState>).data
                val parentPost = detailState.parentPost
                val replies = detailState.replies

                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(PADDING_MEDIUM),
                    verticalArrangement = Arrangement.spacedBy(PADDING_MEDIUM),
                ) {
                    if (parentPost != null) {
                        item {
                            ParentPostCard(
                                post = parentPost,
                                onLike = {
                                    if (parentPost.liked) {
                                        postDetailViewModel.unlikePost(parentPost.id)
                                        onUnlike(parentPost)
                                    } else {
                                        postDetailViewModel.likePost(parentPost.id)
                                        onLike(parentPost)
                                    }
                                },
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Respuestas (${replies.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = PADDING_MEDIUM),
                        )
                    }

                    if (replies.isEmpty()) {
                        item {
                            Text(
                                text = "Sin respuestas aún",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = PADDING_MEDIUM),
                            )
                        }
                    } else {
                        items(replies) { reply ->
                            PostCard(
                                post = reply,
                                isSelectedAsFavorite = false,
                                onSelectedAsFavoriteAction = {},
                                onReply = { onReply(reply.id) },
                                onLike = {
                                    if (reply.liked) {
                                        postDetailViewModel.unlikePost(reply.id)
                                        onUnlike(reply)
                                    } else {
                                        postDetailViewModel.likePost(reply.id)
                                        onLike(reply)
                                    }
                                },
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                ShowErrorMessageOnScreen(
                    onRestoreStateAction = { postDetailViewModel.loadPostDetail(postId) },
                    errorMessage = (uiState as UiState.Error).error,
                )
            }
        }
    }
}

@Composable
private fun ParentPostCard(
    post: PostResponse,
    onLike: () -> Unit = {},
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
    ) {
        Column(
            modifier = Modifier.padding(PADDING_MEDIUM),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                Spacer(modifier = Modifier.width(PADDING_MEDIUM))

                Text(
                    text = post.author,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(PADDING_MEDIUM))

            Text(
                text = post.message,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(PADDING_MEDIUM))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onLike) {
                    Icon(
                        imageVector = if (post.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Me gusta",
                        tint = if (post.liked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = "${post.likes}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
