package dev.alexmester.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.alexmester.database.entity.ClapEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClapDao {

    @Query("SELECT * FROM claps WHERE articleId = :articleId")
    fun getClapFlow(articleId: Long): Flow<ClapEntity?>

    @Query("SELECT count FROM claps WHERE articleId = :articleId")
    suspend fun getClapCount(articleId: Long): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(clap: ClapEntity)

    @Query("UPDATE claps SET count = count + 1 WHERE articleId = :articleId")
    suspend fun increment(articleId: Long)

    @Query("SELECT * FROM claps WHERE count > 0")
    fun getAllClaps(): Flow<List<ClapEntity>>
}