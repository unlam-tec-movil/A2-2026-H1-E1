package ar.edu.unlam.mobile.scaffolding.data.repositories.implementation

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.Draft
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.DraftDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces.TuiterApiService
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.Response

class PostRepositoryImpl
    @Inject
    constructor(
        private val tuiterApiService: TuiterApiService,
        private val tokenManager: TokenManager,
        private val draftDao: DraftDao,
    ) : PostRepository {
        override suspend fun createNewPost(
            createPostRequest: PostCreationRequest,
            userToken: String,
        ): PostCreationResponse = tuiterApiService.createPost(createPostRequest, userToken)

        override suspend fun getPostList(): List<PostResponse> {
            val token = tokenManager.tokenFlow.first()
            val allPosts = mutableListOf<PostResponse>()
            var page = 1

            while (allPosts.size < 20) {
                val posts =
                    tuiterApiService.getPosts(
                        userToken = token,
                        pageNumber = page,
                        onlyParents = true,
                    )

                if (posts.isEmpty()) {
                    break
                }

                allPosts.addAll(posts)
                page++
            }

            return allPosts.take(20)
        }

        override suspend fun getPostById(postId: Int): PostResponse =
            tuiterApiService.getPostById(
                postId = postId,
                userToken = tokenManager.tokenFlow.first(),
            )

        override suspend fun getRepliesByPostId(postId: Int): List<PostResponse> {
            val response =
                tuiterApiService.getRepliesByPostId(
                    postId = postId,
                    userToken = tokenManager.tokenFlow.first(),
                )

            return response.body() ?: emptyList()
        }

        override suspend fun saveDraft(draft: Draft) {
            draftDao.insert(draft)
        }

        override suspend fun deleteDraft(draftId: Int) {
            draftDao.deleteDraft(draftId)
        }

        override fun getAllDrafts(): Flow<List<Draft>> = draftDao.getAllDrafts()

        override suspend fun likePost(
            postId: Int,
            userToken: String,
        ) {
            tuiterApiService.likePost(postId, userToken)
        }

        override suspend fun unlikePost(
            postId: Int,
            userToken: String,
        ) {
            tuiterApiService.unlikePost(postId, userToken)
        }

        override suspend fun createReply(
            parentPostId: Int,
            createPostRequest: PostCreationRequest,
            userToken: String,
        ): PostCreationResponse =
            tuiterApiService.createReply(
                parentPostId = parentPostId,
                request = createPostRequest,
                userToken = userToken,
            )
    }
