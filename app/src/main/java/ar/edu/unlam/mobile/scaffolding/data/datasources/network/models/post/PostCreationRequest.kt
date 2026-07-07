package ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post

import com.google.gson.annotations.SerializedName

data class PostCreationRequest(
    val message: String,
    @SerializedName("parent_id") val parentId: Int = 0,
)
