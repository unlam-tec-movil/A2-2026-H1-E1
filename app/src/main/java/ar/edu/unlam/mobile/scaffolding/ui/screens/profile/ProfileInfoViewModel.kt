package ar.edu.unlam.mobile.scaffolding.ui.screens.profile

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces.NetworkObject
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoUpdateRequest
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.ProfileInfoRepository
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.BLANK_EMAIL
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.BLANK_PASSWORDS
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.PASSWORDS_DOES_NOT_MATCH
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.UNKNOWN_ERROR_MESSAGE
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileInfoViewModel
    @Inject
    constructor(
        private val profileInfoRepository: ProfileInfoRepository,
        private val tokenManager: TokenManager,
    ) : BaseViewModel<Unit>(),
        NetworkObject<ProfileInfoRequest, ProfileInfoResponse> {
        @Suppress("ktlint:standard:backing-property-naming")
        private val _isSaving = MutableStateFlow(false)
        val isSaving = _isSaving.asStateFlow()

        private val _name = MutableStateFlow("")
        val name = _name.asStateFlow()

        private val _avatarUrl = MutableStateFlow("")
        val avatarUrl = _avatarUrl.asStateFlow()

        private val _email = MutableStateFlow("")
        val email = _email.asStateFlow()

        private val _newPassword = MutableStateFlow("")
        val newPassword = _newPassword.asStateFlow()

        private val _newPasswordConfirm = MutableStateFlow("")
        val newPasswordConfirm = _newPasswordConfirm.asStateFlow()

        init {

            getProfileInfo()
        }

        fun getProfileInfo() {
            viewModelScope.launch {
                setUiAsLoading()

                try {
                    val profileRequest = createRequestObject()

                    val profileResponse = createReponseObject(profileRequest)

                    setUiAsSuccess(Unit)

                    updateProfileInfoWithResponseData(profileResponse)
                } catch (exception: Exception) {
                    val responseErrorMessage = exception.message ?: UNKNOWN_ERROR_MESSAGE

                    setUiAsError(responseErrorMessage)
                }
            }
        }

        fun onNameChange(newName: String) {
            _name.value = newName
        }

        fun onEmailChange(newEmail: String) {
            _email.value = newEmail
        }

        fun onAvatarURLChange(newUrl: String) {
            _avatarUrl.value = newUrl
        }

        fun onNewPasswordChange(newPassword: String) {
            _newPassword.value = newPassword
        }

        fun onNewPasswordConfirmChange(newPasswordConfirm: String) {
            _newPasswordConfirm.value = newPasswordConfirm
        }

        fun sendProfileInfoUpdate() {
            if (emailIsValid() && passwordsAreValid()) {
                viewModelScope.launch {
                    _isSaving.value = true

                    try {
                        val updateRequest = createUpdateRequestObject()

                        profileInfoRepository.updateProfileInfo(
                            tokenManager.tokenFlow.first(),
                            updateRequest,
                        )

                        setUiAsSuccess(Unit)

                        resetPaswordFields()
                    } catch (exception: Exception) {
                        val responseErrorMessage = exception.message ?: UNKNOWN_ERROR_MESSAGE

                        setUiAsError(responseErrorMessage)
                    } finally {
                        _isSaving.value = false
                    }
                }
            }
        }

        private fun updateProfileInfoWithResponseData(profileInfoResponse: ProfileInfoResponse) {
            _name.value = profileInfoResponse.name
            _avatarUrl.value = profileInfoResponse.avatarUrl
            _email.value = profileInfoResponse.email
        }

        private fun emailIsValid(): Boolean {
            if (_email.value.isBlank()) {
                setUiAsError(BLANK_EMAIL)

                return false
            }

            return true
        }

        private fun passwordsAreValid(): Boolean {
            if (_newPassword.value.isBlank() && _newPasswordConfirm.value.isBlank()) {
                setUiAsSuccess(Unit)

                return true
            }

            if (_newPassword.value.isBlank() || _newPasswordConfirm.value.isBlank()) {
                setUiAsError(BLANK_PASSWORDS)

                return false
            }

            if (_newPassword.value != _newPasswordConfirm.value) {
                setUiAsError(PASSWORDS_DOES_NOT_MATCH)

                return false
            }

            return true
        }

        private fun resetPaswordFields() {
            _newPassword.value = ""
            _newPasswordConfirm.value = ""
        }

        private fun createUpdateRequestObject(): ProfileInfoUpdateRequest =
            ProfileInfoUpdateRequest(_name.value, _avatarUrl.value, _newPassword.value)

        override suspend fun createRequestObject(): ProfileInfoRequest {
            val userToken = tokenManager.tokenFlow.first()

            return ProfileInfoRequest(userToken)
        }

        override suspend fun createReponseObject(request: ProfileInfoRequest): ProfileInfoResponse =
            profileInfoRepository.getCurrentProfileInfo(request)
    }
