package ar.edu.unlam.mobile.scaffolding.ui.screens.favorites

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.FavoriteUser
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.FavoritesRepository
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel
    @Inject
    constructor(
        private val favoritesRepository: FavoritesRepository,
    ) : BaseViewModel<List<FavoriteUser>>() {
        val favorites: StateFlow<List<FavoriteUser>> =
            favoritesRepository.getAllFavorites().stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList(),
            )

        fun removeFavorite(author: String) {
            viewModelScope.launch {
                favoritesRepository.removeFavorite(author)
            }
        }
    }
