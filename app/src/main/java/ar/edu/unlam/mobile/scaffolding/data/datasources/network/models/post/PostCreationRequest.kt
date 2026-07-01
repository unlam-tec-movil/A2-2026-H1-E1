package ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post

data class PostCreationRequest(
    val message: String,
    val parent_id: Int = 0,
)
