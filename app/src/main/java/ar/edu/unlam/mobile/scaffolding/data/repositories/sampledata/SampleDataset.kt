package ar.edu.unlam.mobile.scaffolding.data.repositories.sampledata

import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.login.LoginResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostCreationResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.profile.ProfileInfoResponse

val samplePostResponseList =
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

val localReply =
    PostResponse(
        id = System.currentTimeMillis().toInt(),
        message = "Hello World, I am learning Kotlin!",
        parentId = 1,
        authorId = 0,
        author = "Usuario local",
        avatarUrl = "",
        likes = 0,
        liked = false,
        date = "2026-01-01",
    )

val localPostCreationResponse1 = PostCreationResponse("Post creado offline")
val localPostCreationResponse2 = PostCreationResponse("Respuesta creada offline")
val localLoginResponse = LoginResponse("mock-token-para-offline")
val localProfileInfoResponse =
    ProfileInfoResponse(
        name = "Usuario Offline",
        avatarUrl = "",
        email = "offline@tuiter.com",
    )
