package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.components.feed.HomeFloatingActionButton
import ar.edu.unlam.mobile.scaffolding.ui.components.post.PostCard
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@Composable
fun FeedScreen(
    feedViewModel: FeedViewModel,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToReply: (Int) -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
) {
    val uiState by feedViewModel.uiState.collectAsState()

    LaunchedEffect(true) {
        feedViewModel.loadPosts()
    }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(onNavigateToProfile, onNavigateToFavorites) },
        floatingActionButton = { HomeFloatingActionButton(onNavigateToCreatePost) },
    ) { paddingValues ->

        FeedContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onRetryAction = { feedViewModel.reloadPostList() },
            onSelectedAsFavoriteAction = { author, avatarUrl -> feedViewModel.markUserAsFavorite(author, avatarUrl) },
            onReply = { postId -> onNavigateToReply(postId) },
            onLike = { post ->
                if (post.liked) {
                    feedViewModel.unlikePost(post.id)
                } else {
                    feedViewModel.likePost(post.id)
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Inicio",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
        modifier = Modifier.padding(PADDING_MEDIUM),
    )
}

@Composable
private fun FeedContent(
    modifier: Modifier,
    uiState: UiState<List<PostUiModel>>,
    onRetryAction: () -> Unit,
    onSelectedAsFavoriteAction: (String, String) -> Unit,
    onReply: (Int) -> Unit,
    onLike: (PostResponse) -> Unit,
) {
    Box(
        modifier =
            modifier.background(MaterialTheme.colorScheme.background),
    ) {
        when (uiState) {
            is UiState.Idle -> {}

            is UiState.Loading -> {
                ShowLoadingStatusOnScreen()
            }

            is UiState.Success -> {
                LazyColumn {
                    items(uiState.data) { post ->

                        val apiResponse = post.apiPostResponse
                        val markedAsFavoriteValue = post.isMarkedAsFavorite

                        PostCard(
                            post = apiResponse,
                            isSelectedAsFavorite = markedAsFavoriteValue,
                            onSelectedAsFavoriteAction = { onSelectedAsFavoriteAction(apiResponse.author, apiResponse.avatarUrl) },
                            onReply = { onReply(apiResponse.id) },
                            onLike = { onLike(apiResponse) },
                        )
                    }
                }
            }

            is UiState.Error -> {
                ShowErrorMessageOnScreen(
                    onRetryAction,
                    errorMessage = uiState.error,
                )
            }
        }
    }
}

@Composable
private fun BottomBar(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
) {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(Icons.Default.Home, contentDescription = null)
            },
            label = { BottomBarTextLabel("Inicio") },
        )

        NavigationBarItem(
            selected = false,
            onClick = onNavigateToProfile,
            icon = {
                Icon(Icons.Default.AccountCircle, contentDescription = null)
            },
            label = { BottomBarTextLabel("Perfil") },
        )

        NavigationBarItem(
            selected = false,
            onClick = onNavigateToFavorites,
            icon = {
                Icon(Icons.Default.Star, contentDescription = null)
            },
            label = { BottomBarTextLabel("Favoritos") },
        )
    }
}

@Composable
private fun BottomBarTextLabel(textToShow: String) {
    Text(
        text = textToShow,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FeedContentPreview() {
    val samplePosts =
        listOf(
            PostUiModel(
                apiPostResponse =
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
                isMarkedAsFavorite = false,
            ),
            PostUiModel(
                apiPostResponse =
                    PostResponse(
                        id = 2,
                        author = "Usuario2",
                        message = "Otro post interesante en el feed",
                        likes = 5,
                        liked = true,
                        avatarUrl = "",
                        parentId = 0,
                        authorId = 2,
                        date = "2024-01-02",
                    ),
                isMarkedAsFavorite = true,
            ),
        )

    ScaffoldingV2Theme {
        FeedContent(
            modifier = Modifier.padding(PADDING_MEDIUM),
            uiState = UiState.Success(samplePosts),
            onRetryAction = {},
            onSelectedAsFavoriteAction = { _, _ -> },
            onReply = {},
            onLike = {},
        )
    }
}
