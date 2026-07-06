package ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.FavoriteUser
import kotlinx.coroutines.flow.Flow

interface FavoriteUserRepository {
    suspend fun saveUserAsFavorite(favoriteUser: FavoriteUser)

    suspend fun deleteUserFromFavorites(favoriteUserName: String)

    fun getAllFavoriteUsers(): Flow<List<FavoriteUser>>
}
