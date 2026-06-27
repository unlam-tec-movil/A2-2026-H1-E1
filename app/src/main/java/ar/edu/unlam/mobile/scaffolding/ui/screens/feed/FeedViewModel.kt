package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.UNKNOWN_ERROR_MESSAGE
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel
    @Inject
    constructor(
        private val postRepository: PostRepository,
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
    }
