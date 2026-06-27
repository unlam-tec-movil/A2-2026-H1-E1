package ar.edu.unlam.mobile.scaffolding.ui.screens.login

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces.NetworkObject
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.login.LoginRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.login.LoginResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.LoginRepository
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.UNKNOWN_ERROR_MESSAGE
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val loginRepository: LoginRepository,
        private val tokenManager: TokenManager,
    ) : BaseViewModel<String>(),
        NetworkObject<LoginRequest, LoginResponse> {
        private val _email = MutableStateFlow("")
        val email: StateFlow<String> = _email.asStateFlow()

        private val _password = MutableStateFlow("")
        val password: StateFlow<String> = _password.asStateFlow()

        fun login() {
            viewModelScope.launch {
                setUiAsLoading()

                try {
                    val request = createRequestObject()

                    val response = createReponseObject(request)

                    setUiAsSuccess(response.token)

                    tokenManager.saveToken(response.token)
                } catch (exception: Exception) {
                    val responseErrorMessage = exception.message ?: UNKNOWN_ERROR_MESSAGE

                    setUiAsError(responseErrorMessage)
                }
            }
        }

        fun restoreStatus() {
            setUiAsIdle()
        }

        fun updateEmailState(newEmailState: String) {
            _email.value = newEmailState
        }

        fun updatePasswordState(newPasswordState: String) {
            _password.value = newPasswordState
        }

        override suspend fun createRequestObject(): LoginRequest = LoginRequest(_email.value, _password.value)

        override suspend fun createReponseObject(request: LoginRequest): LoginResponse = loginRepository.login(request)
    }
