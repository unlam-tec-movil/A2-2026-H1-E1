package ar.edu.unlam.mobile.scaffolding.di.local

import android.content.Context
import androidx.room.Room
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.AppDatabase
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.DraftDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.FavoriteUserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideRoomDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "tuiter_db").build()

    @Provides
    @Singleton
    fun provideDraftDao(database: AppDatabase): DraftDao = database.getDraftDao()

    @Provides
    @Singleton
    fun provideFavoriteUserDao(database: AppDatabase): FavoriteUserDao = database.getFavoriteUserDao()
}
