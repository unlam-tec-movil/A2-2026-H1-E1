package ar.edu.unlam.mobile.scaffolding.ui.components.feed

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun HomeFloatingActionButton(onClickAction: () -> Unit) {
    val buttonIcon = Icons.Default.Add

    FloatingActionButton(onClick = onClickAction) {
        Icon(buttonIcon, contentDescription = null)
    }
}
