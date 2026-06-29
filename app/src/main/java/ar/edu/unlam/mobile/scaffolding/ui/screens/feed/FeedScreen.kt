package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.components.feed.HomeFloatingActionButton
import ar.edu.unlam.mobile.scaffolding.ui.components.post.PostList
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen

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
            uiState,
            onRetry = { feedViewModel.reloadPostList() },
            onReply = { postId -> onNavigateToReply(postId) },
            onLike = { post ->
                if (post.liked) feedViewModel.unlikePost(post.id)
                else feedViewModel.likePost(post.id)
            },
            onAddFavorite = { post -> feedViewModel.addFavorite(post.author, post.avatarUrl) },
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
        modifier = Modifier.padding(PADDING_MEDIUM),
    )
}

@Composable
private fun FeedContent(
    modifier: Modifier,
    uiState: UiState<List<PostResponse>>,
    onRetry: () -> Unit,
    onReply: (Int) -> Unit,
    onLike: (PostResponse) -> Unit,
    onAddFavorite: (PostResponse) -> Unit,
) {
    Box(modifier) {
        when (uiState) {
            is UiState.Idle -> {}

            is UiState.Loading -> {
                ShowLoadingStatusOnScreen()
            }

            is UiState.Success -> {
                PostList(
                    posts = uiState.data,
                    onReply = onReply,
                    onLike = onLike,
                    onAddFavorite = onAddFavorite,
                )
            }

            is UiState.Error -> {
                ShowErrorMessageOnScreen(
                    onRetry,
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
