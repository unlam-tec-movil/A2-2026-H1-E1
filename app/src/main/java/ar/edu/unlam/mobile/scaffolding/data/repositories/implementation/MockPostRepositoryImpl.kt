package ar.edu.unlam.mobile.scaffolding.data.repositories.implementation

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.Draft
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MockPostRepositoryImpl : PostRepository {
    override suspend fun createNewPost(
        createPostRequest: PostCreationRequest,
        userToken: String,
    ): PostCreationResponse =
        PostCreationResponse(
            message = "",
        )

    override suspend fun getPostList(): List<PostResponse> =
        (1..20).map {
            PostResponse(
                id = it,
                message = "Este es un mensaje ficticio",
                parentId = 0,
                authorId = it + 3,
                author = "Autor $it",
                avatarUrl = "https://ui-avatars.com/api/?name=Autor+$it",
                likes = (1..20).random(),
                liked = false,
                date = "2023-04-$it",
            )
        }

    override suspend fun getPostById(postId: Int): PostResponse =
        PostResponse(
            id = postId,
            message = "Este es el post padre ficticio",
            parentId = 0,
            authorId = postId + 3,
            author = "Autor $postId",
            avatarUrl = "https://ui-avatars.com/api/?name=Autor+$postId",
            likes = 5,
            liked = false,
            date = "2023-04-01",
        )

    override suspend fun getRepliesByPostId(postId: Int): List<PostResponse> =
        (1..3).map {
            PostResponse(
                id = postId * 100 + it,
                message = "Respuesta ficticia $it al post $postId",
                parentId = postId,
                authorId = it + 20,
                author = "Usuario respuesta $it",
                avatarUrl = "https://ui-avatars.com/api/?name=Usuario+Respuesta+$it",
                likes = it,
                liked = false,
                date = "2023-04-0$it",
            )
        }

    override suspend fun getRepliesForPost(postId: Int): List<PostResponse> = getRepliesByPostId(postId)

    override suspend fun getRepliesCounts(): Map<Int, Int> = emptyMap()

    override suspend fun saveLocalReply(
        parentPostId: Int,
        reply: PostResponse,
    ) {
        // Mock: no hace nada
    }

    override suspend fun saveDraft(draft: Draft) {
    }

    override suspend fun deleteDraft(draftId: Int) {
    }

    override fun getAllDrafts(): Flow<List<Draft>> = MutableStateFlow(emptyList())

    override suspend fun likePost(
        postId: Int,
        userToken: String,
    ) {
    }

    override suspend fun unlikePost(
        postId: Int,
        userToken: String,
    ) {
    }

    override suspend fun createReply(
        parentPostId: Int,
        createPostRequest: PostCreationRequest,
        userToken: String,
    ): PostCreationResponse =
        PostCreationResponse(
            message = "",
        )
}
