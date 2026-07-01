package ar.edu.unlam.mobile.scaffolding.ui.screens.post

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.Draft
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationRequest
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.DRAFT_SAVED
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.UNKNOWN_ERROR_MESSAGE
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostCreationViewModel
    @Inject
    constructor(
        private val postRepository: PostRepository,
        private val tokenManager: TokenManager,
    ) : BaseViewModel<String>() {
        private var parentId: Int = 0

        private val _message = MutableStateFlow("")
        val message: StateFlow<String> = _message.asStateFlow()

        init {
            viewModelScope.launch {
                checkAndLoadLastDraft()
            }
        }

        fun setParentId(id: Int) {
            parentId = id
        }

        fun createPost() {
            viewModelScope.launch {
                setUiAsLoading()

                try {
                    val token = tokenManager.tokenFlow.first()
                    val request = PostCreationRequest(_message.value, parentId)
                    val response = postRepository.createNewPost(request, token)
                    setUiAsSuccess(response.message)
                    checkAndDeleteDraftIfNeeded()
                } catch (exception: Exception) {
                    val responseErrorMessage = exception.message ?: UNKNOWN_ERROR_MESSAGE
                    setUiAsError(responseErrorMessage)
                }
            }
        }

        private suspend fun checkAndLoadLastDraft() {
            postRepository.getAllDrafts().collect { drafts ->
                _message.value = drafts.lastOrNull()?.postMessage ?: ""
            }
        }

        private suspend fun checkAndDeleteDraftIfNeeded() {
            postRepository.getAllDrafts().collect { drafts ->
                val lastId = drafts.lastOrNull()?.postId ?: 0
                if (lastId > 0) postRepository.deleteDraft(lastId)
                return@collect
            }
        }

        fun createDraft(draftMessage: String) {
            viewModelScope.launch {
                postRepository.saveDraft(Draft(postMessage = draftMessage))
                setUiAsSuccess(DRAFT_SAVED)
            }
        }

        fun onMessageChange(newMessage: String) {
            _message.value = newMessage
        }

        fun restoreStatus() {
            setUiAsIdle()
        }
    }
