package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Draft::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDraftDao(): DraftDao
}
