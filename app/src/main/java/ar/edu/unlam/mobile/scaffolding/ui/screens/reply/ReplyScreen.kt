package ar.edu.unlam.mobile.scaffolding.ui.screens.reply

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.PostCreationScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.PostCreationViewModel

@Composable
fun ReplyScreen(
    parentPostId: Int,
    postCreationViewModel: PostCreationViewModel,
    onPostAction: () -> Unit,
    onCancelAction: () -> Unit,
    onShowSnackbar: (String) -> Unit,
) {
    LaunchedEffect(parentPostId) {
        postCreationViewModel.setParentId(parentPostId)
    }

    PostCreationScreen(
        postCreationViewModel = postCreationViewModel,
        onPostAction = onPostAction,
        onCancelAction = onCancelAction,
        onShowSnackbar = onShowSnackbar,
    )
}
