package ar.edu.unlam.mobile.scaffolding.ui.screens.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterButton
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterOutlinedTextField
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@Composable
fun ProfileScreen(
    profileInfoViewModel: ProfileInfoViewModel,
    onNavigateBackAction: () -> Unit,
    onLogout: () -> Unit = {},
) {
    val uiState by profileInfoViewModel.uiState.collectAsState()
    val currentName by profileInfoViewModel.name.collectAsState()
    val currentEmail by profileInfoViewModel.email.collectAsState()
    val newPassword by profileInfoViewModel.newPassword.collectAsState()
    val newConfirmPassword by profileInfoViewModel.newPasswordConfirm.collectAsState()
    val isSavingStatus by profileInfoViewModel.isSaving.collectAsState()

    BackHandler {
        onNavigateBackAction()
    }

    LaunchedEffect(true) {
        profileInfoViewModel.getProfileInfo()
    }

    when (val state = uiState) {
        is UiState.Loading -> {
            ShowLoadingStatusOnScreen()
        }

        is UiState.Error -> {
            ShowErrorMessageOnScreen(
                onNavigateBackAction,
                state.error,
            )
        }

        is UiState.Success -> {
            ShowProfileForm(
                currentName,
                currentEmail,
                newPassword,
                newConfirmPassword,
                profileInfoViewModel,
                isSavingStatus,
                onLogout,
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
    profileInfoViewModel: ProfileInfoViewModel,
    isSavingStatus: Boolean,
    onLogout: () -> Unit = {},
) {
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
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(PADDING_LARGE),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.user_profile_title_label),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Surface(
                modifier =
                    Modifier
                        .padding(horizontal = PADDING_MEDIUM)
                        .fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 1.dp,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            ) {
                Column(
                    modifier =
                        Modifier
                            .padding(PADDING_MEDIUM)
                            .verticalScroll(rememberScrollState()),
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

                    ConfirmChangesButton(
                        { profileInfoViewModel.sendProfileInfoUpdate() },
                        isSavingStatus,
                    )

                    TuiterButton(
                        textId = R.string.logout,
                        onClickAction = onLogout,
                        buttonColor =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                            ),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(PADDING_MEDIUM),
                    )
                }
            }
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
    Surface(
        modifier = Modifier.fillMaxWidth().padding(PADDING_MEDIUM),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
    ) {
        Column(
            modifier = Modifier.padding(PADDING_MEDIUM),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            ShowPasswordRequirementsList()
        }
    }
}

@Composable
fun ShowPasswordRequirementsList() {
    val passwordListItemsMap = generatePasswordListLabelMap()
    val entries = passwordListItemsMap.entries.toList()

    entries.forEachIndexed { index, item ->
        val textListColor =
            if (index == 0) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
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
        modifier = Modifier.fillMaxWidth().padding(PADDING_MEDIUM),
        isEnabled = !enabledStatus,
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    ScaffoldingV2Theme {
        Surface(
            modifier = Modifier.fillMaxSize().padding(PADDING_MEDIUM),
            shape = RoundedCornerShape(PADDING_MEDIUM),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.padding(PADDING_MEDIUM).fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Perfil",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = PADDING_MEDIUM),
                    )
                }

                UserNameTextfield(currentName = "Usuario Ejemplo") {}
                EmailTextfield(currentEmail = "usuario@email.com") {}
                ShowPasswordHelp()
                PasswordTextfields(
                    newPassword = "",
                    newConfirmPassword = "",
                    onNewPasswordChangeAction = {},
                    onNewPasswordConfirmChangeAction = {},
                )
                ConfirmChangesButton(onConfirmChangesAction = {}, enabledStatus = true)
            }
        }
    }
}
