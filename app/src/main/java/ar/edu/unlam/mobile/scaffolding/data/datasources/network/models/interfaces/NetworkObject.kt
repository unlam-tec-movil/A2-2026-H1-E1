package ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces

interface NetworkObject<Request, Response> {
    suspend fun createRequestObject(): Request

    suspend fun createReponseObject(request: Request): Response
}
