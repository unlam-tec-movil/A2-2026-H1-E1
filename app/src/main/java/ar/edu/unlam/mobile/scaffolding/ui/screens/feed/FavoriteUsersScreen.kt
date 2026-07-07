package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.FavoriteUser
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.ShowLoadingStatusOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.components.shared.TuiterTextLabel
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.AVATAR_SIZE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import ar.edu.unlam.mobile.scaffolding.ui.screens.post.ShowErrorMessageOnScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@Composable
fun FavoriteUserScreen(
    favoriteUsersViemodel: FavoriteUsersScreenViewModel,
    onBackAction: () -> Unit,
    onFavoriteUserClick: (FavoriteUser) -> Unit = {},
) {
    val uiState by favoriteUsersViemodel.uiState.collectAsState()

    BackHandler {
        onBackAction()
    }

    LaunchedEffect(true) {
        favoriteUsersViemodel.loadFavoritesUsers()
    }

    when (val state = uiState) {
        is UiState.Idle -> {}

        is UiState.Loading -> {
            ShowLoadingStatusOnScreen()
        }

        is UiState.Success -> {
            ShowFavoriteUsersScreen(
                favoriteUsers = state.data,
                onFavoriteUserClick = onFavoriteUserClick,
                onRemoveFromFavoritesAction = { authorId ->
                    favoriteUsersViemodel.deleteFromFavoritesByAuthorId(authorId)
                },
            )
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
fun ShowFavoriteUsersScreen(
    favoriteUsers: List<FavoriteUser>,
    onFavoriteUserClick: (FavoriteUser) -> Unit,
    onRemoveFromFavoritesAction: (Int) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(PADDING_MEDIUM),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PADDING_MEDIUM),
        ) {
            TuiterTextLabel(
                R.string.favorite_users_screen_title,
                textStyle = MaterialTheme.typography.titleLarge,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(PADDING_MEDIUM),
            )
        }

        Card(
            modifier = Modifier.padding(PADDING_MEDIUM),
            shape = RoundedCornerShape(PADDING_LARGE),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation =
                cardElevation(
                    defaultElevation = 2.dp,
                ),
        ) {
            LazyColumn {
                items(favoriteUsers) { user ->
                    FavoriteUserCard(
                        user = user,
                        onFavoriteUserClick = { onFavoriteUserClick(user) },
                        onRemoveFromFavoritesAction = {
                            onRemoveFromFavoritesAction(user.authorId)
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun FavoriteUserCard(
    user: FavoriteUser,
    onFavoriteUserClick: () -> Unit,
    onRemoveFromFavoritesAction: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onFavoriteUserClick() }
                .padding(PADDING_MEDIUM),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PADDING_MEDIUM),
    ) {
        Box(
            modifier =
                Modifier
                    .size(AVATAR_SIZE)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            GlideImage(
                model = user.avatarUrl,
                contentDescription = "Avatar de ${user.author}",
                modifier =
                    Modifier
                        .size(AVATAR_SIZE)
                        .clip(CircleShape),
            )
        }

        Column {
            Text(
                text = user.author,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
            )

            Text(
                text = generateUserHandle(user.author),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onRemoveFromFavoritesAction,
        ) {
            Icon(
                Icons.Default.DeleteOutline,
                contentDescription = "Eliminar usuario de favoritos",
            )
        }
    }
}

private fun generateUserHandle(author: String): String =
    "@${
        author
            .trim()
            .lowercase()
            .replace(" ", ".")
    }"

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FavoriteUsersScreenPreview() {
    ScaffoldingV2Theme {
        ShowFavoriteUsersScreen(
            favoriteUsers =
                listOf(
                    FavoriteUser(
                        authorId = 1,
                        author = "Paloma Aguirre",
                        avatarUrl = "https://ui-avatars.com/api/?name=Paloma+Aguirre",
                    ),
                    FavoriteUser(
                        authorId = 2,
                        author = "Alan Irigoin",
                        avatarUrl = "https://ui-avatars.com/api/?name=Alan+Irigoin",
                    ),
                ),
            onFavoriteUserClick = {},
            onRemoveFromFavoritesAction = {},
        )
    }
}