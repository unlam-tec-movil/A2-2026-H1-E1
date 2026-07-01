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
import javax.inject.Inject

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
        ): PostCreationResponse = tuiterApiService.createPost(createPostRequest, "Bearer $userToken")

        override suspend fun getPostList(): List<PostResponse> =
            try {
                tuiterApiService.getPosts(
                    userToken = "Bearer ${tokenManager.tokenFlow.first()}",
                    pageNumber = 1,
                    onlyParents = true,
                )
            } catch (_: Exception) {
                listOf(
                    PostResponse(1, "¡Bienvenido a Tuiter UNLaM!", 0, 1, "Maximo", "", 5, false, "2026-06-30"),
                    PostResponse(2, "Hola mundo! Este es mi primer post", 0, 2, "Alan", "", 3, true, "2026-06-29"),
                    PostResponse(3, "La API está caída pero la app funciona igual", 0, 1, "Maximo", "", 10, false, "2026-06-28"),
                    PostResponse(4, "Esto es un post de respuesta", 1, 3, "Aixa", "", 1, false, "2026-06-27"),
                )
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
            tuiterApiService.likePost(postId, "Bearer $userToken")
        }

        override suspend fun unlikePost(
            postId: Int,
            userToken: String,
        ) {
            tuiterApiService.unlikePost(postId, "Bearer $userToken")
        }
    }
