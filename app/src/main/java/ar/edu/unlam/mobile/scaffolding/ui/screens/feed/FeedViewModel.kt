package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.FavoriteUser
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.FavoriteUserRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.UNKNOWN_ERROR_MESSAGE
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel
    @Inject
    constructor(
        private val postRepository: PostRepository,
        private val favoriteUserRepository: FavoriteUserRepository,
        private val tokenManager: TokenManager,
    ) : BaseViewModel<List<PostUiModel>>() {
        init {
            loadPosts()
        }

        fun loadPosts() {
            viewModelScope.launch {
                setUiAsLoading()

                try {
                    val uiPosts = createPostUiModelList()

                    setUiAsSuccess(uiPosts)
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

        fun markUserAsFavorite(
            author: String,
            avatarUrl: String,
        ) {
            viewModelScope.launch {
                try {
                    val user = FavoriteUser(author, avatarUrl)

                    favoriteUserRepository.saveUserAsFavorite(user)

                    updateFavoritePosts(user)
                } catch (exception: Exception) {
                    val responseMessage = exception.message ?: UNKNOWN_ERROR_MESSAGE

                    setUiAsError(responseMessage)
                }
            }
        }

        fun unmarkUserFromFavorites(author: String) {
            viewModelScope.launch {
                favoriteUserRepository.deleteUserFromFavorites(author)

                removeFromFavorites(author)
            }
        }

        private suspend fun createPostUiModelList(): List<PostUiModel> {
            val postList = postRepository.getPostList()

            val favoriteUsers = favoriteUserRepository.getAllFavoriteUsers().first()

            val uiPosts: List<PostUiModel> =
                postList.map { currentPost ->
                    val isSelectedAsFavorite =
                        favoriteUsers.any { savedUser ->
                            savedUser.author == currentPost.author
                        }

                    PostUiModel(currentPost, isSelectedAsFavorite)
                }
            return uiPosts
        }

        private fun updateFavoritePosts(user: FavoriteUser) {
            val currentUiState = _uiState.value

            if (currentUiState is UiState.Success) {
                val currentPostResponseList = currentUiState.data

                val updatedPostList =
                    currentPostResponseList.map { currentPost ->

                        if (currentPost.apiPostResponse.author == user.author) {
                            currentPost.copy(isMarkedAsFavorite = true)
                        } else {
                            currentPost
                        }
                    }

                setUiAsSuccess(updatedPostList)
            }
        }

        private fun removeFromFavorites(author: String) {
            val currentUiState = _uiState.value

            if (currentUiState is UiState.Success) {
                val currentPostResponseList = currentUiState.data

                val updatedPostList =
                    currentPostResponseList.map { currentPost ->

                        if (currentPost.apiPostResponse.author == author) {
                            currentPost.copy(isMarkedAsFavorite = false)
                        } else {
                            currentPost
                        }
                    }

                setUiAsSuccess(updatedPostList)
            }
        }
    }
