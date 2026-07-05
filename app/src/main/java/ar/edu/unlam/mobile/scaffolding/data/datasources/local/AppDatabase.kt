package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.Draft
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.DraftDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.FavoriteUser
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.FavoriteUserDao

@Database(version = 1, entities = [Draft::class, FavoriteUser::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDraftDao(): DraftDao

    abstract fun getFavoriteUserDao(): FavoriteUserDao
}
