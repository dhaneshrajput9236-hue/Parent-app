package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "child_profiles")
data class ChildProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val avatarEmoji: String = "🧒",
    val dailyGoalMinutes: Int = 120 // Screen time goal in minutes
)

@Entity(tableName = "monitored_apps")
data class MonitoredApp(
    @PrimaryKey val packageName: String,
    val appName: String,
    val categoryName: String, // Education, Games, Social Media, Entertainment, Productivity, Other
    val isMonitored: Boolean = true
)

@Entity(tableName = "app_usage_logs")
data class AppUsageLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childProfileId: Long,
    val packageName: String,
    val appName: String,
    val categoryName: String,
    val dateString: String, // yyyy-MM-dd
    val durationMinutes: Long,
    val launchCount: Int,
    val hasNightUsage: Boolean = false // Track late-night phone usage (10 PM to 5 AM)
)

@Entity(tableName = "timeline_entries")
data class TimelineEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childProfileId: Long,
    val dateString: String, // yyyy-MM-dd
    val timeSlot: String, // e.g., "08:00 - 08:30"
    val appName: String,
    val categoryName: String
)

@Entity(tableName = "focus_sessions")
data class FocusSessionLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childProfileId: Long,
    val sessionName: String, // e.g. "Study mode"
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String, // composite id: childId_achievementId
    val childProfileId: Long,
    val achievementKey: String, // "goal_completed", "clean_week", "sleep_champion", "study_star"
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = false,
    val dateUnlocked: String? = null // yyyy-MM-dd
)
