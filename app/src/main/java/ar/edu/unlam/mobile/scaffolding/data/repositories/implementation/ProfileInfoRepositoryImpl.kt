package ar.edu.unlam.mobile.scaffolding.data.repositories.implementation

import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces.TuiterApiService
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoUpdateRequest
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.ProfileInfoRepository
import javax.inject.Inject

class ProfileInfoRepositoryImpl
    @Inject
    constructor(
        private val tuiterApiService: TuiterApiService,
    ) : ProfileInfoRepository {
        override suspend fun getCurrentProfileInfo(profileInfoRequest: ProfileInfoRequest): ProfileInfoResponse =
            tuiterApiService.getProfileInfo("Bearer ${profileInfoRequest.token}")

        override suspend fun updateProfileInfo(
            token: String,
            profileInfoUpdateRequest: ProfileInfoUpdateRequest,
        ): ProfileInfoResponse = tuiterApiService.updateProfile("Bearer $token", profileInfoUpdateRequest)
    }
