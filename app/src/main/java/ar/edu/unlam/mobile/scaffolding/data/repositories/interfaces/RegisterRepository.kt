package ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces

import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.register.RegisterRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.register.RegisterResponse

interface RegisterRepository {
    suspend fun register(registerRequest: RegisterRequest): RegisterResponse
}
