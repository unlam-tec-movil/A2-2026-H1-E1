package ar.edu.unlam.mobile.scaffolding.ui.screens.postdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostDetailUiModel(
    val selectedPost: PostResponse,
    val parentPost: PostResponse?,
    val replies: List<PostResponse>,
)

@HiltViewModel
class PostDetailViewModel
    @Inject
    constructor(
        private val postRepository: PostRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<UiState<PostDetailUiModel>>(UiState.Idle)
        val uiState: StateFlow<UiState<PostDetailUiModel>> = _uiState.asStateFlow()

        fun loadPostDetail(selectedPost: PostResponse) {
            viewModelScope.launch {
                _uiState.value = UiState.Loading

                try {
                    val refreshedPost =
                        runCatching {
                            postRepository.getPostById(selectedPost.id)
                        }.getOrDefault(selectedPost)

                    val parentPost =
                        if (refreshedPost.parentId != 0) {
                            runCatching {
                                postRepository.getPostById(refreshedPost.parentId)
                            }.getOrNull()
                        } else {
                            null
                        }

                    val replies =
                        runCatching {
                            postRepository.getRepliesByPostId(refreshedPost.id)
                        }.getOrDefault(emptyList())

                    _uiState.value =
                        UiState.Success(
                            PostDetailUiModel(
                                selectedPost = refreshedPost,
                                parentPost = parentPost,
                                replies = replies,
                            ),
                        )
                } catch (exception: Exception) {
                    _uiState.value = UiState.Error(exception.message ?: "Error desconocido")
                }
            }
        }
    }
