package ar.edu.unlam.mobile.scaffolding.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
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
            LaunchedEffect(Unit) {
                loginViewModel.restoreStatus()
                onLoginSuccess()
            }
        }

        is UiState.Error -> {
            ShowLoginForm(
                emailState,
                passwordState,
                { loginViewModel.updateEmailState(it) },
                { loginViewModel.updatePasswordState(it) },
                { loginViewModel.login() },
                onNavigateToRegister,
                errorMessage = state.error,
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
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    errorMessage: String? = null,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer,
                            ),
                    ),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .alpha(if (visible) 1f else 0f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier.padding(PADDING_LARGE),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PADDING_MEDIUM),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tuiter",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )

                TuiterTextLabel(
                    R.string.login_title_label,
                    MaterialTheme.typography.titleMedium,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    Modifier.padding(top = 4.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))

                TuiterOutlinedTextField(
                    email,
                    onEmailChange,
                    Modifier.fillMaxWidth(),
                    R.string.email_label,
                )

                TuiterOutlinedTextField(
                    password,
                    onPasswordChange,
                    Modifier.fillMaxWidth(),
                    R.string.password_label,
                    PasswordVisualTransformation(),
                    KeyboardOptions(keyboardType = KeyboardType.Password),
                )

                if (!errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TuiterButton(
                    R.string.sign_in,
                    onLoginClick,
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    Modifier.fillMaxWidth(),
                )

                TuiterTextLabel(
                    R.string.not_user_yet,
                    MaterialTheme.typography.bodyMedium,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    Modifier.padding(top = 8.dp),
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
