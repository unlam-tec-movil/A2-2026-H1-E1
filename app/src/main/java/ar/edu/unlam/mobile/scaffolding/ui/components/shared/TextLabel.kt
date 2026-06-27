package ar.edu.unlam.mobile.scaffolding.ui.components.shared

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle

@Composable
fun TuiterTextLabel(
    @StringRes textId: Int,
    textStyle: TextStyle,
    textColor: Color,
    modifier: Modifier,
) {
    Text(
        text = stringResource(textId),
        style = textStyle,
        color = textColor,
        modifier = modifier,
    )
}
