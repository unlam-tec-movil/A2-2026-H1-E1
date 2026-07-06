package ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post

data class PostCreationRequest(
    val message: String,
    val parentId: Int = 0,
)
