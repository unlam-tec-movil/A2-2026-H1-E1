package ar.edu.unlam.mobile.scaffolding.ui.components.shared

import androidx.annotation.StringRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun TuiterOutlinedTextField(
    valueText: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    @StringRes labelId: Int,
    visualTransFormation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
) {
    OutlinedTextField(
        value = valueText,
        onValueChange = onTextChange,
        modifier = modifier,
        label = {
            Text(stringResource(labelId))
        },
        visualTransformation = visualTransFormation,
        keyboardOptions = keyboardOptions,
    )
}
