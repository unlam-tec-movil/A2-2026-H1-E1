package ar.edu.unlam.mobile.scaffolding.data.repositories.implementation

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.Draft
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.DraftDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces.TuiterApiService
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.sampledata.localPostCreationResponse1
import ar.edu.unlam.mobile.scaffolding.data.repositories.sampledata.localPostCreationResponse2
import ar.edu.unlam.mobile.scaffolding.data.repositories.sampledata.localReply
import ar.edu.unlam.mobile.scaffolding.data.repositories.sampledata.samplePostResponseList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

private const val STARTING_POST_PAGE_INDEX = 1
private const val MAX_PAGE_VALUE = 20

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
            } catch (_: IOException) {
                localPostCreationResponse1
            }

        override suspend fun getPostList(): List<PostResponse> {
            val token = getToken()
            val allPosts = mutableListOf<PostResponse>()
            var page = STARTING_POST_PAGE_INDEX

            while (allPosts.size < MAX_PAGE_VALUE) {
                val posts =
                    try {
                        tuiterApiService.getPosts(
                            userToken = token,
                            pageNumber = page,
                            onlyParents = true,
                        )
                    } catch (exception: HttpException) {
                        throw exception
                    } catch (_: IOException) {
                        return samplePostResponseList
                    }

                if (posts.isEmpty()) break

                allPosts.addAll(posts)
                page++
            }

            return allPosts.take(MAX_PAGE_VALUE)
        }

        override suspend fun getPostById(postId: Int): PostResponse =
            tuiterApiService.getPostById(
                postId = postId,
                userToken = getToken(),
            )

        override suspend fun getRepliesByPostId(postId: Int): List<PostResponse> {
            val apiReplies =
                try {
                    val response =
                        tuiterApiService.getRepliesByPostId(
                            postId = postId,
                            userToken = getToken(),
                        )

                    response.body() ?: emptyList()
                } catch (_: IOException) {
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
                            userToken = getToken(),
                            pageNumber = STARTING_POST_PAGE_INDEX,
                            onlyParents = false,
                        ).filter { it.parentId > 0 }
                        .groupBy { it.parentId }
                        .mapValues { it.value.size }
                } catch (_: IOException) {
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
            } catch (_: IOException) {
                // offline mode - ignore
            }
        }

        override suspend fun unlikePost(
            postId: Int,
            userToken: String,
        ) {
            try {
                tuiterApiService.unlikePost(postId, userToken)
            } catch (_: IOException) {
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
            } catch (_: IOException) {
                saveLocalReply(parentPostId, localReply)
                localPostCreationResponse2
            }

        private suspend fun getToken(): String = tokenManager.tokenFlow.first()
    }
