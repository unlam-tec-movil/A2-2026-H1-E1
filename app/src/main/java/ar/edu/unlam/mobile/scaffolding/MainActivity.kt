package ar.edu.unlam.mobile.scaffolding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.enums.AppScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.feed.FavoriteUserScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.feed.FeedScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.login.LoginScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.PostCreationScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.profile.ProfileScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.register.RegisterScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.reply.ReplyScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }
            var replyPostId by remember { mutableIntStateOf(0) }
            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()

            ScaffoldingV2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                    ) { paddingValues ->

                        Surface(modifier = Modifier.padding(paddingValues)) {
                            when (currentScreen) {
                                AppScreen.LOGIN -> {
                                    LoginScreen(
                                        hiltViewModel(),
                                        onLoginSuccess = { currentScreen = AppScreen.FEED },
                                        onNavigateToRegister = {
                                            currentScreen =
                                                AppScreen.REGISTER
                                        },
                                    )
                                }

                                AppScreen.REGISTER -> {
                                    RegisterScreen(hiltViewModel()) {
                                        currentScreen = AppScreen.FEED
                                    }
                                }

                                AppScreen.FEED -> {
                                    FeedScreen(
                                        feedViewModel = hiltViewModel(),
                                        onNavigateToCreatePost = {
                                            currentScreen = AppScreen.CREATE_NEW_POST
                                        },
                                        onNavigateToProfile = {
                                            currentScreen = AppScreen.EDIT_PROFILE_INFO
                                        },
                                        onNavigateToReply = { postId ->
                                            replyPostId = postId
                                            currentScreen = AppScreen.REPLY_POST
                                        },
                                        onNavigateToFavorites = {
                                            currentScreen = AppScreen.USERS_MARKED_AS_FAVORITE
                                        },
                                        onShowSnackbar = { message ->
                                            launchSnackBarCoroutine(
                                                snackbarHostState,
                                                message,
                                                coroutineScope,
                                            )
                                        },
                                    )
                                }

                                AppScreen.CREATE_NEW_POST -> {
                                    PostCreationScreen(
                                        postCreationViewModel = hiltViewModel(),
                                        onPostAction = { currentScreen = AppScreen.FEED },
                                        onCancelAction = { currentScreen = AppScreen.FEED },
                                    ) { snackBarMessage ->

                                        launchSnackBarCoroutine(
                                            snackbarHostState,
                                            snackBarMessage,
                                            coroutineScope,
                                        )
                                    }
                                }

                                AppScreen.REPLY_POST -> {
                                    ReplyScreen(
                                        parentPostId = replyPostId,
                                        postCreationViewModel = hiltViewModel(),
                                        onPostAction = { currentScreen = AppScreen.FEED },
                                        onCancelAction = { currentScreen = AppScreen.FEED },
                                        onShowSnackbar = { message ->
                                            launchSnackBarCoroutine(
                                                snackbarHostState,
                                                message,
                                                coroutineScope,
                                            )
                                        },
                                    )
                                }

                                AppScreen.EDIT_PROFILE_INFO -> {
                                    ProfileScreen(hiltViewModel()) {
                                        currentScreen = AppScreen.FEED
                                    }
                                }

                                AppScreen.USERS_MARKED_AS_FAVORITE -> {
                                    FavoriteUserScreen(hiltViewModel()) {
                                        currentScreen = AppScreen.FEED
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun launchSnackBarCoroutine(
        snackbarHostState: SnackbarHostState,
        snackBarMessage: String,
        coroutineScope: CoroutineScope,
    ) {
        coroutineScope.launch { snackbarHostState.showSnackbar(snackBarMessage) }
    }
}
