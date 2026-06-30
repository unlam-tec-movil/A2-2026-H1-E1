package ar.edu.unlam.mobile.scaffolding.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import ar.edu.unlam.mobile.scaffolding.ui.components.PostComponent
import ar.edu.unlam.mobile.scaffolding.ui.components.PostUiModel
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    HomeContent(modifier = modifier, state = state)
}

@Composable
fun HomeContent(
    state: HomeUIState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Mostrar mensaje de bienvenida si está disponible
        val helloState = state.helloMessageState
        if (helloState is HelloMessageUIState.Success) {
            Text(
                text = helloState.message,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp),
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            val error = state.error
            if (state.loading && state.posts.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null && state.posts.isEmpty()) {
                Text(
                    text = error,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error,
                )
            } else {
                LazyColumn {
                    items(state.posts) { post ->
                        PostComponent(post = post)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ScaffoldingV2Theme {
        HomeContent(
            state =
                HomeUIState(
                    helloMessageState = HelloMessageUIState.Success("¡Hola, Usuario de Prueba!"),
                    posts =
                        listOf(
                            PostUiModel(
                                id = 1,
                                message = "Este es un post de ejemplo para el preview.",
                                author = "Autor 1",
                                avatarUrl = "",
                                likes = 10,
                                liked = false,
                                date = "2023-10-27",
                            ),
                            PostUiModel(
                                id = 2,
                                message = "Otro post para ver cómo queda la lista.",
                                author = "Autor 2",
                                avatarUrl = "",
                                likes = 5,
                                liked = true,
                                date = "2023-10-26",
                            ),
                        ),
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenLoadingPreview() {
    ScaffoldingV2Theme {
        HomeContent(
            state = HomeUIState(loading = true),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenErrorPreview() {
    ScaffoldingV2Theme {
        HomeContent(
            state = HomeUIState(error = "Ha ocurrido un error inesperado"),
        )
    }
}
