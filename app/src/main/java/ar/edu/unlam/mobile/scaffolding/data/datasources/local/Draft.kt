package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "draft_table")
data class Draft(
    @PrimaryKey(autoGenerate = true)
    val postId: Int = 0,
    val postMessage: String,
)

@Dao
interface DraftDao {
    @Insert
    suspend fun insert(draft: Draft)

    @Query("SELECT * FROM draft_table")
    fun getAllDrafts(): Flow<List<Draft>>

    @Query("DELETE FROM draft_table WHERE postId = :postId")
    suspend fun deleteDraft(postId: Int)
}
