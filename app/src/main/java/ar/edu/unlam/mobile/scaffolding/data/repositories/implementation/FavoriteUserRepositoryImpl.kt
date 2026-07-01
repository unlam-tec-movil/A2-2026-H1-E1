package ar.edu.unlam.mobile.scaffolding.data.repositories.implementation

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.FavoriteUser
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.FavoriteUserDao
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.FavoriteUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteUserRepositoryImpl
    @Inject
    constructor(
        private val favoriteUserDao: FavoriteUserDao,
    ) : FavoriteUserRepository {
        override suspend fun saveUserAsFavorite(favoriteUser: FavoriteUser) {
            favoriteUserDao.insertFavoriteUser(favoriteUser)
        }

        override suspend fun deleteUserFromFavorites(favoriteUserName: String) {
            favoriteUserDao.deleteFavoriteUser(favoriteUserName)
        }

        override fun getAllFavoriteUsers(): Flow<List<FavoriteUser>> = favoriteUserDao.getUsersMarkedAsFavorite()
    }
