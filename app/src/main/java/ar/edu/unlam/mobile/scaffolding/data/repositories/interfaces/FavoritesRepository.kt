package ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.FavoriteUser
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addFavorite(author: String, avatarUrl: String)

    suspend fun removeFavorite(author: String)

    fun getAllFavorites(): Flow<List<FavoriteUser>>
}
