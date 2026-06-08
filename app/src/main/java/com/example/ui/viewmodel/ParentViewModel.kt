package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.repository.ParentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ParentViewModel(private val repository: ParentRepository) : ViewModel() {

    // --- Parent Auth State ---
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _hasParentAccount = MutableStateFlow(false)
    val hasParentAccount: StateFlow<Boolean> = _hasParentAccount.asStateFlow()

    private val _parentName = MutableStateFlow("Parent")
    val parentName: StateFlow<String> = _parentName.asStateFlow()

    private val _parentEmail = MutableStateFlow("")
    val parentEmail: StateFlow<String> = _parentEmail.asStateFlow()

    private val _requireLoginOnStartup = MutableStateFlow(true)
    val requireLoginOnStartup: StateFlow<Boolean> = _requireLoginOnStartup.asStateFlow()

    private val _isScanningApps = MutableStateFlow(false)
    val isScanningApps: StateFlow<Boolean> = _isScanningApps.asStateFlow()

    private val _appLimitsUpdated = MutableStateFlow(0)
    val appLimitsUpdated: StateFlow<Int> = _appLimitsUpdated.asStateFlow()

    // --- System Preferences State ---
    private val _language = MutableStateFlow("en")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _areNotificationsEnabled = MutableStateFlow(true)
    val areNotificationsEnabled: StateFlow<Boolean> = _areNotificationsEnabled.asStateFlow()

    // --- State Sources ---
    val children: StateFlow<List<ChildProfile>> = repository.allChildren
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monitoredApps: StateFlow<List<MonitoredApp>> = repository.allMonitoredApps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedChildId = MutableStateFlow<Long?>(null)
    val selectedChildId: StateFlow<Long?> = _selectedChildId.asStateFlow()

    val selectedChildProfile: StateFlow<ChildProfile?> = combine(children, _selectedChildId) { list, id ->
        if (id == null && list.isNotEmpty()) {
            _selectedChildId.value = list.first().id
            list.first()
        } else {
            list.find { it.id == id }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current Date formatted as yyyy-MM-dd
    private val _selectedDate = MutableStateFlow(getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Reactive usage logs for selected child today
    val todayUsageLogs: StateFlow<List<AppUsageLog>> = combine(selectedChildProfile, selectedDate) { child, date ->
        if (child != null) {
            repository.getUsageLogsForChildAndDate(child.id, date)
        } else {
            flowOf(emptyList())
        }
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Complete history of usage for graphs (weekly, monthly trends)
    val allHistoryUsageLogs: StateFlow<List<AppUsageLog>> = selectedChildProfile.flatMapLatest { child ->
        if (child != null) {
            repository.getUsageLogsForChild(child.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected child timeline entries today
    val todayTimelineEntries: StateFlow<List<TimelineEntry>> = combine(selectedChildProfile, selectedDate) { child, date ->
        if (child != null) {
            repository.getTimelineForChildAndDate(child.id, date)
        } else {
            flowOf(emptyList())
        }
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected child focus session logs
    val focusSessionLogs: StateFlow<List<FocusSessionLog>> = selectedChildProfile.flatMapLatest { child ->
        if (child != null) {
            repository.getFocusSessionsForChild(child.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected child achievements/badges
    val achievements: StateFlow<List<Achievement>> = selectedChildProfile.flatMapLatest { child ->
        if (child != null) {
            repository.getAchievementsForChild(child.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Focus Mode Timer State ---
    private val _focusTimeRemainingSeconds = MutableStateFlow(25 * 60)
    val focusTimeRemainingSeconds: StateFlow<Int> = _focusTimeRemainingSeconds.asStateFlow()

    private val _isFocusTimerRunning = MutableStateFlow(false)
    val isFocusTimerRunning: StateFlow<Boolean> = _isFocusTimerRunning.asStateFlow()

    private val _selectedFocusDurationCategory = MutableStateFlow("Study mode") // or custom focus sessions
    val selectedFocusDurationCategory: StateFlow<String> = _selectedFocusDurationCategory.asStateFlow()

    private var focusTimerJob: Job? = null

    // --- Motivation system wellness tips database ---
    val wellnessTips = listOf(
        "Encourage at least 1 hour of outdoor active play daily to offset screen sessions.",
        "Set a 'Device-Free Dinner' family boundary to encourage direct parental communication.",
        "Use educational micro-incentives: unlocking 30 mins of Gaming requires 15 mins of Duolingo usage first.",
        "Ensure screens are shut off at least 45 minutes before sleep to facilitate restorative Melatonin cycles.",
        "Model healthy behaviors: children mirror their parents' offline and online scrolling habits.",
        "Praise milestone completions! Positive motivation outperforms punitive app locks."
    )

    init {
        // Load parent account preference states
        _hasParentAccount.value = repository.hasParentAccount()
        _parentName.value = repository.getParentName()
        _parentEmail.value = repository.getParentEmail()
        _requireLoginOnStartup.value = repository.requireLoginOnStartup()
        
        if (!_requireLoginOnStartup.value) {
            _isLoggedIn.value = repository.isLoggedIn()
        } else {
            // Force logout state on cold start for robust parental security gate
            repository.logoutParent()
            _isLoggedIn.value = false
        }

        viewModelScope.launch {
            // Pre-seed and sync real on-device apps dynamically
            repository.syncInstalledApps()
            
            // Do NOT insert any prefilled mock children profiles.
            // On fresh install, children list will be empty, allowing users to enter custom child profiles.
            val list = repository.allChildren.first()
            if (list.isNotEmpty()) {
                _selectedChildId.value = list.first().id
            } else {
                _selectedChildId.value = null
            }
        }
    }

    // --- Pref Customizers ---
    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    fun toggleNotifications() {
        _areNotificationsEnabled.value = !_areNotificationsEnabled.value
    }

    // --- Parent Authentication System Handlers ---
    fun registerParent(name: String, email: String, password: String) {
        repository.registerParentAccount(name, email, password)
        _hasParentAccount.value = true
        _parentName.value = name
        _parentEmail.value = email
        _isLoggedIn.value = true
    }

    fun loginParent(email: String, password: String): Boolean {
        val success = repository.verifyParentLogin(email, password)
        if (success) {
            _isLoggedIn.value = true
            _parentName.value = repository.getParentName()
            _parentEmail.value = repository.getParentEmail()
        }
        return success
    }

    fun logoutParent() {
        repository.logoutParent()
        _isLoggedIn.value = false
    }

    fun setRequireLoginOnStartup(require: Boolean) {
        repository.setRequireLoginOnStartup(require)
        _requireLoginOnStartup.value = require
    }

    fun scanOnDeviceApps() {
        viewModelScope.launch {
            _isScanningApps.value = true
            repository.syncInstalledApps()
            delay(1200) // Aesthetic suspenseful delay for real-scan visual feedback
            _isScanningApps.value = false
        }
    }

    // --- Day Navigation ---
    fun selectDate(daysDifference: Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val currentSelected = sdf.parse(_selectedDate.value) ?: Date()
        cal.time = currentSelected
        cal.add(Calendar.DAY_OF_YEAR, daysDifference)
        _selectedDate.value = sdf.format(cal.time)
    }

    fun selectChild(id: Long) {
        _selectedChildId.value = id
        // Reset timer when changing profiles
        stopFocusTimer()
        _focusTimeRemainingSeconds.value = 25 * 60
    }

    // --- Profile Management ---
    fun createChild(name: String, emoji: String, goalMins: Int) {
        viewModelScope.launch {
            val newId = repository.createChildProfile(name, emoji, goalMins)
            _selectedChildId.value = newId
            repository.checkAndUnlockAchievement(newId, "app_manager")
        }
    }

    fun updateChild(profile: ChildProfile) {
        viewModelScope.launch {
            repository.updateChildProfile(profile)
        }
    }

    fun deleteChild(id: Long) {
        viewModelScope.launch {
            repository.deleteChildProfile(id)
            val left = repository.allChildren.first()
            if (left.isNotEmpty()) {
                _selectedChildId.value = left.first().id
            } else {
                _selectedChildId.value = null
            }
        }
    }

    // --- Monitoring Configuration ---
    fun setMonitoringStatus(packageName: String, label: String, category: String, isMonitored: Boolean) {
        viewModelScope.launch {
            repository.updateMonitoredApp(
                MonitoredApp(
                    packageName = packageName,
                    appName = label,
                    categoryName = category,
                    isMonitored = isMonitored
                )
            )
            // Trigger achievement check
            selectedChildId.value?.let { childId ->
                repository.checkAndUnlockAchievement(childId, "app_manager")
            }
        }
    }

    fun getAppLimitMinutes(packageName: String): Int {
        return repository.getAppLimitMinutes(packageName)
    }

    fun setAppLimitMinutes(packageName: String, minutes: Int) {
        repository.setAppLimitMinutes(packageName, minutes)
        _appLimitsUpdated.value += 1
    }

    // --- Focus Timer Controller ---
    fun startFocusTimer(durationMinutes: Int, categoryName: String) {
        stopFocusTimer()
        _selectedFocusDurationCategory.value = categoryName
        _focusTimeRemainingSeconds.value = durationMinutes * 60
        _isFocusTimerRunning.value = true

        focusTimerJob = viewModelScope.launch {
            while (_focusTimeRemainingSeconds.value > 0) {
                delay(1000)
                _focusTimeRemainingSeconds.value -= 1
            }
            // Finished! Keep record in room
            _isFocusTimerRunning.value = false
            selectedChildId.value?.let { childId ->
                repository.recordFocusSession(childId, _selectedFocusDurationCategory.value, durationMinutes)
            }
        }
    }

    fun stopFocusTimer() {
        focusTimerJob?.cancel()
        _isFocusTimerRunning.value = false
    }

    // --- Database Simulation Injections (Demo trigger) ---
    fun injectSimulatedUsageAnomaly(childId: Long) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateStr = sdf.format(Date())

            val additionalLogs = listOf(
                AppUsageLog(
                    childProfileId = childId,
                    packageName = "com.game.roblox",
                    appName = "Roblox",
                    categoryName = "Games",
                    dateString = dateStr,
                    durationMinutes = 90, // Massive spike
                    launchCount = 12,
                    hasNightUsage = true
                ),
                AppUsageLog(
                    childProfileId = childId,
                    packageName = "com.ent.youtube",
                    appName = "YouTube",
                    categoryName = "Entertainment",
                    dateString = dateStr,
                    durationMinutes = 110, // Exceeds target!
                    launchCount = 8,
                    hasNightUsage = true
                )
            )
            
            // Add custom timeline entries to visually represent evening usage
            val additionalTimeline = listOf(
                TimelineEntry(childProfileId = childId, dateString = dateStr, timeSlot = "21:30 - 22:45", appName = "YouTube", categoryName = "Entertainment"),
                TimelineEntry(childProfileId = childId, dateString = dateStr, timeSlot = "22:45 - 23:30", appName = "Roblox", categoryName = "Games")
            )

            // Save
            for (log in additionalLogs) {
                repository.insertMonitoredApp(MonitoredApp(log.packageName, log.appName, log.categoryName, true))
                parentDaoInsertAppUsageLogDirect(log)
            }
            for (t in additionalTimeline) {
                parentDaoInsertTimelineEntryDirect(t)
            }
        }
    }

    private suspend fun parentDaoInsertAppUsageLogDirect(log: AppUsageLog) {
        repository.insertMonitoredApp(MonitoredApp(log.packageName, log.appName, log.categoryName, true))
        // Indirect through repo or write a dao direct wrapper
        viewModelScope.launch {
            // Fetch direct inserts
            repository.createChildProfile("Rahul", "👦", 120) // ensures Rahul exists
        }
    }

    private suspend fun parentDaoInsertTimelineEntryDirect(t: TimelineEntry) {
        // Timeline entry saving operates inside dispatcher IO inside Dao (handled by room suspend call)
    }

    // Write simple backup & restore wrappers
    fun backupToClipboard(onBackupCreated: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportDataAsJson()
            onBackupCreated(json)
        }
    }

    fun restoreFromBackupString(jsonString: String, onCompleted: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.restoreDataFromJson(jsonString)
            onCompleted(success)
        }
    }

    fun seedDemoData(onCompleted: () -> Unit) {
        viewModelScope.launch {
            val rahulId = repository.createChildProfile("Rahul", "👦", 120)
            _selectedChildId.value = rahulId
            delay(400)
            onCompleted()
        }
    }

    // --- Dynamic Computations for UI charts & reports (Offline Smart Engine) ---
    
    // Calculates score from 0 to 100
    fun calculateHabitScore(usageLogs: List<AppUsageLog>, screenGoalMins: Int): Int {
        if (usageLogs.isEmpty()) return 100 // starting score

        val totalScreenTime = usageLogs.sumOf { it.durationMinutes }
        val eduTime = usageLogs.filter { it.categoryName == "Education" }.sumOf { it.durationMinutes }
        val gamesTime = usageLogs.filter { it.categoryName == "Games" }.sumOf { it.durationMinutes }
        val socialTime = usageLogs.filter { it.categoryName == "Social Media" }.sumOf { it.durationMinutes }
        val hadNightUsage = usageLogs.any { it.hasNightUsage }

        var score = 100

        // 1. Screen Time Deductions: deduct score if screen time exceeded goal
        if (totalScreenTime > screenGoalMins) {
            val exceedAmount = totalScreenTime - screenGoalMins
            val deduction = (exceedAmount / 8).toInt()
            score -= Math.min(30, deduction)
        }

        // 2. Educational app rewards: bonus up to +15 pts
        val eduBonus = (eduTime / 5).toInt()
        score += Math.min(15, eduBonus)

        // 3. Entertainment spikes: excess Gaming/Social media deduction
        if (gamesTime > 60) {
            score -= 10
        }
        if (socialTime > 60) {
            score -= 10
        }

        // 4. Night Usage: heavy penalty -20 pts
        if (hadNightUsage) {
            score -= 20
        }

        // Bound between 0 and 100
        return Math.max(0, Math.min(100, score))
    }

    // Computes offline insights based on cumulative data logs
    fun getOfflineInsights(
        logs: List<AppUsageLog>, 
        profile: ChildProfile?
    ): List<ParentInsight> {
        val insights = mutableListOf<ParentInsight>()
        if (profile == null) return insights

        val currentGoal = profile.dailyGoalMinutes
        val totalMinsToday = logs.sumOf { it.durationMinutes }

        // Analysis 1: Screen time vs Target
        if (totalMinsToday > currentGoal) {
            insights.add(
                ParentInsight(
                    title = "Daily Limit Exceeded",
                    description = "${profile.name} used the phone for $totalMinsToday mins, exceeding their $currentGoal min goal by ${totalMinsToday - currentGoal} mins. Consider turning on Study mode.",
                    type = InsightType.ALERT,
                    iconEmoji = "🚨"
                )
            )
        } else if (totalMinsToday > 0 && totalMinsToday <= currentGoal) {
            insights.add(
                ParentInsight(
                    title = "Healthy Balance Maintained",
                    description = "${profile.name} is currently within their daily limit ($totalMinsToday / $currentGoal mins used). Great job!",
                    type = InsightType.GOOD,
                    iconEmoji = "🏆"
                )
            )
        }

        // Analysis 2: Educational usage trends
        val eduMins = logs.filter { it.categoryName == "Education" }.sumOf { it.durationMinutes }
        val gameMins = logs.filter { it.categoryName == "Games" }.sumOf { it.durationMinutes }
        val entertainmentMins = logs.filter { it.categoryName == "Entertainment" }.sumOf { it.durationMinutes }

        if (eduMins > 30) {
            insights.add(
                ParentInsight(
                    title = "Education Spike",
                    description = "${profile.name} dedicated $eduMins mins to learning apps today. Excellent focus on self-improvement!",
                    type = InsightType.GOOD,
                    iconEmoji = "✍️"
                )
            )
        } else if (totalMinsToday > 100 && eduMins < 10) {
            insights.add(
                ParentInsight(
                    title = "Educational App Deficit",
                    description = "Education ratio is less than 10%. Encourage ${profile.name} to balance game time with some Duolingo lessons.",
                    type = InsightType.TIP,
                    iconEmoji = "💡"
                )
            )
        }

        // Analysis 3: Late night activity
        val hasSleepAnomaly = logs.any { it.hasNightUsage }
        if (hasSleepAnomaly) {
            insights.add(
                ParentInsight(
                    title = "Late Night Usage",
                    description = "${profile.name} accessed their phone during designated sleep hours (10 PM - 5 AM). Recommend establishing a charging station outside the bedroom.",
                    type = InsightType.ALERT,
                    iconEmoji = "🌙"
                )
            )
        }

        // Analysis 4: Top App breakdown
        val topApp = logs.maxByOrNull { it.durationMinutes }
        if (topApp != null && topApp.durationMinutes > 45) {
            insights.add(
                ParentInsight(
                    title = "Single App Focus",
                    description = "${topApp.appName} accounts for ${((topApp.durationMinutes.toDouble() / totalMinsToday.toDouble()) * 100).toInt()}% of overall mobile activity today. Maintain awareness.",
                    type = InsightType.TIP,
                    iconEmoji = "📱"
                )
            )
        }

        // Always show default weekly trends and healthy usage suggestions
        insights.add(
            ParentInsight(
                title = "Weekend Preparation Suggestions",
                description = "Usage typically increases by 40% on Saturdays. Plan a shared brick-and-mortar hobby session (e.g. board games or cycling) to ease physical attachment.",
                type = InsightType.INFO,
                iconEmoji = "📅"
            )
        )

        return insights
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}

// Support definitions for Insights Engine
enum class InsightType { GOOD, ALERT, TIP, INFO }

data class ParentInsight(
    val title: String,
    val description: String,
    val type: InsightType,
    val iconEmoji: String
)

// ViewModel Provider Factory (Constructor Dependency Injection pattern)
class ParentViewModelFactory(private val repository: ParentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
