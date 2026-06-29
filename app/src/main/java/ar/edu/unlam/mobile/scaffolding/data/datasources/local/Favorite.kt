package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorite_table")
data class FavoriteUser(
    @PrimaryKey val author: String,
    val avatarUrl: String,
)

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteUser)

    @Query("SELECT * FROM favorite_table")
    fun getAllFavorites(): Flow<List<FavoriteUser>>

    @Query("DELETE FROM favorite_table WHERE author = :author")
    suspend fun deleteFavorite(author: String)
}
