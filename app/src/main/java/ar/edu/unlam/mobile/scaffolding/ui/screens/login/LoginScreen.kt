package ar.edu.unlam.mobile.scaffolding.ui.screens.login

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
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val emailState by loginViewModel.email.collectAsState()
    val passwordState by loginViewModel.password.collectAsState()
    val restoreState = { loginViewModel.restoreStatus() }

    when (val state = uiState) {
        is UiState.Idle -> {
            ShowLoginForm(
                emailState,
                passwordState,
                { newEmailState -> loginViewModel.updateEmailState(newEmailState) },
                { newPasswordState -> loginViewModel.updatePasswordState(newPasswordState) },
                { loginViewModel.login() },
                onNavigateToRegister,
            )
        }

        is UiState.Loading -> {
            ShowLoadingStatusOnScreen()
        }

        is UiState.Success -> {
            onLoginSuccess()
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
private fun ShowLoginForm(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginButtonClick: () -> Unit,
    onRegisterButtonClick: () -> Unit,
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
            R.string.login_title_label,
            MaterialTheme.typography.headlineLarge,
            MaterialTheme.colorScheme.primary,
            Modifier.padding(PADDING_MEDIUM),
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
            R.string.sign_in,
            onLoginButtonClick,
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PADDING_MEDIUM),
        )

        TuiterTextLabel(
            R.string.not_user_yet,
            MaterialTheme.typography.titleSmall,
            MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(PADDING_MEDIUM),
        )

        TuiterButton(
            R.string.sign_up,
            onRegisterButtonClick,
            ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PADDING_MEDIUM),
        )
    }
}

@Composable
private fun ShowErrorMessageOnScreen(
    restoreState: () -> Unit,
    errorMessage: String,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(PADDING_SMALL)) {
            TuiterTextLabel(
                R.string.login_error_label,
                MaterialTheme.typography.titleMedium,
                MaterialTheme.colorScheme.error,
                Modifier.padding(PADDING_MEDIUM),
            )

            Text(
                errorMessage,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(PADDING_MEDIUM),
            )
        }

        TuiterTextLabel(
            R.string.login_error_status_code,
            MaterialTheme.typography.titleMedium,
            MaterialTheme.colorScheme.error,
            Modifier.padding(PADDING_MEDIUM),
        )

        TuiterButton(
            R.string.retry_label,
            restoreState,
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier,
        )
    }
}
