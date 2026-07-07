class LoginRepositoryImpl
    @Inject
    constructor(
        private val tuiterApiService: TuiterApiService,
    ) : LoginRepository {
        override suspend fun login(loginRequest: LoginRequest): LoginResponse {
            return tuiterApiService.login(loginRequest)
        }
    }
    
