package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ChildProfile::class,
        MonitoredApp::class,
        AppUsageLog::class,
        TimelineEntry::class,
        FocusSessionLog::class,
        Achievement::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ParentDatabase : RoomDatabase() {

    abstract fun parentDao(): ParentDao

    companion object {
        @Volatile
        private var INSTANCE: ParentDatabase? = null

        fun getDatabase(context: Context): ParentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParentDatabase::class.java,
                    "smart_parent_ai_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
