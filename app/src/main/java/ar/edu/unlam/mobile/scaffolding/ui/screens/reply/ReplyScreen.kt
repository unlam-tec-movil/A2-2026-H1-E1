package ar.edu.unlam.mobile.scaffolding.ui.screens.reply

import androidx.compose.runtime.Composable
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.PostCreationScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.PostCreationViewModel

@Composable
fun ReplyScreen(
    parentPost: PostResponse,
    postCreationViewModel: PostCreationViewModel,
    onPostAction: () -> Unit,
    onCancelAction: () -> Unit,
    onShowSnackbar: (String) -> Unit,
) {
    PostCreationScreen(
        postCreationViewModel = postCreationViewModel,
        onPostAction = onPostAction,
        onCancelAction = onCancelAction,
        onShowSnackbar = onShowSnackbar,
    )
}
