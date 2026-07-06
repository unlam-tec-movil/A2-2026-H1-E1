package ar.edu.unlam.mobile.scaffolding.ui.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.post.PostResponse
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@Composable
fun PostReplyCard(post: PostResponse) {
    Card(
        modifier =
            Modifier
                .padding(PADDING_MEDIUM)
                .fillMaxWidth(),
        shape = RoundedCornerShape(PADDING_LARGE),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(vertical = PADDING_LARGE)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(horizontal = PADDING_LARGE, vertical = PADDING_MEDIUM),
            ) {
                Text(
                    text = post.author,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = post.date,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier.padding(start = PADDING_LARGE, end = PADDING_LARGE),
            )

            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = PADDING_LARGE,
                            vertical = PADDING_MEDIUM,
                        ),
                shape = RoundedCornerShape(PADDING_MEDIUM),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Text(
                    text = post.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(PADDING_MEDIUM),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostReplyCardPreview() {
    ScaffoldingV2Theme {
        PostReplyCard(
            PostResponse(
                id = 10,
                message = @Suppress("ktlint:standard:max-line-length")
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat",
                parentId = 1,
                authorId = 2,
                author = "Tomas Gabriel Elbert",
                avatarUrl = "http://tomas.avatarurl.com",
                likes = 152,
                liked = true,
                date = "2026-7-1",
            ),
        )
    }
}
