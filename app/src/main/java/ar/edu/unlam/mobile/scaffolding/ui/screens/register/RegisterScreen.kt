package ar.edu.unlam.mobile.scaffolding.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterButton
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterOutlinedTextField
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterTextLabel
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PADDING_LARGE)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(PADDING_LARGE))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(PADDING_LARGE),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Crear Cuenta",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                TuiterTextLabel(
                    R.string.register_title_label,
                    MaterialTheme.typography.titleMedium,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    Modifier.padding(bottom = PADDING_LARGE),
                )

                TuiterOutlinedTextField(
                    name,
                    onNameChange,
                    Modifier.fillMaxWidth(),
                    R.string.register_name_label,
                )

                TuiterOutlinedTextField(
                    email,
                    onEmailChange,
                    Modifier.fillMaxWidth().padding(top = PADDING_MEDIUM),
                    R.string.email_label,
                )

                TuiterOutlinedTextField(
                    password,
                    onPasswordChange,
                    Modifier.fillMaxWidth().padding(top = PADDING_MEDIUM),
                    R.string.password_label,
                    PasswordVisualTransformation(),
                    KeyboardOptions(keyboardType = KeyboardType.Password),
                )

                TuiterButton(
                    R.string.confirm,
                    onRegisterClick,
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    Modifier.fillMaxWidth().padding(top = PADDING_LARGE),
                )

                TuiterButton(
                    R.string.reset_form,
                    onResetClick,
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    Modifier.fillMaxWidth().padding(top = PADDING_MEDIUM),
                )
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
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PADDING_LARGE)
                .clip(RoundedCornerShape(PADDING_LARGE))
                .background(MaterialTheme.colorScheme.surface)
                .padding(PADDING_LARGE),
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
