package ar.edu.unlam.mobile.scaffolding.ui.screens.detail

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.UNKNOWN_ERROR_MESSAGE
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostDetailUiState(
    val parentPost: PostResponse? = null,
    val replies: List<PostResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class PostDetailViewModel
    @Inject
    constructor(
        private val postRepository: PostRepository,
        private val tokenManager: TokenManager,
    ) : BaseViewModel<PostDetailUiState>() {
        private var currentPostId: Int = 0

        fun loadPostDetail(postId: Int) {
            currentPostId = postId
            viewModelScope.launch {
                setUiAsLoading()

                try {
                    val allFeedPosts = postRepository.getPostList()
                    val parentPost = allFeedPosts.firstOrNull { it.id == postId }
                    val replies = postRepository.getRepliesForPost(postId)

                    setUiAsSuccess(PostDetailUiState(parentPost = parentPost, replies = replies))
                } catch (exception: Exception) {
                    val responseMessage = exception.message ?: UNKNOWN_ERROR_MESSAGE
                    setUiAsError(responseMessage)
                }
            }
        }

        fun likePost(postId: Int) {
            viewModelScope.launch {
                try {
                    val token = tokenManager.tokenFlow.first()
                    postRepository.likePost(postId, token)
                    loadPostDetail(currentPostId)
                } catch (_: Exception) {
                }
            }
        }

        fun unlikePost(postId: Int) {
            viewModelScope.launch {
                try {
                    val token = tokenManager.tokenFlow.first()
                    postRepository.unlikePost(postId, token)
                    loadPostDetail(currentPostId)
                } catch (_: Exception) {
                }
            }
        }
    }
