package ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post

import com.google.gson.annotations.SerializedName

data class PostResponse(
    val id: Int,
    val message: String,
    @SerializedName("parent_id") val parentId: Int,
    @SerializedName("author_id") val authorId: Int,
    val author: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    val likes: Int,
    val liked: Boolean,
    val date: String,
)
