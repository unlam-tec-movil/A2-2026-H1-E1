package ar.edu.unlam.mobile.scaffolding.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun RegisterScreen(
    registerViewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
) {
    val uiState by registerViewModel.uiState.collectAsState()
    val nameState by registerViewModel.name.collectAsState()
    val emailState by registerViewModel.email.collectAsState()
    val passwordState by registerViewModel.password.collectAsState()

    when (val state = uiState) {
        is UiState.Idle -> {
            ShowRegisterForm(
                nameState,
                emailState,
                passwordState,
                { registerViewModel.updateName(it) },
                { registerViewModel.updateEmail(it) },
                { registerViewModel.updatePassword(it) },
                { registerViewModel.register() },
                { registerViewModel.resetForm() },
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
    onRegisterClick: () -> Unit,
    onResetClick: () -> Unit,
) {
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
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
                        text = "Crear Cuenta",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                    )

                    TuiterTextLabel(
                        R.string.register_title_label,
                        MaterialTheme.typography.titleMedium,
                        MaterialTheme.colorScheme.onSurfaceVariant,
                        Modifier.padding(top = 4.dp),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TuiterOutlinedTextField(
                        name,
                        onNameChange,
                        Modifier.fillMaxWidth(),
                        R.string.register_name_label,
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
                        Modifier.fillMaxWidth(),
                        R.string.password_label,
                        PasswordVisualTransformation(),
                        KeyboardOptions(keyboardType = KeyboardType.Password),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TuiterButton(
                        R.string.confirm,
                        onRegisterClick,
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        Modifier.fillMaxWidth(),
                    )

                    TuiterButton(
                        R.string.reset_form,
                        onResetClick,
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowRegisterErrorScreen(
    errorMessage: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PADDING_LARGE),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            shadowElevation = 2.dp,
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

                TuiterTextLabel(
                    R.string.user_creation_error_label,
                    MaterialTheme.typography.titleMedium,
                    MaterialTheme.colorScheme.onSurface,
                    Modifier.padding(vertical = PADDING_MEDIUM),
                )

                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = PADDING_LARGE),
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
private fun RegisterScreenPreview() {
    ScaffoldingV2Theme {
        ShowRegisterForm(
            name = "",
            email = "",
            password = "",
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRegisterClick = {},
            onResetClick = {},
        )
    }
}
