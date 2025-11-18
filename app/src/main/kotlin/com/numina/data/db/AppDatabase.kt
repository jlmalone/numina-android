package com.numina.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UserEntity::class,
        FitnessClassEntity::class,
        ReviewEntity::class,
        ReviewDraftEntity::class,
        BookingEntity::class,
        ReminderPreferencesEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun fitnessClassDao(): FitnessClassDao
    abstract fun reviewDao(): ReviewDao
    abstract fun bookingDao(): BookingDao
    abstract fun reminderPreferencesDao(): ReminderPreferencesDao

    companion object {
        const val DATABASE_NAME = "numina_db"
    }
}
