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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.sampledata.samplePostsForFeedPreview
import ar.edu.unlam.mobile.scaffolding.ui.components.feed.HomeFloatingActionButton
import ar.edu.unlam.mobile.scaffolding.ui.components.post.PostCard
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShimmerFeed
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@Composable
fun FeedScreen(
    feedViewModel: FeedViewModel,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToReply: (PostResponse) -> Unit,
    onNavigateToPostDetail: (PostResponse) -> Unit = {},
    onShowSnackbar: (String) -> Unit,
) {
    val uiState by feedViewModel.uiState.collectAsState()
    val markedAsFavoriteSnackbar = stringResource(R.string.marked_as_favorite_snackbar)
    val unmarkedFromFavoritesSnackbar = stringResource(R.string.unmarked_from_favorite_snackbar)

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
                feedViewModel.toggleUserFavorite(authorId, author, avatarUrl, isMarkedAsFavorite)
                if (isMarkedAsFavorite) {
                    onShowSnackbar(unmarkedFromFavoritesSnackbar)
                } else {
                    onShowSnackbar(markedAsFavoriteSnackbar)
                }
            },
            onReply = { post -> onNavigateToReply(post) },
            onPostClick = { post -> onNavigateToPostDetail(post) },
            onLike = { post ->
                feedViewModel.togglePostLike(post.id, post.liked)
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
                ShowErrorMessageOnScreen(
                    onRetryAction,
                    errorMessage = uiState.error,
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FeedContentPreview() {
    ScaffoldingV2Theme {
        FeedContent(
            modifier = Modifier.padding(PADDING_MEDIUM),
            uiState = UiState.Success(samplePostsForFeedPreview),
            onRetryAction = {},
            onReply = {},
            onPostClick = {},
            onLike = {},
            onSelectedAsFavoriteAction = { _, _, _, _ -> },
        )
    }
}
