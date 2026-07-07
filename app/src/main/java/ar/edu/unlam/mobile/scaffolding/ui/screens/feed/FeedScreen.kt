package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.components.feed.HomeFloatingActionButton
import ar.edu.unlam.mobile.scaffolding.ui.components.post.PostCard
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShimmerFeed
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

private const val MARKED_AS_FAVORITE = "Usuario marcado como favorito"
private const val REMOVED_FROM_FAVORITE = "Usuario eliminado de favoritos"

@Composable
fun FeedScreen(
    feedViewModel: FeedViewModel,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToReply: (PostResponse) -> Unit,
    onNavigateToPostDetail: (PostResponse) -> Unit = {},
    onShowSnackbar: (String) -> Unit,
    onUnauthorized: () -> Unit = {},
) {
    val uiState by feedViewModel.uiState.collectAsState()

    LaunchedEffect(true) {
        feedViewModel.loadPosts()
    }

    Scaffold(
        topBar = { TopBar() },
        floatingActionButton = { HomeFloatingActionButton(onNavigateToCreatePost) },
    ) { paddingValues ->
        FeedContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onRetryAction = { feedViewModel.reloadPostList() },
            onSelectedAsFavoriteAction = { authorId, author, avatarUrl, isMarkedAsFavorite ->
                if (isMarkedAsFavorite) {
                    feedViewModel.unmarkUserFromFavorites(authorId)
                    onShowSnackbar(REMOVED_FROM_FAVORITE)
                } else {
                    feedViewModel.markUserAsFavorite(
                        authorId = authorId,
                        author = author,
                        avatarUrl = avatarUrl,
                    )
                    onShowSnackbar(MARKED_AS_FAVORITE)
                }
            },
            onReply = { post -> onNavigateToReply(post) },
            onPostClick = { post -> onNavigateToPostDetail(post) },
            onLike = { post ->
                if (post.liked) {
                    feedViewModel.unlikePost(post.id)
                } else {
                    feedViewModel.likePost(post.id)
                }
            },
            onUnauthorized = onUnauthorized,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    TopAppBar(
        expandedHeight = 48.dp,
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
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedContent(
    modifier: Modifier,
    uiState: UiState<List<PostUiModel>>,
    onRetryAction: () -> Unit,
    onSelectedAsFavoriteAction: (Int, String, String, Boolean) -> Unit,
    onReply: (PostResponse) -> Unit,
    onLike: (PostResponse) -> Unit,
    onPostClick: (PostResponse) -> Unit,
    onUnauthorized: () -> Unit,
) {
    var isRefreshing by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
    ) {
        when (uiState) {
            is UiState.Idle -> {}

            is UiState.Loading -> {
                ShimmerFeed()
            }

            is UiState.Success -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        onRetryAction()
                        isRefreshing = false
                    },
                ) {
                    LazyColumn {
                        items(uiState.data) { post ->
                            val apiResponse = post.apiPostResponse
                            val markedAsFavoriteValue = post.isMarkedAsFavorite

                            PostCard(
                                post = apiResponse,
                                isSelectedAsFavorite = markedAsFavoriteValue,
                                onSelectedAsFavoriteAction = {
                                    onSelectedAsFavoriteAction(
                                        apiResponse.authorId,
                                        apiResponse.author,
                                        apiResponse.avatarUrl,
                                        markedAsFavoriteValue,
                                    )
                                },
                                repliesCount = post.repliesCount,
                                onReply = { onReply(apiResponse) },
                                onLike = { onLike(apiResponse) },
                                onPostClick = { onPostClick(apiResponse) },
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                if (uiState.error.contains("401")) {
                    LaunchedEffect(Unit) {
                        onUnauthorized()
                    }
                } else {
                    ShowErrorMessageOnScreen(
                        onRetryAction,
                        errorMessage = uiState.error,
                    )
                }
            }
        }
    }
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
                repliesCount = 2,
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
                repliesCount = 0,
            ),
        )

    ScaffoldingV2Theme {
        FeedContent(
            modifier = Modifier.padding(PADDING_MEDIUM),
            uiState = UiState.Success(samplePosts),
            onRetryAction = {},
            onReply = {},
            onPostClick = {},
            onLike = {},
            onSelectedAsFavoriteAction = { _, _, _, _ -> },
            onUnauthorized = {},
        )
    }
}
