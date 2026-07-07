package ar.edu.unlam.mobile.scaffolding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.FavoriteUser
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.CREATE_NEW_POST
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.EDIT_PROFILE_INFO
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.FAVORITE_USER_POSTS
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.FEED
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.LOGIN
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.POST_DETAIL
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.REGISTER
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.USERS_MARKED_AS_FAVORITE
import ar.edu.unlam.mobile.scaffolding.ui.screens.feed.FavoriteUserPostsScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.feed.FavoriteUserScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.feed.FeedScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.login.LoginScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.PostCreationScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.PostCreationViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.postdetail.PostDetailScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.profile.ProfileScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.register.RegisterScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var currentScreen by remember { mutableStateOf(LOGIN) }
            var selectedPost by remember { mutableStateOf<PostResponse?>(null) }
            var selectedFavoriteUser by remember { mutableStateOf<FavoriteUser?>(null) }

            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()

            val snackBarAction: (String) -> Unit = { message ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }

            LaunchedEffect(Unit) {
                val savedToken = tokenManager.tokenFlow.first()
                if (savedToken.isNotEmpty()) {
                    currentScreen = FEED
                }
            }

            ScaffoldingV2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                            if (currentScreen == FEED ||
                                currentScreen == USERS_MARKED_AS_FAVORITE ||
                                currentScreen == EDIT_PROFILE_INFO
                            ) {
                                MainBottomBar(currentScreen) { currentScreen = it }
                            }
                        },
                    ) { paddingValues ->
                        Surface(modifier = Modifier.padding(paddingValues)) {
                            when (currentScreen) {
                                LOGIN -> {
                                    GoToLoginScreen { currentScreen = it }
                                }

                                REGISTER -> {
                                    GoToRegisterScreen { currentScreen = it }
                                }

                                FEED -> {
                                    GoToFeedScreen(
                                        onNavigate = { currentScreen = it },
                                        onReplyAction = { post ->
                                            selectedPost = post
                                            currentScreen = POST_DETAIL
                                        },
                                        onPostClick = { post ->
                                            selectedPost = post
                                            currentScreen = POST_DETAIL
                                        },
                                        onShowSnackBar = snackBarAction,
                                    )
                                }

                                CREATE_NEW_POST -> {
                                    GoToPostCreationScreen(
                                        onNavigate = { currentScreen = it },
                                        onShowSnackBar = snackBarAction,
                                    )
                                }

                                POST_DETAIL -> {
                                    GoToPostDetailScreen(
                                        selectedPost = selectedPost,
                                        onNavigate = { currentScreen = it },
                                        onReplyAction = { post ->
                                            selectedPost = post
                                            currentScreen = POST_DETAIL
                                        },
                                        onPostClick = { post ->
                                            selectedPost = post
                                            currentScreen = POST_DETAIL
                                        },
                                    )
                                }

                                EDIT_PROFILE_INFO -> {
                                    GoToProfileScreen(
                                        onNavigate = { currentScreen = it },
                                        onLogout = {
                                            tokenManager.clearToken()
                                            currentScreen = LOGIN
                                        },
                                    )
                                }

                                USERS_MARKED_AS_FAVORITE -> {
                                    GoToFavoritesScreen(
                                        onNavigate = { currentScreen = it },
                                        onFavoriteUserClick = { favoriteUser ->
                                            selectedFavoriteUser = favoriteUser
                                            currentScreen = FAVORITE_USER_POSTS
                                        },
                                    )
                                }

                                FAVORITE_USER_POSTS -> {
                                    GoToFavoriteUserPostsScreen(
                                        favoriteUser = selectedFavoriteUser,
                                        onNavigate = { currentScreen = it },
                                        onReplyAction = { post ->
                                            selectedPost = post
                                            currentScreen = POST_DETAIL
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MainBottomBar(
        currentScreen: AppScreen,
        onNavigate: (AppScreen) -> Unit,
    ) {
        NavigationBar {
            NavigationBarItem(
                selected = currentScreen == FEED,
                label = { Text(stringResource(R.string.nav_bar_home_button)) },
                onClick = { onNavigate(FEED) },
                icon = { Icon(Icons.AutoMirrored.Filled.Feed, contentDescription = null) },
            )

            NavigationBarItem(
                selected = currentScreen == USERS_MARKED_AS_FAVORITE,
                label = { Text(stringResource(R.string.nav_bar_favorites_button)) },
                onClick = { onNavigate(USERS_MARKED_AS_FAVORITE) },
                icon = { Icon(Icons.Default.Star, contentDescription = null) },
            )

            NavigationBarItem(
                selected = currentScreen == EDIT_PROFILE_INFO,
                label = { Text(stringResource(R.string.nav_bar_profile_button)) },
                onClick = { onNavigate(EDIT_PROFILE_INFO) },
                icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
            )
        }
    }

    @Composable
    private fun GoToLoginScreen(onNavigate: (AppScreen) -> Unit) {
        LoginScreen(
            hiltViewModel(),
            onLoginSuccess = { onNavigate(FEED) },
            onNavigateToRegister = { onNavigate(REGISTER) },
        )
    }

    @Composable
    private fun GoToRegisterScreen(onNavigate: (AppScreen) -> Unit) {
        RegisterScreen(
            registerViewModel = hiltViewModel(),
        ) { onNavigate(FEED) }
    }

    @Composable
    private fun GoToFeedScreen(
        onNavigate: (AppScreen) -> Unit,
        onReplyAction: (PostResponse) -> Unit,
        onPostClick: (PostResponse) -> Unit,
        onShowSnackBar: (String) -> Unit,
    ) {
        FeedScreen(
            feedViewModel = hiltViewModel(),
            onNavigateToCreatePost = { onNavigate(CREATE_NEW_POST) },
            onNavigateToReply = { post -> onReplyAction(post) },
            onNavigateToPostDetail = { post -> onPostClick(post) },
            onShowSnackbar = onShowSnackBar,
        )
    }

    @Composable
    private fun GoToPostCreationScreen(
        onNavigate: (AppScreen) -> Unit,
        onShowSnackBar: (String) -> Unit,
    ) {
        val postCreationViewModel = hiltViewModel<PostCreationViewModel>()

        LaunchedEffect(Unit) {
            postCreationViewModel.setParentId(0)
        }

        PostCreationScreen(
            postCreationViewModel = postCreationViewModel,
            onPostAction = { onNavigate(FEED) },
            onCancelAction = { onNavigate(FEED) },
            onShowSnackbar = onShowSnackBar,
        )
    }

    @Composable
    private fun GoToPostDetailScreen(
        selectedPost: PostResponse?,
        onNavigate: (AppScreen) -> Unit,
        onReplyAction: (PostResponse) -> Unit,
        onPostClick: (PostResponse) -> Unit,
    ) {
        selectedPost?.let { post ->
            PostDetailScreen(
                selectedPost = post,
                postDetailViewModel = hiltViewModel(),
                onBackAction = { onNavigate(FEED) },
                onReplyAction = onReplyAction,
                onPostClick = onPostClick,
            )
        }
    }

    @Composable
    private fun GoToFavoriteUserPostsScreen(
        favoriteUser: FavoriteUser?,
        onNavigate: (AppScreen) -> Unit,
        onReplyAction: (PostResponse) -> Unit,
    ) {
        favoriteUser?.let { user ->
            FavoriteUserPostsScreen(
                favoriteUser = user,
                viewModel = hiltViewModel(),
                onBack = { onNavigate(USERS_MARKED_AS_FAVORITE) },
                onReply = onReplyAction,
                onLike = {},
            )
        }
    }

    @Composable
    private fun GoToProfileScreen(
        onNavigate: (AppScreen) -> Unit,
        onLogout: suspend () -> Unit,
    ) {
        val scope = rememberCoroutineScope()

        ProfileScreen(
            profileInfoViewModel = hiltViewModel(),
            onNavigateBackAction = { onNavigate(FEED) },
            onLogout = {
                scope.launch {
                    onLogout()
                }
            },
        )
    }

    @Composable
    private fun GoToFavoritesScreen(
        onNavigate: (AppScreen) -> Unit,
        onFavoriteUserClick: (FavoriteUser) -> Unit,
    ) {
        FavoriteUserScreen(
            favoriteUsersViemodel = hiltViewModel(),
            onBackAction = { onNavigate(FEED) },
            onFavoriteUserClick = onFavoriteUserClick,
        )
    }
}
