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

    override suspend fun getPostList(): List<PostResponse> {
        val postList =
            (1..20).map {
                PostResponse(
                    id = it,
                    message = "Este es un mensaje ficticio",
                    parentId = it * 5,
                    authorId = it + 3,
                    author = "Autor $it",
                    avatarUrl = "https://example.com/avatar_$it.jpg",
                    likes = (1..20).random(),
                    liked = false,
                    date = "2023-04-0$it",
                )
            }
        return postList
    }

    override suspend fun getRepliesForPost(postId: Int): List<PostResponse> = emptyList()

    override suspend fun getRepliesCounts(): Map<Int, Int> = emptyMap()

    override suspend fun saveLocalReply(
        parentPostId: Int,
        reply: PostResponse,
    ) {
    }

    override suspend fun saveDraft(draft: Draft) {
    }

    override suspend fun deleteDraft(draftId: Int) {
    }

    override fun getAllDrafts(): Flow<List<Draft>> {
        val emptyList: List<Draft> = listOf()

        return MutableStateFlow(emptyList)
    }

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
}
