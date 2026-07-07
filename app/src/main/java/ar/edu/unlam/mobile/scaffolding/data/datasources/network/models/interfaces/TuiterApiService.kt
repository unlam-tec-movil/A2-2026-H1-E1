package ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces

import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.login.LoginRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.login.LoginResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoUpdateRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.register.RegisterRequest
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.register.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TuiterApiService {
    @POST("api/v1/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): LoginResponse

    @POST("api/v1/users")
    suspend fun register(
        @Body request: RegisterRequest,
    ): RegisterResponse

    @POST("api/v1/me/tuits")
    suspend fun createPost(
        @Body request: PostCreationRequest,
        @Header("Authorization") userToken: String,
    ): PostCreationResponse

    @GET("api/v1/me/feed")
    suspend fun getPosts(
        @Header("Authorization") userToken: String,
        @Query("page") pageNumber: Int,
        @Query("only_parents") onlyParents: Boolean,
    ): List<PostResponse>

    @GET("api/v1/me/profile")
    suspend fun getProfileInfo(
        @Header("Authorization") userToken: String,
    ): ProfileInfoResponse

    @PUT("api/v1/me/profile")
    suspend fun updateProfile(
        @Header("Authorization") userToken: String,
        @Body profileUpdateRequest: ProfileInfoUpdateRequest,
    ): ProfileInfoResponse

    @POST("api/v1/me/tuits/{tuit_id}/likes")
    suspend fun likePost(
        @Path("tuit_id") postId: Int,
        @Header("Authorization") userToken: String,
    )

    @DELETE("api/v1/me/tuits/{id}/likes")
    suspend fun unlikePost(
        @Path("id") postId: Int,
        @Header("Authorization") userToken: String,
    )

    @GET("/api/v1/me/tuits/{tuit_id}/replies")
    @Headers("Application-Token: f6b67b050d16483ee05ce7563a5f8f246a85ea4eec3cde1064a3bc82ddddd921")
    suspend fun getRepliesList(
        @Header("Authorization") userToken: String,
        @Path("tuit_id") tuitId: Int,
    ): Response<List<PostResponse>>

    @GET("/api/v1/me/tuits/{tuit_id}")
    @Headers("Application-Token: f6b67b050d16483ee05ce7563a5f8f246a85ea4eec3cde1064a3bc82ddddd921")
    suspend fun getPostById(
        @Path("tuit_id") postId: Int,
        @Header("Authorization") userToken: String,
    ): PostResponse
}
