package ar.edu.unlam.mobile.scaffolding.ui.screens.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoRequest
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.ProfileInfoRepository
import ar.edu.unlam.mobile.scaffolding.ui.components.PostUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
sealed interface HelloMessageUIState {
    data class Success(
        val message: String,
    ) : HelloMessageUIState

    data object Loading : HelloMessageUIState

    data class Error(
        val message: String,
    ) : HelloMessageUIState
}

data class HomeUIState(
    val helloMessageState: HelloMessageUIState = HelloMessageUIState.Loading,
    val posts: List<PostUiModel> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val postRepository: PostRepository,
        private val profileInfoRepository: ProfileInfoRepository,
        private val tokenManager: TokenManager,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(HomeUIState())
        val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

        init {
            loadFeed()
        }

        fun loadFeed() {
            viewModelScope.launch {
                _uiState.update { it.copy(loading = true, helloMessageState = HelloMessageUIState.Loading) }
                try {
                    // Cargar posts
                    val postList = postRepository.getPostList()
                    val uiPosts =
                        postList.map { post ->
                            PostUiModel(
                                id = post.id,
                                message = post.message,
                                author = post.author,
                                avatarUrl = post.avatarUrl,
                                likes = post.likes,
                                liked = post.liked,
                                date = post.date,
                            )
                        }

                    // Cargar información de perfil para el mensaje de bienvenida
                    val token = tokenManager.tokenFlow.first()
                    val profileInfo = profileInfoRepository.getCurrentProfileInfo(ProfileInfoRequest(token))

                    _uiState.update {
                        it.copy(
                            loading = false,
                            posts = uiPosts,
                            helloMessageState = HelloMessageUIState.Success("¡Hola, ${profileInfo.name}!"),
                        )
                    }
                } catch (exception: Exception) {
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = exception.message ?: "Error desconocido",
                            helloMessageState = HelloMessageUIState.Error(exception.message ?: "Error al cargar datos"),
                        )
                    }
                }
            }
        }
    }
