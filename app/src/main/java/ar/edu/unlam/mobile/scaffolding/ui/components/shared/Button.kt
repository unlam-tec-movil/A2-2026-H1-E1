package ar.edu.unlam.mobile.scaffolding.ui.components.shared

import androidx.annotation.StringRes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun TuiterButton(
    @StringRes textId: Int,
    onClickAction: () -> Unit,
    buttonColor: ButtonColors,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    Button(
        onClick = onClickAction,
        modifier = modifier,
        colors = buttonColor,
        enabled = isEnabled,
    ) {
        Text(stringResource(textId))
    }
}
