package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
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
import ar.edu.unlam.mobile.scaffolding.ui.components.post.PostCard
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen

@Composable
fun FeedScreen(
    feedViewModel: FeedViewModel,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    val uiState by feedViewModel.uiState.collectAsState()

    LaunchedEffect(true) {
        feedViewModel.loadPosts()
    }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(onNavigateToProfile) },
        floatingActionButton = { HomeFloatingActionButton(onNavigateToCreatePost) },
    ) { paddingValues ->

        FeedContent(
            modifier = Modifier.padding(paddingValues),
            uiState,
        ) { feedViewModel.reloadPostList() }
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
    onRetryAction: () -> Unit,
) {
    Box(modifier) {
        when (uiState) {
            is UiState.Idle -> {}

            is UiState.Loading -> {
                ShowLoadingStatusOnScreen()
            }

            is UiState.Success -> {
                LazyColumn {
                    items(uiState.data) { post ->

                        PostCard(post)
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
private fun BottomBar(onNavigateToProfile: () -> Unit = {}) {
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
            onClick = {},
            icon = {
                Icon(Icons.Default.Settings, contentDescription = null)
            },
            label = { BottomBarTextLabel("Ajustes") },
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
