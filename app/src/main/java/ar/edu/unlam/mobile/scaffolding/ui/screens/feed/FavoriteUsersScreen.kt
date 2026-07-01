package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.FavoriteUser
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterTextLabel
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@Composable
fun FavoriteUserScreen(favoriteUsersViemodel: FavoriteUsersScreenViewModel) {
    val uiState by favoriteUsersViemodel.uiState.collectAsState()

    when (val state = uiState) {
        is UiState.Idle -> {}

        is UiState.Loading -> {
            ShowLoadingStatusOnScreen()
        }

        is UiState.Success -> {
            ShowFavoriteUsersScreen(state.data)
        }

        is UiState.Error -> {
            ShowErrorMessageOnScreen(
                onRestoreStateAction = {},
                errorMessage = state.error,
            )
        }
    }
}

@Composable
fun ShowFavoriteUsersScreen(favoriteUsers: List<FavoriteUser>) {
    Column(modifier = Modifier.padding(8.dp).background(MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            TuiterTextLabel(
                R.string.favorite_users_screen_title,
                textStyle = MaterialTheme.typography.titleLarge,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(PADDING_MEDIUM),
            )
        }

        Column {
            Card(modifier = Modifier.padding(PADDING_MEDIUM), RoundedCornerShape(PADDING_LARGE)) {
                LazyColumn {
                    items(favoriteUsers) { user ->
                        FavoriteUserCard(user)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun FavoriteUserCard(user: FavoriteUser) {
    Row(
        modifier = Modifier.padding(PADDING_MEDIUM),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        GlideImage(
            model = user.avatarUrl,
            contentDescription = null,
            modifier = Modifier.padding(PADDING_MEDIUM),
        )

        Text(
            text = user.author,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
