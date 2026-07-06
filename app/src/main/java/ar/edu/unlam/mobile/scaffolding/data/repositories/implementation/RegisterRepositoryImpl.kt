package ar.edu.unlam.mobile.scaffolding.data.repositories.implementation

import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces.TuiterApiService
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.register.RegisterRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.register.RegisterResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.RegisterRepository
import javax.inject.Inject

class RegisterRepositoryImpl
    @Inject
    constructor(
        private val tuiterApiService: TuiterApiService,
    ) : RegisterRepository {
        override suspend fun register(registerRequest: RegisterRequest): RegisterResponse =
            tuiterApiService.register(registerRequest)
    }
