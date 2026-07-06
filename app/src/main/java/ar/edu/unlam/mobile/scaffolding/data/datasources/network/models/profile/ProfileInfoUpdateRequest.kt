package ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile

import com.google.gson.annotations.SerializedName

data class ProfileInfoUpdateRequest(
    val name: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    val password: String,
)
