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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.ui.screens.detail.PostDetailScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.CREATE_NEW_POST
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.EDIT_PROFILE_INFO
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.FEED
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.LOGIN
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.POST_DETAIL
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.REGISTER
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.REPLY_POST
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen.USERS_MARKED_AS_FAVORITE
import ar.edu.unlam.mobile.scaffolding.ui.screens.feed.FavoriteUserScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.feed.FeedScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.login.LoginScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.PostCreationScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.PostCreationViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.profile.ProfileScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.register.RegisterScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.reply.PostReplyScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val HOME_LABEL = "Inicio"
private const val FAVORITES_LABEL = "Favoritos"
private const val PROFILE_LABEL = "Perfil"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf(LOGIN) }
            var replyPostId by remember { mutableIntStateOf(0) }
            var postDetailId by remember { mutableIntStateOf(0) }
            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()

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
                            @Suppress("ktlint:standard:max-line-length")
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
                                    GoToLoginScreen(onNavigate = { currentScreen = it })
                                }

                                REGISTER -> {
                                    GoToRegisterScreen { currentScreen = it }
                                }

                                FEED -> {
                                    GoToFeedScreen(
                                        onNavigate = { currentScreen = it },
                                        onReplyAction = { replyPostId = it },
                                        onPostDetailAction = { postDetailId = it },
                                        onShowSnackBar = {
                                            launchSnackBarCoroutine(
                                                snackbarHostState = snackbarHostState,
                                                snackBarMessage = it,
                                                coroutineScope = coroutineScope,
                                            )
                                        },
                                    )
                                }

                                CREATE_NEW_POST -> {
                                    GoToPostCreationScreen(
                                        { currentScreen = it },
                                        {
                                            launchSnackBarCoroutine(
                                                snackbarHostState = snackbarHostState,
                                                snackBarMessage = it,
                                                coroutineScope = coroutineScope,
                                            )
                                        },
                                    )
                                }

                                REPLY_POST -> {
                                    GoToReplyScreen(
                                        replyPostId = replyPostId,
                                        onNavigate = { currentScreen = it },
                                    )
                                }

                                POST_DETAIL -> {
                                    GoToPostDetailScreen(
                                        onNavigate = { currentScreen = it },
                                        onReplyAction = { replyPostId = it },
                                        onShowSnackBar = {
                                            launchSnackBarCoroutine(
                                                snackbarHostState = snackbarHostState,
                                                snackBarMessage = it,
                                                coroutineScope = coroutineScope,
                                            )
                                        },
                                        postDetailId = postDetailId,
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
                                    GoToFavoritesScreen { currentScreen = it }
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
                label = { Text(HOME_LABEL) },
                onClick = { onNavigate(FEED) },
                icon = { Icon(Icons.AutoMirrored.Filled.Feed, contentDescription = null) },
            )
            NavigationBarItem(
                selected = currentScreen == USERS_MARKED_AS_FAVORITE,
                label = { Text(FAVORITES_LABEL) },
                onClick = { onNavigate(USERS_MARKED_AS_FAVORITE) },
                icon = { Icon(Icons.Default.Star, contentDescription = null) },
            )
            NavigationBarItem(
                selected = currentScreen == EDIT_PROFILE_INFO,
                label = { Text(PROFILE_LABEL) },
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
        onReplyAction: (Int) -> Unit,
        onPostDetailAction: (Int) -> Unit,
        onShowSnackBar: (String) -> Unit,
    ) {
        FeedScreen(
            feedViewModel = hiltViewModel(),
            onNavigateToCreatePost = {
                onNavigate(CREATE_NEW_POST)
            },
            onNavigateToReply = { postId ->
                onReplyAction(postId)
                onNavigate(REPLY_POST)
            },
            onNavigateToPostDetail = { postId ->
                onPostDetailAction(postId)
                onNavigate(POST_DETAIL)
            },
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
        onNavigate: (AppScreen) -> Unit,
        onReplyAction: (Int) -> Unit,
        onShowSnackBar: (String) -> Unit,
        postDetailId: Int,
    ) {
        PostDetailScreen(
            postId = postDetailId,
            postDetailViewModel = hiltViewModel(),
            onBack = { onNavigate(FEED) },
            onReply = {
                onReplyAction(it)
                onNavigate(REPLY_POST)
            },
            onShowSnackbar = onShowSnackBar,
        )
    }

    @Composable
    private fun GoToReplyScreen(
        replyPostId: Int,
        onNavigate: (AppScreen) -> Unit,
    ) {
        PostReplyScreen(hiltViewModel(), { onNavigate(FEED) }, replyPostId)
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
    private fun GoToFavoritesScreen(onNavigate: (AppScreen) -> Unit) {
        FavoriteUserScreen(hiltViewModel()) {
            onNavigate(FEED)
        }
    }

    private fun launchSnackBarCoroutine(
        snackbarHostState: SnackbarHostState,
        snackBarMessage: String,
        coroutineScope: CoroutineScope,
    ) {
        coroutineScope.launch { snackbarHostState.showSnackbar(snackBarMessage) }
    }
}
