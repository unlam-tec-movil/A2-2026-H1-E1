package ar.edu.unlam.mobile.scaffolding.ui.screens.post

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.Draft
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_SMALL
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.DRAFT_SAVED
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement

private const val DRAFT_BUTTON_TEXT = "Guardar borrador"
private const val DRAFTS_BUTTON_TEXT = "Borradores"
private const val POST_BUTTON_TEXT = "Publicar"
private const val TEXTFIELD_PROMPT_TEXT = "¿Qué estás pensando?..."
private const val DRAFT_CREATED_SNACKBAR_TEXT = "¡Tu borrador ha sido creado!"

@Composable
fun PostCreationScreen(
    postCreationViewModel: PostCreationViewModel,
    onPostAction: () -> Unit,
    onCancelAction: () -> Unit,
    onShowSnackbar: (String) -> Unit,
) {
    val message by postCreationViewModel.message.collectAsState()
    val drafts by postCreationViewModel.drafts.collectAsState()
    val uiState by postCreationViewModel.uiState.collectAsState()
    val restoreState = { postCreationViewModel.restoreStatus() }

    when (val state = uiState) {
        is UiState.Idle -> {
            ShowPostCreationForm(
                message = message,
                drafts = drafts,
                onPostMessageChangeAction = { postCreationViewModel.onMessageChange(it) },
                onDraftAction = { postCreationViewModel.createDraft(message) },
                onSelectDraft = { postCreationViewModel.selectDraft(it) },
                onPostAction = { postCreationViewModel.createPost() },
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
            } else {
                onShowSnackbar(state.data)
                restoreState()
                onPostAction()
            }
        }

        is UiState.Error -> {
            ShowErrorMessageOnScreen(
                restoreState,
                state.error,
            )
        }
    }
}

@Composable
private fun ShowPostCreationForm(
    message: String,
    drafts: List<Draft>,
    onPostMessageChangeAction: (String) -> Unit,
    onDraftAction: () -> Unit,
    onSelectDraft: (Draft) -> Unit,
    onPostAction: () -> Unit,
    onCancelAction: () -> Unit,
) {
    var showDraftsDialog by remember { mutableStateOf(false) }

    Surface(
        modifier =
            Modifier
                .padding(PADDING_MEDIUM)
                .fillMaxSize(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
    ) {
        Column {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(PADDING_MEDIUM),
            ) {
                IconButton(onClick = onCancelAction) {
                    Icon(Icons.Default.Cancel, contentDescription = null)
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onPostAction,
                    enabled = message.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Text(POST_BUTTON_TEXT)
                }
            }

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PADDING_MEDIUM),
                horizontalArrangement = Arrangement.spacedBy(PADDING_SMALL),
            ) {
                Button(
                    onClick = { showDraftsDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                ) {
                    Text(DRAFTS_BUTTON_TEXT)
                }

                Button(
                    onClick = onDraftAction,
                    modifier = Modifier.weight(1f),
                    enabled = message.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                ) {
                    Text(DRAFT_BUTTON_TEXT)
                }
            }

            TextField(
                value = message,
                placeholder = { Text(TEXTFIELD_PROMPT_TEXT) },
                onValueChange = { newMessage -> onPostMessageChangeAction(newMessage) },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(PADDING_LARGE),
                colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
            )
        }
    }

    if (showDraftsDialog) {
        DraftsDialog(
            drafts = drafts,
            onDismiss = { showDraftsDialog = false },
            onSelectDraft = { draft ->
                onSelectDraft(draft)
                showDraftsDialog = false
            },
        )
    }
}

@Composable
private fun DraftsDialog(
    drafts: List<Draft>,
    onDismiss: () -> Unit,
    onSelectDraft: (Draft) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Borradores") },
        text = {
            if (drafts.isEmpty()) {
                Text("No tenés borradores guardados.")
            } else {
                LazyColumn {
                    items(drafts) { draft ->
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectDraft(draft) }
                                    .padding(vertical = PADDING_MEDIUM),
                        ) {
                            Text(
                                text = draft.postMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }

                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
    )
}

@Composable
fun ShowErrorMessageOnScreen(
    onRestoreStateAction: () -> Unit,
    errorMessage: String,
) {
    Row(modifier = Modifier.padding(PADDING_MEDIUM)) {
        IconButton(
            onClick = onRestoreStateAction,
        ) {
            Icon(Icons.Default.ArrowCircleLeft, contentDescription = null)
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            "Ocurrió un error al procesar el post: $errorMessage",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(PADDING_MEDIUM),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PostCreationScreenPreview() {
    ScaffoldingV2Theme {
        ShowPostCreationForm(
            message = "",
            drafts =
                listOf(
                    Draft(postId = 1, postMessage = "Primer borrador"),
                    Draft(postId = 2, postMessage = "Segundo borrador"),
                ),
            onPostMessageChangeAction = {},
            onDraftAction = {},
            onSelectDraft = {},
            onPostAction = {},
            onCancelAction = {},
        )
    }
}