package ar.edu.unlam.mobile.scaffolding.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterButton
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterOutlinedTextField
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterTextLabel
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val emailState by loginViewModel.email.collectAsState()
    val passwordState by loginViewModel.password.collectAsState()

    when (val state = uiState) {
        is UiState.Idle -> {
            ShowLoginForm(
                emailState,
                passwordState,
                { loginViewModel.updateEmailState(it) },
                { loginViewModel.updatePasswordState(it) },
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
            ShowErrorScreen(state.error) { loginViewModel.restoreStatus() }
        }
    }
}

@Composable
private fun ShowLoginForm(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PADDING_LARGE),
            shape = RoundedCornerShape(PADDING_LARGE),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.padding(PADDING_LARGE),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Tuiter",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                TuiterTextLabel(
                    R.string.login_title_label,
                    MaterialTheme.typography.titleMedium,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    Modifier.padding(bottom = PADDING_LARGE),
                )

                TuiterOutlinedTextField(
                    email,
                    onEmailChange,
                    Modifier.fillMaxWidth(),
                    R.string.email_label,
                )

                TuiterOutlinedTextField(
                    password,
                    onPasswordChange,
                    Modifier
                        .fillMaxWidth()
                        .padding(top = PADDING_MEDIUM),
                    R.string.password_label,
                    PasswordVisualTransformation(),
                    KeyboardOptions(keyboardType = KeyboardType.Password),
                )

                Row(
                    modifier = Modifier.padding(top = PADDING_LARGE),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Checkbox(
                        checked = false,
                        onCheckedChange = null,
                    )
                    TuiterTextLabel(
                        textId = R.string.keep_session_credentials,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = PADDING_MEDIUM),
                    )
                }

                TuiterButton(
                    R.string.sign_in,
                    onLoginClick,
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    Modifier
                        .fillMaxWidth()
                        .padding(top = PADDING_LARGE),
                )

                TuiterTextLabel(
                    R.string.not_user_yet,
                    MaterialTheme.typography.bodyMedium,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    Modifier.padding(top = PADDING_LARGE, bottom = PADDING_MEDIUM),
                )

                TuiterButton(
                    R.string.sign_up,
                    onRegisterClick,
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun ShowErrorScreen(
    errorMessage: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PADDING_LARGE),
            shape = RoundedCornerShape(PADDING_LARGE),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.padding(PADDING_LARGE),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = PADDING_LARGE),
                    textAlign = TextAlign.Center,
                )

                TuiterButton(
                    R.string.retry_label,
                    onRetry,
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    ScaffoldingV2Theme {
        ShowLoginForm(
            email = "",
            password = "",
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onRegisterClick = {},
        )
    }
}
