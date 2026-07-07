package ar.edu.unlam.mobile.scaffolding.ui.screens.postdetail

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.UNKNOWN_ERROR_MESSAGE
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
    ) : BaseViewModel<PostDetailUiModel>() {
        fun loadPostDetail(selectedPost: PostResponse) {
            viewModelScope.launch {
                setUiAsLoading()

                try {
                    val refreshedPost = getRefreshedPost(selectedPost)

                    val parentPost = getParentPost(selectedPost)

                    val replies = getReplies(selectedPost)

                    setUiAsSuccess(PostDetailUiModel(refreshedPost, parentPost, replies))
                } catch (exception: Exception) {
                    val responseErrorMessage = exception.message ?: UNKNOWN_ERROR_MESSAGE
                    setUiAsError(responseErrorMessage)
                }
            }
        }

        private suspend fun getParentPost(selectedPost: PostResponse): PostResponse? =
            if (selectedPost.parentId != 0) {
                runCatching {
                    postRepository.getPostById(selectedPost.parentId)
                }.getOrNull()
            } else {
                null
            }

        private suspend fun getRefreshedPost(selectedPost: PostResponse): PostResponse =
            runCatching {
                postRepository.getPostById(selectedPost.id)
            }.getOrDefault(selectedPost)

        private suspend fun getReplies(selectedPost: PostResponse): List<PostResponse> =
            runCatching {
                postRepository.getRepliesByPostId(selectedPost.id)
            }.getOrDefault(emptyList())
    }
