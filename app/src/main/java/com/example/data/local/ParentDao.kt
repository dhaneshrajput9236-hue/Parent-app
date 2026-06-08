package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ParentDao {

    // --- Child Profiles ---
    @Query("SELECT * FROM child_profiles")
    fun getAllChildProfilesFlow(): Flow<List<ChildProfile>>

    @Query("SELECT * FROM child_profiles")
    suspend fun getAllChildProfilesDirect(): List<ChildProfile>

    @Query("SELECT * FROM child_profiles WHERE id = :id")
    suspend fun getChildProfileById(id: Long): ChildProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChildProfile(profile: ChildProfile): Long

    @Query("DELETE FROM child_profiles WHERE id = :id")
    suspend fun deleteChildProfileById(id: Long)

    // --- Monitored Apps ---
    @Query("SELECT * FROM monitored_apps")
    fun getAllMonitoredAppsFlow(): Flow<List<MonitoredApp>>

    @Query("SELECT * FROM monitored_apps")
    suspend fun getAllMonitoredAppsDirect(): List<MonitoredApp>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonitoredApps(apps: List<MonitoredApp>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonitoredApp(app: MonitoredApp)

    @Update
    suspend fun updateMonitoredApp(app: MonitoredApp)

    // --- App Usage Logs ---
    @Query("SELECT * FROM app_usage_logs WHERE childProfileId = :childId")
    fun getUsageLogsForChildFlow(childId: Long): Flow<List<AppUsageLog>>

    @Query("SELECT * FROM app_usage_logs WHERE childProfileId = :childId AND dateString = :date")
    fun getUsageLogsForChildAndDateFlow(childId: Long, date: String): Flow<List<AppUsageLog>>

    @Query("SELECT * FROM app_usage_logs WHERE childProfileId = :childId AND dateString = :date")
    suspend fun getUsageLogsForChildAndDateDirect(childId: Long, date: String): List<AppUsageLog>

    @Query("SELECT * FROM app_usage_logs WHERE childProfileId = :childId")
    suspend fun getAllUsageLogsForChildDirect(childId: Long): List<AppUsageLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppUsageLogs(logs: List<AppUsageLog>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppUsageLog(log: AppUsageLog)

    @Query("DELETE FROM app_usage_logs WHERE childProfileId = :childId")
    suspend fun deleteUsageLogsForChild(childId: Long)

    @Query("DELETE FROM app_usage_logs")
    suspend fun deleteAllUsageLogs()

    // --- Timeline Entries ---
    @Query("SELECT * FROM timeline_entries WHERE childProfileId = :childId AND dateString = :date ORDER BY id DESC")
    fun getTimelineForChildAndDateFlow(childId: Long, date: String): Flow<List<TimelineEntry>>

    @Query("SELECT * FROM timeline_entries WHERE childProfileId = :childId AND dateString = :date")
    suspend fun getTimelineForChildAndDateDirect(childId: Long, date: String): List<TimelineEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimelineEntry(entry: TimelineEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimelineEntries(entries: List<TimelineEntry>)

    @Query("DELETE FROM timeline_entries WHERE childProfileId = :childId")
    suspend fun deleteTimelineForChild(childId: Long)

    // --- Focus Sessions ---
    @Query("SELECT * FROM focus_sessions WHERE childProfileId = :childId ORDER BY timestamp DESC")
    fun getFocusSessionsForChildFlow(childId: Long): Flow<List<FocusSessionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSessionLog)

    @Query("DELETE FROM focus_sessions WHERE childProfileId = :childId")
    suspend fun deleteFocusSessionsForChild(childId: Long)

    // --- Achievements ---
    @Query("SELECT * FROM achievements WHERE childProfileId = :childId")
    fun getAchievementsForChildFlow(childId: Long): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE childProfileId = :childId")
    suspend fun getAchievementsForChildDirect(childId: Long): List<Achievement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)

    @Update
    suspend fun updateAchievement(achievement: Achievement)

    @Query("DELETE FROM achievements WHERE childProfileId = :childId")
    suspend fun deleteAchievementsForChild(childId: Long)
}
