package ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions

import androidx.lifecycle.ViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.interfaces.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T> : ViewModel() {
    protected val uiStateFlow = MutableStateFlow<UiState<T>>(UiState.Idle)
    val uiState: StateFlow<UiState<T>> = uiStateFlow.asStateFlow()

    fun setUiAsIdle() {
        uiStateFlow.value = UiState.Idle
    }

    fun setUiAsLoading() {
        uiStateFlow.value = UiState.Loading
    }

    fun setUiAsSuccess(data: T) {
        uiStateFlow.value = UiState.Success(data)
    }

    fun setUiAsError(error: String) {
        uiStateFlow.value = UiState.Error(error)
    }
}
