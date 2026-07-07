package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse

data class PostUiModel(
    val apiPostResponse: PostResponse,
    val isMarkedAsFavorite: Boolean,
    val repliesCount: Int,
)
