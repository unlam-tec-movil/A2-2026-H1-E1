package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.components.feed.HomeFloatingActionButton
import ar.edu.unlam.mobile.scaffolding.ui.components.post.PostCard
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
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
    onNavigateToReply: (Int) -> Unit = {},
    onNavigateToPostDetail: (Int) -> Unit = {},
    onShowSnackbar: (String) -> Unit,
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
            onSelectedAsFavoriteAction = { author, avatarUrl, isMarkedAsFavorite ->
                if (isMarkedAsFavorite) {
                    feedViewModel.unmarkUserFromFavorites(author)
                    onShowSnackbar(REMOVED_FROM_FAVORITE)
                } else {
                    feedViewModel.markUserAsFavorite(author, avatarUrl)
                    onShowSnackbar(MARKED_AS_FAVORITE)
                }
            },
            onReply = { postId -> onNavigateToReply(postId) },
            onPostClick = { postId -> onNavigateToPostDetail(postId) },
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
    onSelectedAsFavoriteAction: (String, String, Boolean) -> Unit,
    onReply: (Int) -> Unit,
    onPostClick: (Int) -> Unit,
    onLike: (PostResponse) -> Unit,
) {
    var selectedUser by remember { mutableStateOf<PostResponse?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }

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
                                replyCount = post.replyCount,
                                onSelectedAsFavoriteAction = {
                                    onSelectedAsFavoriteAction(
                                        apiResponse.author,
                                        apiResponse.avatarUrl,
                                        markedAsFavoriteValue,
                                    )
                                },
                                onClick = { onPostClick(apiResponse.id) },
                                onUserClick = { selectedUser = apiResponse },
                                onReply = { onReply(apiResponse.id) },
                                onLike = { onLike(apiResponse) },
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

    if (selectedUser != null) {
        val user = selectedUser!!
        AlertDialog(
            onDismissRequest = { selectedUser = null },
            title = { Text(user.author) },
            text = {
                Column {
                    Text("ID: ${user.authorId}")
                    if (user.avatarUrl.isNotEmpty()) {
                        Text("Avatar: ${user.avatarUrl}")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedUser = null }) {
                    Text("Cerrar")
                }
            },
        )
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
            onReply = {},
            onPostClick = {},
            onLike = {},
            onSelectedAsFavoriteAction = { _, _, _ -> },
        )
    }
}
