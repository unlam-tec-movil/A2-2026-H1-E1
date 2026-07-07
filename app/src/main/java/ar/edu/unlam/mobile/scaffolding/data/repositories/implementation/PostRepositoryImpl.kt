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

        override suspend fun createNewPost(
            createPostRequest: PostCreationRequest,
            userToken: String,
        ): PostCreationResponse =
            try {
                tuiterApiService.createPost(createPostRequest, userToken)
            } catch (_: Exception) {
                PostCreationResponse("Post creado offline")
            }

        override suspend fun getPostList(): List<PostResponse> {
            val token = tokenManager.tokenFlow.first()
            val allPosts = mutableListOf<PostResponse>()
            var page = 1

            while (allPosts.size < 20) {
                val posts =
                    try {
                        tuiterApiService.getPosts(
                            userToken = token,
                            pageNumber = page,
                            onlyParents = true,
                        )
                    } catch (_: Exception) {
                        return getOfflinePosts()
                    }

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
            val apiReplies =
                try {
                    val response =
                        tuiterApiService.getRepliesByPostId(
                            postId = postId,
                            userToken = tokenManager.tokenFlow.first(),
                        )

                    response.body() ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }

            return apiReplies + (localReplies[postId] ?: emptyList())
        }

        override suspend fun getRepliesForPost(postId: Int): List<PostResponse> = getRepliesByPostId(postId)

        override suspend fun saveLocalReply(
            parentPostId: Int,
            reply: PostResponse,
        ) {
            localReplies.getOrPut(parentPostId) { mutableListOf() }.add(reply)
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
                    emptyMap()
                }

            val localCounts = localReplies.mapValues { it.value.size }

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

        override suspend fun createReply(
            parentPostId: Int,
            createPostRequest: PostCreationRequest,
            userToken: String,
        ): PostCreationResponse =
            try {
                tuiterApiService.createReply(
                    parentPostId = parentPostId,
                    request = createPostRequest,
                    userToken = userToken,
                )
            } catch (_: Exception) {
                val localReply =
                    PostResponse(
                        id = System.currentTimeMillis().toInt(),
                        message = createPostRequest.message,
                        parentId = parentPostId,
                        authorId = 0,
                        author = "Usuario local",
                        avatarUrl = "",
                        likes = 0,
                        liked = false,
                        date = "2026-01-01",
                    )

                saveLocalReply(parentPostId, localReply)
                PostCreationResponse("Respuesta creada offline")
            }

        private fun getOfflinePosts(): List<PostResponse> =
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
