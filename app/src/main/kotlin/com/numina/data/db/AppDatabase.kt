package com.numina.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [UserEntity::class, FitnessClassEntity::class, ReviewEntity::class, ReviewDraftEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun fitnessClassDao(): FitnessClassDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        const val DATABASE_NAME = "numina_db"
    }
}
