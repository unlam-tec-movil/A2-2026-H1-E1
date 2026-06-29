package ar.edu.unlam.mobile.scaffolding.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterButton
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterOutlinedTextField
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterTextLabel
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_SMALL
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState

@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
) {
    val uiState by registerViewModel.uiState.collectAsState()
    val nameState by registerViewModel.name.collectAsState()
    val emailState by registerViewModel.email.collectAsState()
    val passwordState by registerViewModel.password.collectAsState()

    val resetForm = { registerViewModel.resetForm() }

    when (val state = uiState) {
        is UiState.Idle -> {
            ShowRegisterForm(
                nameState,
                emailState,
                passwordState,
                { newNameState -> registerViewModel.updateName(newNameState) },
                { newEmailState -> registerViewModel.updateEmail(newEmailState) },
                { newPasswordState -> registerViewModel.updatePassword(newPasswordState) },
                { registerViewModel.register() },
                resetForm,
            )
        }

        is UiState.Loading -> {
            ShowLoadingStatusOnScreen()
        }

        is UiState.Success -> {
            onRegisterSuccess()
        }

        is UiState.Error -> {
            ShowRegisterErrorScreen(state.error) { registerViewModel.restoreStatus() }
        }
    }
}

@Composable
fun ShowRegisterForm(
    name: String,
    email: String,
    password: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterButtonClick: () -> Unit,
    onResetButtonClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TuiterTextLabel(
            R.string.register_title_label,
            MaterialTheme.typography.headlineLarge,
            MaterialTheme.colorScheme.onSurface,
            Modifier.padding(PADDING_MEDIUM),
        )

        TuiterOutlinedTextField(
            name,
            { newName -> onNameChange(newName) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PADDING_MEDIUM),
            R.string.register_name_label,
        )

        TuiterOutlinedTextField(
            email,
            { newEmail -> onEmailChange(newEmail) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PADDING_MEDIUM),
            R.string.email_label,
        )

        TuiterOutlinedTextField(
            password,
            { newPassword -> onPasswordChange(newPassword) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PADDING_MEDIUM),
            R.string.password_label,
            PasswordVisualTransformation(),
            KeyboardOptions(keyboardType = KeyboardType.Password),
        )

        TuiterButton(
            R.string.confirm,
            onRegisterButtonClick,
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.padding(PADDING_MEDIUM),
        )

        TuiterButton(
            R.string.reset_form,
            onResetButtonClick,
            ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PADDING_MEDIUM),
        )
    }
}

@Composable
private fun ShowRegisterErrorScreen(
    errorMessage: String,
    onRetryButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(PADDING_SMALL)) {
            TuiterTextLabel(
                R.string.user_creation_error_label,
                MaterialTheme.typography.titleMedium,
                MaterialTheme.colorScheme.error,
                Modifier.padding(PADDING_MEDIUM),
            )
        }

        Text(
            errorMessage,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(PADDING_MEDIUM),
        )

        TuiterButton(
            R.string.retry_label,
            onRetryButtonClick,
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.padding(PADDING_MEDIUM),
        )
    }
}
