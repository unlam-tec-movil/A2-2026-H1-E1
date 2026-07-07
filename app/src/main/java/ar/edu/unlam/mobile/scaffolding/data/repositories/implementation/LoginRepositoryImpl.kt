package ar.edu.unlam.mobile.scaffolding.data.repositories.implementation

import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces.TuiterApiService
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.login.LoginRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.login.LoginResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.LoginRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.sampledata.localLoginResponse
import javax.inject.Inject

class LoginRepositoryImpl
    @Inject
    constructor(
        private val tuiterApiService: TuiterApiService,
    ) : LoginRepository {
        override suspend fun login(loginRequest: LoginRequest): LoginResponse =
            try {
                tuiterApiService.login(loginRequest)
            } catch (_: Exception) {
                localLoginResponse
            }
    }
