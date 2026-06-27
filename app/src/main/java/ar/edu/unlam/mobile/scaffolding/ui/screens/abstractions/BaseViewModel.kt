package ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions

import androidx.lifecycle.ViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T> : ViewModel() {
    @Suppress("ktlint:standard:backing-property-naming")
    protected val _uiState = MutableStateFlow<UiState<T>>(UiState.Idle)
    val uiState: StateFlow<UiState<T>> = _uiState.asStateFlow()

    fun setUiAsIdle() {
        _uiState.value = UiState.Idle
    }

    fun setUiAsLoading() {
        _uiState.value = UiState.Loading
    }

    fun setUiAsSuccess(data: T) {
        _uiState.value = UiState.Success(data)
    }

    fun setUiAsError(error: String) {
        _uiState.value = UiState.Error(error)
    }
}
