package ar.edu.unlam.mobile.scaffolding.ui.screens.reply

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.AVATAR_SIZE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_SMALL
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.DRAFT_SAVED
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.PostCreationViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

private const val REPLY_TITLE = "Responder"
private const val REPLY_BUTTON_TEXT = "Responder"
private const val REPLY_PLACEHOLDER = "Publica tu respuesta"
private const val DRAFT_CREATED_SNACKBAR_TEXT = "¡Tu borrador ha sido creado!"
private const val REPLY_CREATED_SNACKBAR_TEXT = "¡Tu respuesta ha sido publicada!"

@Composable
fun ReplyScreen(
    parentPost: PostResponse,
    postCreationViewModel: PostCreationViewModel,
    onPostAction: () -> Unit,
    onCancelAction: () -> Unit,
    onShowSnackbar: (String) -> Unit,
) {
    val message by postCreationViewModel.message.collectAsState()
    val uiState by postCreationViewModel.uiState.collectAsState()
    val restoreState = { postCreationViewModel.restoreStatus() }

    LaunchedEffect(parentPost.id) {
        postCreationViewModel.setParentId(parentPost.id)
    }

    when (val state = uiState) {
        is UiState.Idle -> {
            ReplyContent(
                parentPost = parentPost,
                message = message,
                onMessageChange = { postCreationViewModel.onMessageChange(it) },
                onReplyAction = { postCreationViewModel.createPost() },
                onCancelAction = onCancelAction,
            )
        }

        is UiState.Loading -> {
            ShowLoadingStatusOnScreen()
        }

        is UiState.Success -> {
            if (state.data == DRAFT_SAVED) {
                onShowSnackbar(DRAFT_CREATED_SNACKBAR_TEXT)
                restoreState()
                onCancelAction()
            } else {
                onShowSnackbar(REPLY_CREATED_SNACKBAR_TEXT)
                restoreState()
                onPostAction()
            }
        }

        is UiState.Error -> {
            ShowErrorMessageOnScreen(
                onRestoreStateAction = restoreState,
                errorMessage = state.error,
            )
        }
    }
}

@Composable
private fun ReplyContent(
    parentPost: PostResponse,
    message: String,
    onMessageChange: (String) -> Unit,
    onReplyAction: () -> Unit,
    onCancelAction: () -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .padding(PADDING_MEDIUM)
                .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(PADDING_LARGE),
            verticalArrangement = Arrangement.spacedBy(PADDING_MEDIUM),
        ) {
            ReplyHeader(
                message = message,
                onReplyAction = onReplyAction,
                onCancelAction = onCancelAction,
            )

            ParentPostPreview(parentPost)

            Text(
                text = "Respondiendo a ${generateUserHandle(parentPost.author)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                placeholder = { Text(REPLY_PLACEHOLDER) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 180.dp),
                shape = RoundedCornerShape(24.dp),
            )
        }
    }
}

@Composable
private fun ReplyHeader(
    message: String,
    onReplyAction: () -> Unit,
    onCancelAction: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onCancelAction) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancelar respuesta",
            )
        }

        Text(
            text = REPLY_TITLE,
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onReplyAction,
            enabled = message.isNotBlank(),
        ) {
            Text(REPLY_BUTTON_TEXT)
        }
    }
}

@Composable
private fun ParentPostPreview(parentPost: PostResponse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        ReplyAvatar(
            avatarUrl = parentPost.avatarUrl,
            author = parentPost.author,
        )

        Spacer(modifier = Modifier.width(PADDING_MEDIUM))

        Column(
            verticalArrangement = Arrangement.spacedBy(PADDING_SMALL),
        ) {
            Row {
                Text(
                    text = parentPost.author,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.width(PADDING_SMALL))

                Text(
                    text = generateUserHandle(parentPost.author),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = parentPost.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ReplyAvatar(
    avatarUrl: String,
    author: String,
) {
    val isValidAvatarUrl =
        avatarUrl.startsWith("http") || avatarUrl.startsWith("data:image")

    Box(
        modifier =
            Modifier
                .size(AVATAR_SIZE)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (avatarUrl.isBlank() || !isValidAvatarUrl) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar de $author",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            GlideImage(
                model = avatarUrl,
                contentDescription = "Avatar de $author",
                modifier =
                    Modifier
                        .size(AVATAR_SIZE)
                        .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

private fun generateUserHandle(author: String): String =
    "@${
        author
            .trim()
            .lowercase()
            .replace(" ", ".")
    }"
