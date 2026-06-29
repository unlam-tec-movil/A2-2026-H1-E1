package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 2, entities = [Draft::class, FavoriteUser::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDraftDao(): DraftDao
    abstract fun getFavoriteDao(): FavoriteDao
}
