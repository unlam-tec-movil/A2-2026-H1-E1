package ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>

    data object Loading : UiState<Nothing>

    data class Success<T>(
        val data: T,
    ) : UiState<T>

    data class Error(
        val error: String,
    ) : UiState<Nothing>
}
