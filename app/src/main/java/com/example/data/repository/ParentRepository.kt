package com.example.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.data.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ParentRepository(
    private val parentDao: ParentDao,
    private val context: Context
) {
    // Expose Flow data sources to the presentation layer
    val allChildren: Flow<List<ChildProfile>> = parentDao.getAllChildProfilesFlow()
    val allMonitoredApps: Flow<List<MonitoredApp>> = parentDao.getAllMonitoredAppsFlow()

    fun getUsageLogsForChild(childId: Long): Flow<List<AppUsageLog>> =
        parentDao.getUsageLogsForChildFlow(childId)

    fun getUsageLogsForChildAndDate(childId: Long, date: String): Flow<List<AppUsageLog>> =
        parentDao.getUsageLogsForChildAndDateFlow(childId, date)

    fun getTimelineForChildAndDate(childId: Long, date: String): Flow<List<TimelineEntry>> =
        parentDao.getTimelineForChildAndDateFlow(childId, date)

    fun getFocusSessionsForChild(childId: Long): Flow<List<FocusSessionLog>> =
        parentDao.getFocusSessionsForChildFlow(childId)

    fun getAchievementsForChild(childId: Long): Flow<List<Achievement>> =
        parentDao.getAchievementsForChildFlow(childId)

    // --- Profile Operations ---
    suspend fun createChildProfile(name: String, avatarEmoji: String, goalMinutes: Int): Long {
        val id = parentDao.insertChildProfile(ChildProfile(name = name, avatarEmoji = avatarEmoji, dailyGoalMinutes = goalMinutes))
        // Auto-generate some sample historic data so that graphs work instantly for this child profile
        withContext(Dispatchers.IO) {
            generateSimulationData(id)
        }
        return id
    }

    suspend fun updateChildProfile(profile: ChildProfile) {
        parentDao.insertChildProfile(profile)
    }

    suspend fun deleteChildProfile(id: Long) {
        parentDao.deleteChildProfileById(id)
        parentDao.deleteUsageLogsForChild(id)
        parentDao.deleteTimelineForChild(id)
        parentDao.deleteFocusSessionsForChild(id)
        parentDao.deleteAchievementsForChild(id)
    }

    // --- Monitored App Operations ---
    suspend fun updateMonitoredApp(app: MonitoredApp) {
        parentDao.updateMonitoredApp(app)
    }

    suspend fun insertMonitoredApp(app: MonitoredApp) {
        parentDao.insertMonitoredApp(app)
    }

    // --- Focus Sessions ---
    suspend fun recordFocusSession(childId: Long, sessionName: String, durationMinutes: Int) {
        parentDao.insertFocusSession(
            FocusSessionLog(
                childProfileId = childId,
                sessionName = sessionName,
                durationMinutes = durationMinutes
            )
        )
        
        // Check for achievements related to focus modes
        checkAndUnlockAchievement(childId, "study_star")
    }

    // --- App System Sync --
    suspend fun syncInstalledApps() = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val realApps = try {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
        } catch (e: Exception) {
            emptyList()
        }

        val dbApps = parentDao.getAllMonitoredAppsDirect().associateBy { it.packageName }
        val updatedList = mutableListOf<MonitoredApp>()
        val addedPackageNames = mutableSetOf<String>()

        // Add real packages found on system
        for (appInfo in realApps) {
            if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) { // user app
                val packageName = appInfo.packageName
                if (!dbApps.containsKey(packageName) && packageName != context.packageName && !addedPackageNames.contains(packageName)) {
                    val label = pm.getApplicationLabel(appInfo).toString()
                    val category = guessCategory(packageName, label)
                    updatedList.add(MonitoredApp(packageName, label, category))
                    addedPackageNames.add(packageName)
                }
            }
        }

        // Add system launchable apps (Chrome, Play Store, YouTube, settings, etc.) to ensure full monitoring capability
        try {
            val mainIntent = android.content.Intent(android.content.Intent.ACTION_MAIN, null).apply {
                addCategory(android.content.Intent.CATEGORY_LAUNCHER)
            }
            val launchableApps = pm.queryIntentActivities(mainIntent, 0)
            for (resolveInfo in launchableApps) {
                val packageName = resolveInfo.activityInfo.packageName
                if (packageName != context.packageName && !dbApps.containsKey(packageName) && !addedPackageNames.contains(packageName)) {
                    val label = resolveInfo.loadLabel(pm).toString()
                    val category = guessCategory(packageName, label)
                    updatedList.add(MonitoredApp(packageName, label, category))
                    addedPackageNames.add(packageName)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (updatedList.isNotEmpty()) {
            parentDao.insertMonitoredApps(updatedList)
        }
    }

    private fun guessCategory(packageName: String, label: String): String {
        val cleanLabel = label.lowercase()
        val cleanPkg = packageName.lowercase()
        return when {
            cleanLabel.contains("game") || cleanLabel.contains("play") || cleanLabel.contains("puzzle") || cleanPkg.contains("game") || cleanPkg.contains("toy") -> "Games"
            cleanLabel.contains("study") || cleanLabel.contains("learn") || cleanLabel.contains("class") || cleanLabel.contains("math") || cleanLabel.contains("educa") || cleanPkg.contains("education") -> "Education"
            cleanLabel.contains("chat") || cleanLabel.contains("social") || cleanLabel.contains("meet") || cleanLabel.contains("whatsapp") || cleanLabel.contains("messenger") || cleanPkg.contains("social") -> "Social Media"
            cleanLabel.contains("video") || cleanLabel.contains("tv") || cleanLabel.contains("movie") || cleanLabel.contains("music") || cleanLabel.contains("tube") || cleanPkg.contains("netflix") || cleanPkg.contains("youtube") -> "Entertainment"
            cleanLabel.contains("note") || cleanLabel.contains("office") || cleanLabel.contains("docs") || cleanLabel.contains("drive") || cleanLabel.contains("calendar") || cleanPkg.contains("work") || cleanPkg.contains("productivity") -> "Productivity"
            else -> "Other"
        }
    }

    // --- Achievement Verification ---
    suspend fun checkAndUnlockAchievement(childId: Long, key: String) {
        val achievements = parentDao.getAchievementsForChildDirect(childId)
        val target = achievements.find { it.achievementKey == key }
        if (target != null && !target.isUnlocked) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateStr = sdf.format(Date())
            parentDao.updateAchievement(target.copy(isUnlocked = true, dateUnlocked = dateStr))
        }
    }

    // --- Offline Interactive Data Simulation ---
    suspend fun generateSimulationData(childId: Long) = withContext(Dispatchers.IO) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val categories = listOf("Education", "Games", "Social Media", "Entertainment", "Productivity", "Other")
        val appNames = mapOf(
            "Education" to listOf("Duolingo", "Khan Academy", "Wikipedia"),
            "Games" to listOf("Roblox", "Minecraft", "Subway Surfers"),
            "Social Media" to listOf("WhatsApp", "Instagram", "Snapchat"),
            "Entertainment" to listOf("YouTube", "Netflix", "Hotstar"),
            "Productivity" to listOf("Google Classroom", "Zoom", "Notes"),
            "Other" to listOf("Chrome", "Files", "Calculator")
        )

        val packagePrefixes = mapOf(
            "Education" to "com.edu",
            "Games" to "com.game",
            "Social Media" to "com.social",
            "Entertainment" to "com.ent",
            "Productivity" to "com.prod",
            "Other" to "com.other"
        )

        // Seed 30 days of backup data to make reporting truly spectacular
        val logsList = mutableListOf<AppUsageLog>()
        val timelineList = mutableListOf<TimelineEntry>()

        for (daysAgo in 30 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val dateStr = sdf.format(calendar.time)

            // Randomize screen time based on day of week: more time on weekends
            val isWeekend = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
            val targetTotal = if (isWeekend) (100..260).random() else (40..150).random()

            // Distribute screen time among categories
            var remainingTime = targetTotal
            for (category in categories) {
                if (remainingTime <= 0) break
                val apps = appNames[category] ?: emptyList()
                val pkgPrefix = packagePrefixes[category] ?: "com.app"

                // Create usage entries for 1-2 apps in this category
                val appsCount = (1..2).random()
                val shuffledApps = apps.shuffled()

                for (i in 0 until Math.min(appsCount, shuffledApps.size)) {
                    if (remainingTime <= 0) break
                    val appName = shuffledApps[i]
                    val pkg = "$pkgPrefix.${appName.lowercase().replace(" ", "")}"

                    // Educational apps have higher usage on weekdays, Games on weekends
                    val percentage = when (category) {
                        "Education" -> if (isWeekend) (5..15).random() else (20..35).random()
                        "Games" -> if (isWeekend) (30..50).random() else (10..20).random()
                        "Social Media" -> (10..25).random()
                        "Entertainment" -> (15..30).random()
                        else -> (5..15).random()
                    }

                    val duration = Math.min(remainingTime.toLong(), (targetTotal * percentage / 100).toLong())
                    if (duration > 0) {
                        remainingTime -= duration.toInt()
                        val launches = (1..15).random()
                        
                        // Late night usage? (Weekend entertainment or social apps might trigger bedtime usage)
                        val nightUsage = isWeekend && (category == "Games" || category == "Entertainment") && (0..100).random() > 60

                        logsList.add(
                            AppUsageLog(
                                childProfileId = childId,
                                packageName = pkg,
                                appName = appName,
                                categoryName = category,
                                dateString = dateStr,
                                durationMinutes = duration,
                                launchCount = launches,
                                hasNightUsage = nightUsage
                            )
                        )
                    }
                }
            }

            // Generate timeline for TODAY (daysAgo = 0) to demonstrate usage timeline beautifully
            if (daysAgo == 0) {
                timelineList.add(TimelineEntry(childProfileId = childId, dateString = dateStr, timeSlot = "08:15 - 08:35", appName = "Duolingo", categoryName = "Education"))
                timelineList.add(TimelineEntry(childProfileId = childId, dateString = dateStr, timeSlot = "09:00 - 09:40", appName = "Google Classroom", categoryName = "Productivity"))
                timelineList.add(TimelineEntry(childProfileId = childId, dateString = dateStr, timeSlot = "12:15 - 12:45", appName = "Roblox", categoryName = "Games"))
                timelineList.add(TimelineEntry(childProfileId = childId, dateString = dateStr, timeSlot = "14:00 - 14:15", appName = "WhatsApp", categoryName = "Social Media"))
                timelineList.add(TimelineEntry(childProfileId = childId, dateString = dateStr, timeSlot = "17:30 - 18:15", appName = "YouTube", categoryName = "Entertainment"))
                timelineList.add(TimelineEntry(childProfileId = childId, dateString = dateStr, timeSlot = "20:30 - 21:00", appName = "Wikipedia", categoryName = "Education"))
            }
        }

        parentDao.insertAppUsageLogs(logsList)
        if (timelineList.isNotEmpty()) {
            parentDao.insertTimelineEntries(timelineList)
        }

        // Seed Achievements
        val defaultAchievements = listOf(
            Achievement("ach1_$childId", childId, "goal_completed", "First Victory", "First screen time goal met successfully", "🎯", false, null),
            Achievement("ach2_$childId", childId, "clean_week", "Weekly Champion", "Kept total screen time under target for 7 consecutive days", "👑", false, null),
            Achievement("ach3_$childId", childId, "sleep_champion", "Restful Rest", "Zero late-night usage detected this week", "🛌", false, null),
            Achievement("ach4_$childId", childId, "study_star", "Academic Focus", "Completed a custom Study session in Focus Mode", "✏️", false, null),
            Achievement("ach5_$childId", childId, "app_manager", "Safety Sealed", "Parent configured monitored applications", "🛡️", true, sdf.format(Date()))
        )
        parentDao.insertAchievements(defaultAchievements)
    }

    // --- Data Export & Import (Backup & Restore) ---
    suspend fun exportDataAsJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        
        val profiles = parentDao.getAllChildProfilesDirect()
        val jsonProfiles = JSONArray()
        for (p in profiles) {
            val jp = JSONObject()
            jp.put("id", p.id)
            jp.put("name", p.name)
            jp.put("avatarEmoji", p.avatarEmoji)
            jp.put("dailyGoalMinutes", p.dailyGoalMinutes)
            jsonProfiles.put(jp)
            
            // Collect child data for logs, history, timeline, focus & achievements
            val logs = parentDao.getAllUsageLogsForChildDirect(p.id)
            val jsonLogs = JSONArray()
            for (l in logs) {
                val jl = JSONObject()
                jl.put("packageName", l.packageName)
                jl.put("appName", l.appName)
                jl.put("categoryName", l.categoryName)
                jl.put("dateString", l.dateString)
                jl.put("durationMinutes", l.durationMinutes)
                jl.put("launchCount", l.launchCount)
                jl.put("hasNightUsage", l.hasNightUsage)
                jsonLogs.put(jl)
            }
            root.put("logs_${p.id}", jsonLogs)

            val achievements = parentDao.getAchievementsForChildDirect(p.id)
            val jsonAch = JSONArray()
            for (a in achievements) {
                val ja = JSONObject()
                ja.put("id", a.id)
                ja.put("achievementKey", a.achievementKey)
                ja.put("title", a.title)
                ja.put("description", a.description)
                ja.put("iconEmoji", a.iconEmoji)
                ja.put("isUnlocked", a.isUnlocked)
                ja.put("dateUnlocked", a.dateUnlocked ?: "")
                jsonAch.put(ja)
            }
            root.put("achievements_${p.id}", jsonAch)
        }
        root.put("profiles", jsonProfiles)

        val monitored = parentDao.getAllMonitoredAppsDirect()
        val jsonMonitored = JSONArray()
        for (m in monitored) {
            val jm = JSONObject()
            jm.put("packageName", m.packageName)
            jm.put("appName", m.appName)
            jm.put("categoryName", m.categoryName)
            jm.put("isMonitored", m.isMonitored)
            jsonMonitored.put(jm)
        }
        root.put("monitored_apps", jsonMonitored)

        root.toString(2)
    }

    suspend fun restoreDataFromJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            
            // Parse and restore profiles
            val jsonProfiles = root.optJSONArray("profiles") ?: return@withContext false
            for (i in 0 until jsonProfiles.length()) {
                val jp = jsonProfiles.getJSONObject(i)
                val originalId = jp.getLong("id")
                val name = jp.getString("name")
                val avatarEmoji = jp.optString("avatarEmoji", "🧒")
                val dailyGoalMinutes = jp.optInt("dailyGoalMinutes", 120)

                // Insert child profile and get new inserted ID
                val newProfile = ChildProfile(name = name, avatarEmoji = avatarEmoji, dailyGoalMinutes = dailyGoalMinutes)
                val newId = parentDao.insertChildProfile(newProfile)

                // Restore historical usage logs matching original child ID mapped to new ID
                val jsonLogs = root.optJSONArray("logs_$originalId")
                if (jsonLogs != null) {
                    val logsToInsert = mutableListOf<AppUsageLog>()
                    for (j in 0 until jsonLogs.length()) {
                        val jl = jsonLogs.getJSONObject(j)
                        logsToInsert.add(
                            AppUsageLog(
                                childProfileId = newId,
                                packageName = jl.getString("packageName"),
                                appName = jl.getString("appName"),
                                categoryName = jl.getString("categoryName"),
                                dateString = jl.getString("dateString"),
                                durationMinutes = jl.getLong("durationMinutes"),
                                launchCount = jl.getInt("launchCount"),
                                hasNightUsage = jl.optBoolean("hasNightUsage", false)
                            )
                        )
                    }
                    parentDao.insertAppUsageLogs(logsToInsert)
                }

                // Restore achievements
                val jsonAch = root.optJSONArray("achievements_$originalId")
                if (jsonAch != null) {
                    val achToInsert = mutableListOf<Achievement>()
                    for (j in 0 until jsonAch.length()) {
                        val ja = jsonAch.getJSONObject(j)
                        val key = ja.getString("achievementKey")
                        val dateUnlockedStr = ja.optString("dateUnlocked", "")
                        achToInsert.add(
                            Achievement(
                                id = "${key}_$newId",
                                childProfileId = newId,
                                achievementKey = key,
                                title = ja.getString("title"),
                                description = ja.getString("description"),
                                iconEmoji = ja.getString("iconEmoji"),
                                isUnlocked = ja.getBoolean("isUnlocked"),
                                dateUnlocked = if (dateUnlockedStr.isEmpty()) null else dateUnlockedStr
                            )
                        )
                    }
                    parentDao.insertAchievements(achToInsert)
                }
            }

            // Restore monitored apps configuration
            val jsonMonitored = root.optJSONArray("monitored_apps")
            if (jsonMonitored != null) {
                val monitoredToInsert = mutableListOf<MonitoredApp>()
                for (i in 0 until jsonMonitored.length()) {
                    val jm = jsonMonitored.getJSONObject(i)
                    monitoredToInsert.add(
                        MonitoredApp(
                            packageName = jm.getString("packageName"),
                            appName = jm.getString("appName"),
                            categoryName = jm.getString("categoryName"),
                            isMonitored = jm.optBoolean("isMonitored", true)
                        )
                    )
                }
                parentDao.insertMonitoredApps(monitoredToInsert)
            }
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- Parent Authentication System ---
    private val prefs = context.getSharedPreferences("ParentPrefs", Context.MODE_PRIVATE)

    fun hasParentAccount(): Boolean {
        return prefs.getString("parent_email", null) != null
    }

    fun getParentName(): String {
        return prefs.getString("parent_name", "Parent") ?: "Parent"
    }

    fun getParentEmail(): String {
        return prefs.getString("parent_email", "") ?: ""
    }

    fun registerParentAccount(name: String, email: String, password: String) {
        prefs.edit().apply {
            putString("parent_name", name)
            putString("parent_email", email)
            putString("parent_password", password)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun verifyParentLogin(email: String, password: String): Boolean {
        val savedEmail = prefs.getString("parent_email", null)
        val savedPassword = prefs.getString("parent_password", null)
        return if (savedEmail != null && savedPassword != null) {
            val success = savedEmail.equals(email, ignoreCase = true) && savedPassword == password
            if (success) {
                prefs.edit().putBoolean("is_logged_in", true).apply()
            }
            success
        } else {
            false
        }
    }

    fun logoutParent() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun requireLoginOnStartup(): Boolean {
        return prefs.getBoolean("require_login_on_startup", true)
    }

    fun setRequireLoginOnStartup(require: Boolean) {
        prefs.edit().putBoolean("require_login_on_startup", require).apply()
    }

    fun getAppLimitMinutes(packageName: String): Int {
        return prefs.getInt("app_limit_mins_$packageName", 45) // Default 45 mins
    }

    fun setAppLimitMinutes(packageName: String, minutes: Int) {
        prefs.edit().putInt("app_limit_mins_$packageName", minutes).apply()
    }
}
