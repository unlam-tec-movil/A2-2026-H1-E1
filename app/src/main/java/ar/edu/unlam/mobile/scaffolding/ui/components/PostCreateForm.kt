package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE

@Composable
fun PostCreateForm(
    postText: String,
    onPostTextChange: (String) -> Unit,
    onPublishClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(PADDING_LARGE),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil",
                modifier = Modifier.size(72.dp),
            )
            Spacer(
                modifier = Modifier.width(PADDING_LARGE),
            )
            OutlinedTextField(
                value = postText,
                onValueChange = { onPostTextChange(it) },
                placeholder = { Text("¿Qué estás pensando?") },
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Button(
                onClick = onPublishClick,
            ) {
                Text("Publicar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostCreateFormPreview() {
    PostCreateForm(
        postText = "",
        onPostTextChange = {},
        onPublishClick = {},
    )
}
