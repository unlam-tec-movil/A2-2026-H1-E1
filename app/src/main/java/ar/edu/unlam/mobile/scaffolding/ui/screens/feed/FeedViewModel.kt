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
                    setUiAsSuccess(createPostUiModelList())
                } catch (exception: Exception) {
                    setUiAsError(exception.message ?: UNKNOWN_ERROR_MESSAGE)
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
            authorId: Int,
            author: String,
            avatarUrl: String,
        ) {
            viewModelScope.launch {
                try {
                    val user =
                        FavoriteUser(
                            authorId = authorId,
                            author = author,
                            avatarUrl = avatarUrl,
                        )

                    favoriteUserRepository.saveUserAsFavorite(user)
                    updateFavoritePosts(user)
                } catch (exception: Exception) {
                    setUiAsError(exception.message ?: UNKNOWN_ERROR_MESSAGE)
                }
            }
        }

        fun unmarkUserFromFavorites(authorId: Int) {
            viewModelScope.launch {
                favoriteUserRepository.deleteUserFromFavorites(authorId)
                removeFromFavorites(authorId)
            }
        }

        private suspend fun createPostUiModelList(): List<PostUiModel> {
            val postList = postRepository.getPostList()
            val favoriteUsers = favoriteUserRepository.getAllFavoriteUsers().first()

            return postList.map { currentPost ->
                val isSelectedAsFavorite =
                    favoriteUsers.any { savedUser ->
                        savedUser.authorId == currentPost.authorId
                    }

                val repliesCount =
                    runCatching {
                        postRepository.getRepliesByPostId(currentPost.id).size
                    }.getOrDefault(0)

                PostUiModel(
                    apiPostResponse = currentPost,
                    isMarkedAsFavorite = isSelectedAsFavorite,
                    repliesCount = repliesCount,
                )
            }
        }

        private fun updateFavoritePosts(user: FavoriteUser) {
            val currentUiState = _uiState.value

            if (currentUiState is UiState.Success) {
                val updatedPostList =
                    currentUiState.data.map { currentPost ->
                        if (currentPost.apiPostResponse.authorId == user.authorId) {
                            currentPost.copy(isMarkedAsFavorite = true)
                        } else {
                            currentPost
                        }
                    }

                setUiAsSuccess(updatedPostList)
            }
        }

        private fun removeFromFavorites(authorId: Int) {
            val currentUiState = _uiState.value

            if (currentUiState is UiState.Success) {
                val updatedPostList =
                    currentUiState.data.map { currentPost ->
                        if (currentPost.apiPostResponse.authorId == authorId) {
                            currentPost.copy(isMarkedAsFavorite = false)
                        } else {
                            currentPost
                        }
                    }

                setUiAsSuccess(updatedPostList)
            }
        }
    }
