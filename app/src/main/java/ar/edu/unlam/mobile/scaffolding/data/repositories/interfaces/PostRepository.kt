package ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.Draft
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun createNewPost(
        createPostRequest: PostCreationRequest,
        userToken: String,
    ): PostCreationResponse

    suspend fun getPostList(): List<PostResponse>

    suspend fun saveDraft(draft: Draft)

    suspend fun deleteDraft(draftId: Int)

    fun getAllDrafts(): Flow<List<Draft>>

    suspend fun likePost(postId: Int, userToken: String)

    suspend fun unlikePost(postId: Int, userToken: String)
}
