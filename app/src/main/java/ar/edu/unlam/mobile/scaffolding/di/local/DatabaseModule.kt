package ar.edu.unlam.mobile.scaffolding.di.local

import android.content.Context
import androidx.room.Room
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.AppDatabase
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.DraftDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "tuiter_db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideDraftDao(database: AppDatabase): DraftDao = database.getDraftDao()

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao = database.getFavoriteDao()
}
