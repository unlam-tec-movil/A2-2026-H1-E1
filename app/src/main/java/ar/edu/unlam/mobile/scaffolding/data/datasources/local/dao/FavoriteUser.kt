package ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorite_user")
data class FavoriteUser(
    @PrimaryKey
    val author: String,
    val avatarUrl: String,
)

@Dao
interface FavoriteUserDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertFavoriteUser(user: FavoriteUser)

    @Query("SELECT * FROM favorite_user")
    fun getUsersMarkedAsFavorite(): Flow<List<FavoriteUser>>

    @Query("DELETE FROM favorite_user WHERE author = :author")
    suspend fun deleteFavoriteUser(author: String)
}
