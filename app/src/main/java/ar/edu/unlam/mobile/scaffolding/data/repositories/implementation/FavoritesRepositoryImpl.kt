package ar.edu.unlam.mobile.scaffolding.data.repositories.implementation

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.FavoriteDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.FavoriteUser
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoritesRepositoryImpl
    @Inject
    constructor(
        private val favoriteDao: FavoriteDao,
    ) : FavoritesRepository {
        override suspend fun addFavorite(author: String, avatarUrl: String) {
            favoriteDao.insert(FavoriteUser(author, avatarUrl))
        }

        override suspend fun removeFavorite(author: String) {
            favoriteDao.deleteFavorite(author)
        }

        override fun getAllFavorites(): Flow<List<FavoriteUser>> = favoriteDao.getAllFavorites()
    }
