package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.FavoriteUser
import ar.edu.unlam.mobile.scaffolding.ui.components.post.PostCard
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@Composable
fun FavoriteUserPostsScreen(
    favoriteUser: FavoriteUser,
    viewModel: FavoriteUserPostsViewModel,
    onBack: () -> Unit,
    onReply: (ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse) -> Unit,
    onLike: (ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler {
        onBack()
    }

    LaunchedEffect(favoriteUser.authorId) {
        viewModel.loadPostsByFavoriteUser(favoriteUser)
    }

    when (val state = uiState) {
        UiState.Idle -> {}

        UiState.Loading -> {
            ShowLoadingStatusOnScreen()
        }

        is UiState.Error -> {
            ShowErrorMessageOnScreen(
                onRestoreStateAction = {},
                errorMessage = state.error,
            )
        }

        is UiState.Success -> {
            FavoriteUserPostsContent(
                state.data,
                onReply,
                onLike,
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun FavoriteUserPostsContent(
    uiModel: FavoriteUserPostsUiModel,
    onReply: (ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse) -> Unit,
    onLike: (ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlideImage(
                model = uiModel.favoriteUser.avatarUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(70.dp)
                        .clip(CircleShape),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = uiModel.favoriteUser.author,
                    style = MaterialTheme.typography.headlineSmall,
                )

                Text(
                    text = "@${uiModel.favoriteUser.author.lowercase().replace(" ", ".")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Text(
                    text = "${uiModel.posts.size} publicaciones",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        HorizontalDivider()

        if (uiModel.posts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Este usuario todavía no tiene publicaciones en tu feed.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            LazyColumn {
                items(uiModel.posts) { post ->

                    PostCard(
                        post = post,
                        isSelectedAsFavorite = true,
                        onSelectedAsFavoriteAction = {},
                        onReply = { onReply(post) },
                        onLike = { onLike(post) },
                    )
                }
            }
        }
    }
}
