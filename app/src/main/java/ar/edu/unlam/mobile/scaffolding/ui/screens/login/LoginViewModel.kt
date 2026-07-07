package ar.edu.unlam.mobile.scaffolding.ui.screens.login

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.login.LoginRequest
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.LoginRepository
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

private const val EMPTY_FIELDS_ERROR = "Ingresá tu email y contraseña."
private const val INVALID_CREDENTIALS_ERROR = "Email o contraseña incorrectos."
private const val SERVER_ERROR = "No pudimos iniciar sesión. Intentá nuevamente."

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val loginRepository: LoginRepository,
        private val tokenManager: TokenManager,
    ) : BaseViewModel<String>() {
        private val _email = MutableStateFlow("")
        val email: StateFlow<String> = _email.asStateFlow()

        private val _password = MutableStateFlow("")
        val password: StateFlow<String> = _password.asStateFlow()

        fun login() {
            viewModelScope.launch {
                if (_email.value.isBlank() || _password.value.isBlank()) {
                    setUiAsError(EMPTY_FIELDS_ERROR)
                    return@launch
                }

                setUiAsLoading()

                try {
                    val request =
                        LoginRequest(
                            email = _email.value.trim(),
                            password = _password.value,
                        )

                    val response = loginRepository.login(request)

                    tokenManager.saveToken(response.token)
                    setUiAsSuccess(response.token)
                } catch (exception: HttpException) {
                    val message =
                        when (exception.code()) {
                            400, 401, 403, 404 -> INVALID_CREDENTIALS_ERROR
                            else -> SERVER_ERROR
                        }

                    setUiAsError(message)
                } catch (_: Exception) {
                    setUiAsError(SERVER_ERROR)
                }
            }
        }

        fun restoreStatus() {
            setUiAsIdle()
        }

        fun updateEmailState(newEmailState: String) {
            _email.value = newEmailState
            restoreStatus()
        }

        fun updatePasswordState(newPasswordState: String) {
            _password.value = newPasswordState
            restoreStatus()
        }
    }
