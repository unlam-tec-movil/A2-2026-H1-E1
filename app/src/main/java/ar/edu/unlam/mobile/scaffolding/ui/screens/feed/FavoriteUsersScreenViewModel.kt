package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.FavoriteUser
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.FavoriteUserRepository
import ar.edu.unlam.mobile.scaffolding.ui.constant.text.TextConstant.UNKNOWN_ERROR_MESSAGE
import ar.edu.unlam.mobile.scaffolding.ui.screens.abstractions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteUsersScreenViewModel
    @Inject
    constructor(
        private val favoriteUserRepository: FavoriteUserRepository,
    ) : BaseViewModel<List<FavoriteUser>>() {
        init {
            loadFavoritesUsers()
        }

        fun loadFavoritesUsers() {
            viewModelScope.launch {
                setUiAsLoading()

                try {
                    val favoritesUsers = getFirstItemOnFavoritesUsersFlow()
                    setUiAsSuccess(favoritesUsers)
                } catch (exception: Exception) {
                    setUiAsError(exception.message ?: UNKNOWN_ERROR_MESSAGE)
                }
            }
        }

        fun deleteFromFavoritesByAuthorId(authorId: Int) {
            viewModelScope.launch {
                setUiAsLoading()

                try {
                    favoriteUserRepository.deleteUserFromFavorites(authorId)
                    val favoritesUsers = getFirstItemOnFavoritesUsersFlow()
                    setUiAsSuccess(favoritesUsers)
                } catch (exception: Exception) {
                    setUiAsError(exception.message ?: UNKNOWN_ERROR_MESSAGE)
                }
            }
        }

        private suspend fun getFirstItemOnFavoritesUsersFlow(): List<FavoriteUser> = favoriteUserRepository.getAllFavoriteUsers().first()
    }
