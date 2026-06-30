package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.FavoritesRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.UNKNOWN_ERROR_MESSAGE
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel
    @Inject
    constructor(
        private val postRepository: PostRepository,
        private val tokenManager: TokenManager,
        private val favoritesRepository: FavoritesRepository,
    ) : BaseViewModel<List<PostResponse>>() {
        init {
            loadPosts()
        }

        fun loadPosts() {
            viewModelScope.launch {
                setUiAsLoading()

                try {
                    val postList = postRepository.getPostList()

                    setUiAsSuccess(postList)
                } catch (exception: Exception) {
                    val responseMessage = exception.message ?: UNKNOWN_ERROR_MESSAGE

                    setUiAsError(responseMessage)
                }
            }
        }

        fun reloadPostList() {
            loadPosts()
        }

        fun likePost(postId: Int) {
            viewModelScope.launch {
                try {
                    val token = tokenManager.tokenFlow.first()
                    postRepository.likePost(postId, token)
                    loadPosts()
                } catch (_: Exception) {
                }
            }
        }

        fun unlikePost(postId: Int) {
            viewModelScope.launch {
                try {
                    val token = tokenManager.tokenFlow.first()
                    postRepository.unlikePost(postId, token)
                    loadPosts()
                } catch (_: Exception) {
                }
            }
        }

        fun addFavorite(
            author: String,
            avatarUrl: String,
        ) {
            viewModelScope.launch {
                favoritesRepository.addFavorite(author, avatarUrl)
            }
        }
    }
