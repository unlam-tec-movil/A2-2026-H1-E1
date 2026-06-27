package ar.edu.unlam.mobile.scaffolding.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterButton
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterOutlinedTextField
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_SMALL
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen

@Composable
fun ProfileScreen(
    profileInfoViewModel: ProfileInfoViewModel,
    onReturnClickAction: () -> Unit,
) {
    val uiState by profileInfoViewModel.uiState.collectAsState()
    val currentName by profileInfoViewModel.name.collectAsState()
    val currentEmail by profileInfoViewModel.email.collectAsState()
    val currentAvatarURL by profileInfoViewModel.avatarUrl.collectAsState()
    val newPassword by profileInfoViewModel.newPassword.collectAsState()
    val newConfirmPassword by profileInfoViewModel.newPasswordConfirm.collectAsState()
    val isSavingStatus by profileInfoViewModel.isSaving.collectAsState()

    when (val state = uiState) {
        is UiState.Loading -> {
            ShowLoadingStatusOnScreen()
        }

        is UiState.Error -> {
            ShowErrorMessageOnScreen(
                onReturnClickAction,
                state.error,
            )
        }

        is UiState.Success -> {
            ShowProfileForm(
                currentName,
                currentEmail,
                newPassword,
                newConfirmPassword,
                onReturnClickAction,
                profileInfoViewModel,
                isSavingStatus,
            )
        }

        UiState.Idle -> {}
    }
}

@Composable
fun ShowProfileForm(
    currentName: String,
    currentEmail: String,
    newPassword: String,
    newConfirmPassword: String,
    onReturnClickAction: () -> Unit,
    profileInfoViewModel: ProfileInfoViewModel,
    isSavingStatus: Boolean,
) {
    Column(
        modifier =
            Modifier
                .padding(PADDING_MEDIUM)
                .background(MaterialTheme.colorScheme.surface)
                .clip(RoundedCornerShape(PADDING_MEDIUM))
                .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(
                onClick = onReturnClickAction,
                modifier = Modifier.padding(PADDING_MEDIUM),
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = null)
            }

            Text(
                text = stringResource(R.string.user_profile_title_label),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Column(
            modifier =
                Modifier
                    .padding(PADDING_MEDIUM)
                    .fillMaxSize()
                    .border(
                        shape = RoundedCornerShape(PADDING_MEDIUM),
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
        ) {
            UserNameTextfield(
                currentName,
            ) { newUserName -> profileInfoViewModel.onNameChange(newUserName) }

            EmailTextfield(
                currentEmail,
            ) { newEmail -> profileInfoViewModel.onEmailChange(newEmail) }

            ShowPasswordHelp()

            PasswordTextfields(
                newPassword,
                newConfirmPassword,
                { newPassword -> profileInfoViewModel.onNewPasswordChange(newPassword) },
                { confirmNewPassword ->
                    profileInfoViewModel.onNewPasswordConfirmChange(
                        confirmNewPassword,
                    )
                },
            )

            ConfirmChangesButton({ profileInfoViewModel.sendProfileInfoUpdate() }, isSavingStatus)
        }
    }
}

@Composable
fun UserNameTextfield(
    currentName: String,
    onNameChange: (String) -> Unit,
) {
    TuiterOutlinedTextField(
        valueText = currentName,
        onTextChange = onNameChange,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(PADDING_MEDIUM),
        labelId = R.string.edit_user_name_prompt_text,
    )
}

@Composable
fun EmailTextfield(
    currentEmail: String,
    onEmailChange: (String) -> Unit,
) {
    TuiterOutlinedTextField(
        valueText = currentEmail,
        onTextChange = onEmailChange,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(PADDING_MEDIUM),
        labelId = R.string.edit_user_email_prompt_text,
    )
}

@Composable
fun ShowPasswordHelp() {
    Column(
        modifier =
            Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(PADDING_MEDIUM)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(PADDING_MEDIUM),
                ).clip(shape = RoundedCornerShape(PADDING_MEDIUM))
                .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        ShowPasswordRequirementsList()
    }
}

@Composable
fun ShowPasswordRequirementsList() {
    val passwordListItemsMap = generatePasswordListLabelMap()

    for (item in passwordListItemsMap) {
        val textListColor = MaterialTheme.colorScheme.onSurface
        val textListModifier = Modifier.padding(PADDING_MEDIUM)

        PasswordHelpText(
            item.key,
            textListColor,
            item.value,
            textListModifier,
        )
    }
}

@Composable
private fun generatePasswordListLabelMap(): Map<String, TextStyle> {
    val map: Map<String, TextStyle> =
        mapOf(
            stringResource(R.string.password_update_help_title) to MaterialTheme.typography.titleMedium,
            stringResource(R.string.at_least_8_characters) to MaterialTheme.typography.bodyMedium,
            stringResource(R.string.at_least_1_uppercase) to MaterialTheme.typography.bodyMedium,
            stringResource(R.string.at_least_1_lowercase) to MaterialTheme.typography.bodyMedium,
            stringResource(R.string.at_least_1_digit) to MaterialTheme.typography.bodyMedium,
            stringResource(R.string.at_least_1_special_character) to MaterialTheme.typography.bodyMedium,
        )

    return map
}

@Composable
private fun generatePasswordTextfieldConfigurationMap(): List<Int> {
    val passwordTextfieldPromptTextList: List<Int> =
        listOf(
            R.string.edit_user_password_prompt_text,
            R.string.edit_user_password_confirm_prompt_text,
        )

    return passwordTextfieldPromptTextList
}

@Composable
fun PasswordHelpText(
    text: String,
    textColor: Color,
    textStyle: TextStyle,
    modifier: Modifier,
) {
    Text(
        text = text,
        color = textColor,
        style = textStyle,
        modifier = modifier,
    )
}

@Composable
fun PasswordTextfields(
    newPassword: String,
    newConfirmPassword: String,
    onNewPasswordChangeAction: (String) -> Unit,
    onNewPasswordConfirmChangeAction: (String) -> Unit,
) {
    val promptTexts = generatePasswordTextfieldConfigurationMap()

    val modifier =
        Modifier
            .fillMaxWidth()
            .padding(PADDING_MEDIUM)
    val visualTransformation = PasswordVisualTransformation()
    val keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)

    TuiterOutlinedTextField(
        newPassword,
        onNewPasswordChangeAction,
        modifier,
        promptTexts[0],
        visualTransformation,
        keyboardOptions,
    )

    TuiterOutlinedTextField(
        newConfirmPassword,
        onNewPasswordConfirmChangeAction,
        modifier,
        promptTexts[1],
        visualTransformation,
        keyboardOptions,
    )
}

@Composable
fun ConfirmChangesButton(
    onConfirmChangesAction: () -> Unit,
    enabledStatus: Boolean,
) {
    TuiterButton(
        textId = R.string.confirm,
        onClickAction = onConfirmChangesAction,
        buttonColor = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.padding(PADDING_SMALL),
        isEnabled = !enabledStatus,
    )
}
