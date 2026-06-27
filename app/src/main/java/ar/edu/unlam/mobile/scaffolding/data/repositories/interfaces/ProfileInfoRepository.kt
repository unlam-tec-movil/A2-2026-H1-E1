package ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces

import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoUpdateRequest

interface ProfileInfoRepository {
    suspend fun getCurrentProfileInfo(profileInfoRequest: ProfileInfoRequest): ProfileInfoResponse

    suspend fun updateProfileInfo(
        token: String,
        profileInfoUpdateRequest: ProfileInfoUpdateRequest,
    ): ProfileInfoResponse
}
