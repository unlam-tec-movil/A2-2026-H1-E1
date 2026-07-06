package ar.edu.unlam.mobile.scaffolding.ui.screens.reply

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReplyViewModel
    @Inject
    constructor(
        private val tokenManager: TokenManager,
        private val postRepository: PostRepository,
    ) : BaseViewModel<ReplyScreenUiData>() {
        fun reloadPostRepliesList(replyPostId: Int) {
            loadPostRepliesInfo(replyPostId)
        }

        suspend fun getPostRepliesById(postId: Int): List<PostResponse> =
            postRepository.getPostReplies(postId, tokenManager.tokenFlow.first())

        suspend fun getParentPostById(postId: Int): PostResponse =
            postRepository.getPostById(
                postId,
                userToken = tokenManager.tokenFlow.first(),
            )

        fun loadPostRepliesInfo(replyPostId: Int) {
            viewModelScope.launch {
                setUiAsLoading()

                val parentPost = getParentPostById(replyPostId)

                val repliesList = getPostRepliesById(parentPost.id)

                val replyData = ReplyScreenUiData(parentPost, repliesList)

                setUiAsSuccess(replyData)
            }
        }
    }
