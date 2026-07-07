package ar.edu.unlam.mobile.scaffolding.ui.screens.post

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.Draft
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PostCreationViewModel
    @Inject
    constructor(
        private val postRepository: PostRepository,
        private val tokenManager: TokenManager,
    ) : BaseViewModel<String>() {
        private var parentId: Int = 0
        private var selectedDraftId: Int? = null

        private val _message = MutableStateFlow("")
        val message: StateFlow<String> = _message.asStateFlow()

        private val _drafts = MutableStateFlow<List<Draft>>(emptyList())
        val drafts: StateFlow<List<Draft>> = _drafts.asStateFlow()

        init {
            loadDrafts()
        }

        fun setParentId(id: Int) {
            parentId = id
        }

        fun loadDrafts() {
            viewModelScope.launch {
                postRepository.getAllDrafts().collect { savedDrafts ->
                    _drafts.value = savedDrafts
                }
            }
        }

        fun selectDraft(draft: Draft) {
            selectedDraftId = draft.postId
            _message.value = draft.postMessage
        }

        fun createPost() {
            viewModelScope.launch {
                setUiAsLoading()

                try {
                    val token = tokenManager.tokenFlow.first()
                    val request = PostCreationRequest(_message.value, parentId)

                    val response =
                        if (parentId != 0) {
                            postRepository.createReply(
                                parentPostId = parentId,
                                createPostRequest = request,
                                userToken = token,
                            )
                        } else {
                            postRepository.createNewPost(request, token)
                        }

                    if (parentId > 0) {
                        val localReply =
                            PostResponse(
                                id = 0,
                                message = _message.value,
                                parentId = parentId,
                                authorId = 0,
                                author = "Tú",
                                avatarUrl = "",
                                likes = 0,
                                liked = false,
                                date =
                                    SimpleDateFormat(
                                        "yyyy-MM-dd'T'HH:mm:ss'Z'",
                                        Locale.getDefault(),
                                    ).format(Date()),
                            )

                        postRepository.saveLocalReply(parentId, localReply)
                    }

                    selectedDraftId?.let { draftId ->
                        postRepository.deleteDraft(draftId)
                    }

                    setUiAsSuccess(response.message)
                } catch (exception: Exception) {
                    setUiAsError(exception.message ?: UNKNOWN_ERROR_MESSAGE)
                }
            }
        }

        fun createDraft(draftMessage: String) {
            viewModelScope.launch {
                if (draftMessage.isNotBlank()) {
                    postRepository.saveDraft(Draft(postMessage = draftMessage))
                    _message.value = ""
                    selectedDraftId = null
                    setUiAsSuccess(DRAFT_SAVED)
                }
            }
        }

        fun onMessageChange(newMessage: String) {
            _message.value = newMessage
        }

        fun restoreStatus() {
            setUiAsIdle()
        }
    }
