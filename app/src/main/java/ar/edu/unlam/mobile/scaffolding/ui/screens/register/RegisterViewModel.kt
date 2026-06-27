package ar.edu.unlam.mobile.scaffolding.ui.screens.register

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces.NetworkObject
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.register.RegisterRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.register.RegisterResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.RegisterRepository
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.BLANK_FIELDS_ERROR_MESSAGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.UNKNOWN_ERROR_MESSAGE
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
    @Inject
    constructor(
        private val registerRepository: RegisterRepository,
    ) : BaseViewModel<String>(),
        NetworkObject<RegisterRequest, RegisterResponse> {
        private val _name = MutableStateFlow("")
        val name: StateFlow<String> = _name.asStateFlow()

        private val _email = MutableStateFlow("")
        val email: StateFlow<String> = _email.asStateFlow()

        private val _password = MutableStateFlow("")
        val password: StateFlow<String> = _password.asStateFlow()

        fun register() {
            viewModelScope.launch {
                if (name.value.isBlank() || email.value.isBlank() || password.value.isBlank()) {
                    setUiAsError(BLANK_FIELDS_ERROR_MESSAGE)

                    return@launch
                }

                setUiAsLoading()

                try {
                    val request = createRequestObject()

                    val response = createReponseObject(request)

                    setUiAsSuccess(response.token)
                } catch (exception: Exception) {
                    val responseMessage = exception.message ?: UNKNOWN_ERROR_MESSAGE

                    setUiAsError(responseMessage)
                }
            }
        }

        fun updateName(newName: String) {
            _name.value = newName
        }

        fun updateEmail(newEmail: String) {
            _email.value = newEmail
        }

        fun updatePassword(newPassword: String) {
            _password.value = newPassword
        }

        fun resetForm() {
            _name.value = ""
            _email.value = ""
            _password.value = ""
        }

        fun setUiStateAsIdle() {
            setUiStateAsIdle()
        }

        override suspend fun createRequestObject(): RegisterRequest = RegisterRequest(_name.value, _email.value, _password.value)

        override suspend fun createReponseObject(request: RegisterRequest): RegisterResponse = registerRepository.register(request)
    }
