package com.numina.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessClassDao {
    @Query("SELECT * FROM fitness_classes WHERE id = :classId")
    fun getClassById(classId: String): Flow<FitnessClassEntity?>

    @Query("SELECT * FROM fitness_classes ORDER BY cachedAt DESC LIMIT :limit")
    fun getRecentClasses(limit: Int = 50): Flow<List<FitnessClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(fitnessClass: FitnessClassEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(classes: List<FitnessClassEntity>)

    @Query("DELETE FROM fitness_classes WHERE cachedAt < :timestamp")
    suspend fun deleteOldClasses(timestamp: Long)

    @Query("DELETE FROM fitness_classes")
    suspend fun deleteAll()
}
