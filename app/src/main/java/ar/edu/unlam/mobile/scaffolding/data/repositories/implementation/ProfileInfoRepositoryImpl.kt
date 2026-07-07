package ar.edu.unlam.mobile.scaffolding.data.repositories.implementation

import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces.TuiterApiService
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoUpdateRequest
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.ProfileInfoRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.sampledata.localProfileInfoResponse
import javax.inject.Inject

class ProfileInfoRepositoryImpl
    @Inject
    constructor(
        private val tuiterApiService: TuiterApiService,
    ) : ProfileInfoRepository {
        override suspend fun getCurrentProfileInfo(profileInfoRequest: ProfileInfoRequest): ProfileInfoResponse =
            try {
                tuiterApiService.getProfileInfo(profileInfoRequest.token)
            } catch (_: Exception) {
                localProfileInfoResponse
            }

        override suspend fun updateProfileInfo(
            token: String,
            profileInfoUpdateRequest: ProfileInfoUpdateRequest,
        ): ProfileInfoResponse =
            try {
                tuiterApiService.updateProfile(token, profileInfoUpdateRequest)
            } catch (_: Exception) {
                localProfileInfoResponse
            }
    }
