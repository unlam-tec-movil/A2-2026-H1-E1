package ar.edu.unlam.mobile.scaffolding.ui.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM

@Composable
fun PostList(
    posts: List<PostResponse>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(PADDING_MEDIUM),
        contentPadding = PaddingValues(PADDING_MEDIUM),
    ) {
        items(
            items = posts,
            key = { it.id },
        ) { post ->
            PostCard(
                post = post,
                isSelectedAsFavorite = false,
                onSelectedAsFavoriteAction = {},
            )
        }
    }
}

// Mock

val mockPost =
    PostResponse(
        id = 1,
        message = "¡Hola a todos! Este es un post de prueba para verificar cómo se renderiza la tarjeta en Jetpack Compose.",
        parentId = 0,
        authorId = 101,
        author = "Juan Pérez",
        avatarUrl = "",
        likes = 42,
        liked = true,
        date = "2026-06-30T19:30:00Z",
    )

@Preview(showBackground = true)
@Composable
fun PostListPostCardPreview() {
    MaterialTheme {
        PostCard(
            post = mockPost,
            isSelectedAsFavorite = false,
            onSelectedAsFavoriteAction = {},
        )
    }
}

private val mockPosts =
    listOf(
        PostResponse(
            id = 1,
            message = "¡Hola! Este es el primer post de prueba.",
            parentId = 0,
            authorId = 101,
            author = "Juan Pérez",
            avatarUrl = "",
            likes = 42,
            liked = true,
            date = "2026-06-30",
        ),
        PostResponse(
            id = 2,
            message = "Jetpack Compose hace muy sencillo crear interfaces modernas.",
            parentId = 0,
            authorId = 102,
            author = "María Gómez",
            avatarUrl = "",
            likes = 18,
            liked = false,
            date = "2026-06-30",
        ),
        PostResponse(
            id = 3,
            message = "Este es un tercer post para comprobar el comportamiento del LazyColumn.",
            parentId = 0,
            authorId = 103,
            author = "Carlos López",
            avatarUrl = "",
            likes = 7,
            liked = false,
            date = "2026-06-30",
        ),
    )

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PostListPreview() {
    MaterialTheme {
        PostList(posts = mockPosts)
    }
}
