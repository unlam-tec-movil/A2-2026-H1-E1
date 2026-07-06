package ar.edu.unlam.mobile.scaffolding.ui.screens.reply

import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse

data class ReplyScreenUiData(
    val parentPost: PostResponse,
    val postReplies: List<PostResponse>?,
)
