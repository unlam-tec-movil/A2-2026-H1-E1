package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.FavoriteUser
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.UNKNOWN_ERROR_MESSAGE
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoriteUserPostsUiModel(
    val favoriteUser: FavoriteUser,
    val posts: List<PostResponse>,
)

@HiltViewModel
class FavoriteUserPostsViewModel
    @Inject
    constructor(
        private val postRepository: PostRepository,
    ) : BaseViewModel<FavoriteUserPostsUiModel>() {
        fun loadPostsByFavoriteUser(favoriteUser: FavoriteUser) {
            viewModelScope.launch {
                setUiAsLoading()

                try {
                    val posts =
                        postRepository
                            .getPostList()
                            .filter { post -> post.authorId == favoriteUser.authorId }

                    setUiAsSuccess(
                        FavoriteUserPostsUiModel(
                            favoriteUser = favoriteUser,
                            posts = posts,
                        ),
                    )
                } catch (exception: Exception) {
                    setUiAsError(exception.message ?: UNKNOWN_ERROR_MESSAGE)
                }
            }
        }
    }
