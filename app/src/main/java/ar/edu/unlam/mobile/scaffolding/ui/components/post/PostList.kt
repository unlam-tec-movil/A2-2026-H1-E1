package ar.edu.unlam.mobile.scaffolding.ui.components.post

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.screens.feed.PostUiModel

@Composable
fun PostList(
    posts: List<PostUiModel>,
    modifier: Modifier = Modifier,
    onReply: (Int) -> Unit = {},
    onLike: (PostResponse) -> Unit = {},
    onAddFavorite: (PostResponse) -> Unit = {},
) {
    LazyColumn(modifier = modifier) {
        items(posts) { post ->
            PostCard(
                post = post,
                onReply = { onReply(post.id) },
                onLike = { onLike(post) },
                onAddFavorite = { onAddFavorite(post) },
            )
        }
    }
}
