package ar.edu.unlam.mobile.scaffolding.data.repositories.implementation

import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.login.LoginRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.login.LoginResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.LoginRepository

class MockLoginRepositoryImpl : LoginRepository {
    override suspend fun login(loginRequest: LoginRequest): LoginResponse = LoginResponse("token12345")
}
