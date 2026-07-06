package ar.edu.unlam.mobile.scaffolding.ui.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@Composable
fun PostList(
    posts: List<PostResponse>,
    favoritePosts: Set<Int> = emptySet(),
    onFavoriteClick: (PostResponse) -> Unit = {},
    onReplyClick: (PostResponse) -> Unit = {},
    onLikeClick: (PostResponse) -> Unit = {},
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
                isSelectedAsFavorite = favoritePosts.contains(post.id),
                onSelectedAsFavoriteAction = {
                    onFavoriteClick(post)
                },
                onReply = {
                    onReplyClick(post)
                },
                onLike = {
                    onLikeClick(post)
                },
            )
        }
    }
}

private val previewPosts =
    listOf(
        PostResponse(
            id = 1,
            author = "Juan Pérez",
            message = "Hoy tuve una excelente atención en la guardia. ¡Muchas gracias al equipo médico!",
            likes = 12,
            liked = true,
            avatarUrl = "",
            parentId = 0,
            authorId = 1,
            date = "2026-07-05",
        ),
        PostResponse(
            id = 2,
            author = "María López",
            message = "¿Alguien sabe cuánto demora la autorización para una resonancia?",
            likes = 8,
            liked = false,
            avatarUrl = "",
            parentId = 0,
            authorId = 2,
            date = "2026-07-04",
        ),
        PostResponse(
            id = 3,
            author = "Carlos Gómez",
            message = "Comparto mi experiencia con la nueva aplicación. La interfaz quedó muy buena y es mucho más rápida.",
            likes = 21,
            liked = true,
            avatarUrl = "",
            parentId = 0,
            authorId = 3,
            date = "2026-07-03",
        ),
        PostResponse(
            id = 4,
            author = "Ana Fernández",
            message = "Excelente iniciativa incorporar un espacio para la comunidad. 👏",
            likes = 4,
            liked = false,
            avatarUrl = "",
            parentId = 0,
            authorId = 4,
            date = "2026-07-02",
        ),
    )

@Preview(showBackground = true)
@Composable
private fun PostListPreview() {
    ScaffoldingV2Theme {
        PostList(
            posts = previewPosts,
            favoritePosts = setOf(1, 3),
        )
    }
}
