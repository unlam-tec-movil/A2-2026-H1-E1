package ar.edu.unlam.mobile.scaffolding.ui.screens.post

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.Draft
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces.NetworkObject
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationResponse
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
    ) : BaseViewModel<String>(),
        NetworkObject<PostCreationRequest, PostCreationResponse> {
        @Suppress("ktlint:standard:backing-property-naming")
        private val _draftId: MutableStateFlow<Int> = MutableStateFlow(0)

        private val _message = MutableStateFlow("")
        val message: StateFlow<String> = _message.asStateFlow()

        init {

            viewModelScope.launch {

                checkAndLoadLastDraft()
            }
        }

        fun createPost() {
            viewModelScope.launch {
                setUiAsLoading()

                try {
                    val request = createRequestObject()

                    val response = createReponseObject(request)

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
                _draftId.value = drafts.lastOrNull()?.postId ?: 0
                _message.value = drafts.lastOrNull()?.postMessage ?: ""
            }
        }

        private suspend fun checkAndDeleteDraftIfNeeded() {
            if (_draftId.value > 0) {
                postRepository.deleteDraft(_draftId.value)
            }
        }

        fun createDraft(draftMessage: String) {
            viewModelScope.launch {
                val draft = Draft(postMessage = draftMessage)

                postRepository.saveDraft(draft)

                setUiAsSuccess(DRAFT_SAVED)
            }
        }

        fun onMessageChange(newMessage: String) {
            _message.value = newMessage
        }

        fun restoreStatus() {
            setUiAsIdle()
        }

        override suspend fun createRequestObject(): PostCreationRequest = PostCreationRequest(_message.value)

        override suspend fun createReponseObject(request: PostCreationRequest): PostCreationResponse =
            postRepository.createNewPost(request, tokenManager.tokenFlow.first())
    }
