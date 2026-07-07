package ar.edu.unlam.mobile.scaffolding.data.repositories.implementation

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.Draft
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.DraftDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces.TuiterApiService
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class PostRepositoryImpl
    @Inject
    constructor(
        private val tuiterApiService: TuiterApiService,
        private val tokenManager: TokenManager,
        private val draftDao: DraftDao,
    ) : PostRepository {
        private val localReplies = ConcurrentHashMap<Int, MutableList<PostResponse>>()

        override suspend fun saveLocalReply(
            parentPostId: Int,
            reply: PostResponse,
        ) {
            localReplies.getOrPut(parentPostId) { mutableListOf() }.add(reply)
        }

        override suspend fun createNewPost(
            createPostRequest: PostCreationRequest,
            userToken: String,
        ): PostCreationResponse =
            try {
                tuiterApiService.createPost(createPostRequest, userToken)
            } catch (_: Exception) {
                PostCreationResponse("Post creado offline")
            }

        override suspend fun getPostList(): List<PostResponse> =
            try {
                tuiterApiService.getPosts(
                    userToken = tokenManager.tokenFlow.first(),
                    pageNumber = 1,
                    onlyParents = true,
                )
            } catch (_: Exception) {
                listOf(
                    PostResponse(
                        id = 1,
                        message = "Este es un post de prueba offline",
                        parentId = 0,
                        authorId = 1,
                        author = "Usuario Offline",
                        avatarUrl = "",
                        likes = 42,
                        liked = false,
                        date = "2024-01-01T00:00:00Z",
                    ),
                    PostResponse(
                        id = 2,
                        message = "La API está caída, pero seguimos posteando",
                        parentId = 0,
                        authorId = 2,
                        author = "Tuiter Offline",
                        avatarUrl = "",
                        likes = 17,
                        liked = true,
                        date = "2024-01-02T00:00:00Z",
                    ),
                )
            }

        override suspend fun getRepliesForPost(postId: Int): List<PostResponse> {
            val apiReplies =
                try {
                    tuiterApiService
                        .getPosts(
                            userToken = tokenManager.tokenFlow.first(),
                            pageNumber = 1,
                            onlyParents = false,
                        ).filter { it.parentId == postId }
                } catch (_: Exception) {
                    emptyList()
                }
            return apiReplies + (localReplies[postId] ?: emptyList())
        }

        override suspend fun getRepliesCounts(): Map<Int, Int> {
            val apiCounts =
                try {
                    tuiterApiService
                        .getPosts(
                            userToken = tokenManager.tokenFlow.first(),
                            pageNumber = 1,
                            onlyParents = false,
                        ).filter { it.parentId > 0 }
                        .groupBy { it.parentId }
                        .mapValues { it.value.size }
                } catch (_: Exception) {
                    emptyMap<Int, Int>()
                }
            val localCounts =
                localReplies.mapValues { it.value.size }
            return (apiCounts.keys + localCounts.keys).associateWith {
                (apiCounts[it] ?: 0) + (localCounts[it] ?: 0)
            }
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
            try {
                tuiterApiService.likePost(postId, userToken)
            } catch (_: Exception) {
                // offline mode - ignore
            }
        }

        override suspend fun unlikePost(
            postId: Int,
            userToken: String,
        ) {
            try {
                tuiterApiService.unlikePost(postId, userToken)
            } catch (_: Exception) {
                // offline mode - ignore
            }
        }

        override suspend fun getPostReplies(
            postId: Int,
            userToken: String,
        ): List<PostResponse> = tuiterApiService.getRepliesList(userToken, postId).body() ?: emptyList()

        override suspend fun getPostById(
            postId: Int,
            userToken: String,
        ): PostResponse = tuiterApiService.getPostById(postId, userToken)
    }
