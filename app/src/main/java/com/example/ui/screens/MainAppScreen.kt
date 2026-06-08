package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.*
import com.example.ui.viewmodel.InsightType
import com.example.ui.viewmodel.ParentInsight
import com.example.ui.viewmodel.ParentViewModel
import java.text.SimpleDateFormat
import java.util.*

// --- High-Fidelity Localization dictionary ---
object Dictionary {
    private val en = mapOf(
        "dashboard" to "Dashboard",
        "app_rules" to "Rules",
        "focus_mode" to "Focus",
        "timeline" to "Timeline",
        "achievements" to "Badges",
        "settings" to "Settings",
        "hello_parent" to "Hello Parent",
        "offline_notice" to "Fully Encrypted, Offline Mode Enabled",
        "no_child" to "No Child Profile Active. Create one below!",
        "goal_prog" to "Today's Limit Progress",
        "mins_used" to "mins used",
        "daily_goal" to "Daily Goal",
        "habit_score" to "Habit Score",
        "excellent" to "Excellent",
        "moderate" to "Moderate",
        "needs_work" to "Needs Work",
        "study_vs_ent" to "Study vs Entertainment",
        "app_ranking" to "Screen Time Rankings",
        "top_used" to "Most Used",
        "least_used" to "Least Used",
        "offline_insights" to "Smart Offline Insights",
        "monitored_apps" to "App Blocker & Limits",
        "all_categories" to "All Categories",
        "search_hint" to "Search installed packages...",
        "monitoring" to "Monitoring",
        "monitored" to "Monitored",
        "unmonitored" to "Ignored",
        "productivity_timer" to "Productivity Timer",
        "focus_sub" to "Lock down entertainment during school sessions",
        "start_session" to "Start Focus Session",
        "cancel" to "Cancel Session",
        "session_active" to "Focus Session Active!",
        "focus_completed" to "Focus Milestone Achieved!",
        "timeline_title" to "Usage Timeline",
        "timeline_sub" to "Chronologic app log audits for today",
        "inject_anomaly" to "Simulate Usage Spike",
        "inject_desc" to "Triggers extreme gameplay and night data to reveal alert patterns in charts",
        "achievement_milestones" to "Achievements & Badges",
        "milestone_sub" to "Consistently healthy mobile habits earn reward trophies",
        "profile_mgt" to "Profiles & Device Backup",
        "add_child" to "Add New Child Profile",
        "child_name" to "Child's Name",
        "daily_goal_label" to "Daily Screen Time Limit (mins)",
        "save_profile" to "Save Profile",
        "manage_profiles" to "Currently Monitored Profiles",
        "remove" to "Delete",
        "local_backup" to "Local Database Control",
        "export_backup" to "Copy Database Export Code",
        "restore_backup" to "Restore Database from Code",
        "restore_hint" to "Paste exported JSON string here...",
        "restore_btn" to "Execute Restore",
        "language_sel" to "System Language",
        "theme_sel" to "Application Theme",
        "dark_mode" to "Dark Mode",
        "light_mode" to "Light Mode",
        "notifications" to "Real-time Push Reminders",
        "enabled" to "Active",
        "disabled" to "Muted",
        "privacy_policy" to "Privacy Policy & GDPR Compliance",
        "privacy_content" to "Smart Parent AI is a local application. We strictly gather ZERO cloud telemetry, password characters, keystrokes, social chat text, or financial digits. No adware, trackware, or remote servers block this architecture. Android Usage Access is queried exclusively through native platform APIs in compliance with Google Play Store Safety Regulations.",
        "wellbeing_tip" to "Daily Digital Wellness Tip",
        "unlocked_on" to "Unlocked on",
        "locked" to "Locked",
        "no_app_logs" to "No system log recordings found for this date. Check system settings or simulate an anomaly above!",
        "no_timeline" to "No timeline events detected yet.",
        "study_session_p" to "Study mode",
        "reading_session_p" to "Reading focus",
        "writing_session_p" to "Writing focus"
    )

    private val hi = mapOf(
        "dashboard" to "डैशबोर्ड",
        "app_rules" to "नियम",
        "focus_mode" to "फ़ोकस",
        "timeline" to "समयरेखा",
        "achievements" to "बैज",
        "settings" to "सेटिंग्स",
        "hello_parent" to "नमस्ते अभिभावक",
        "offline_notice" to "पूरी तरह से एन्क्रिप्टेड, ऑफ़लाइन मोड सक्रिय",
        "no_child" to "कोई चाइल्ड प्रोफ़ाइल सक्रिय नहीं है। नीचे एक बनाएं!",
        "goal_prog" to "आज की दैनिक सीमा प्रगति",
        "mins_used" to "मिनट उपयोग किए गए",
        "daily_goal" to "दैनिक लक्ष्य",
        "habit_score" to "डिजिटल आदत स्कोर",
        "excellent" to "शानदार",
        "moderate" to "मध्यम",
        "needs_work" to "सुधार की जरूरत",
        "study_vs_ent" to "पढ़ाई बनाम मनोरंजन",
        "app_ranking" to "स्क्रीन समय रैंकिंग",
        "top_used" to "सक्रिय ऐप्स",
        "least_used" to "कम सक्रिय ऐप्स",
        "offline_insights" to "स्मार्ट ऑफलाइन अंतर्दृष्टि",
        "monitored_apps" to "निगरानी और ऐप नियम",
        "all_categories" to "सभी श्रेणियां",
        "search_hint" to "इंस्टॉल किए गए ऐप्स खोजें...",
        "monitoring" to "निगरानी",
        "monitored" to "सक्रिय नियम",
        "unmonitored" to "उपेक्षित",
        "productivity_timer" to "उत्पादकता टाइमर",
        "focus_sub" to "पढ़ाई के सत्रों के दौरान मनोरंजन ऐप्स पर रोक लगाएं",
        "start_session" to "फ़ोकस सत्र शुरू करें",
        "cancel" to "सत्र रद्द करें",
        "session_active" to "फ़ोकस सत्र सक्रिय!",
        "focus_completed" to "फ़ोकस लक्ष्य पूरा हुआ!",
        "timeline_title" to "उपयोग समयरेखा",
        "timeline_sub" to "आज के ऐप्स का क्रोनोलॉजिकल ऑडिट",
        "inject_anomaly" to "सिम्युलेट यूसेज स्पाइक",
        "inject_desc" to "चार्ट में चेतावनी पैटर्न देखने के लिए अत्यधिक गेमप्ले और रात के डेटा को ट्रिगर करता है",
        "achievement_milestones" to "उपलब्धियां और बैज",
        "milestone_sub" to "स्वस्थ आदतों के निर्माण पर बच्चों को ट्राफियां मिलती हैं",
        "profile_mgt" to "पारिवारिक प्रोफ़ाइल और बैकअप",
        "add_child" to "नई चाइल्ड प्रोफाइल बनाएं",
        "child_name" to "बच्चे का नाम",
        "daily_goal_label" to "दैनिक स्क्रीन समय सीमा (मिनट)",
        "save_profile" to "प्रोफ़ाइल सहेजें",
        "manage_profiles" to "निगरानी में शामिल प्रोफ़ाइल",
        "remove" to "हटाएं",
        "local_backup" to "स्थानीय डेटाबेस नियंत्रण",
        "export_backup" to "डेटाबेस एक्सपोर्ट कॉपी करें",
        "restore_backup" to "कोड से डेटाबेस रीस्टोर करें",
        "restore_hint" to "एक्सपोर्ट किया गया JSON यहां पेस्ट करें...",
        "restore_btn" to "डेटा रीस्टोर करें",
        "language_sel" to "प्रणाली भाषा",
        "theme_sel" to "एप्लीकेशन थीम",
        "dark_mode" to "डार्क मोड",
        "light_mode" to "लाइट मोड",
        "notifications" to "पुश रिमाइंडर्स",
        "enabled" to "सक्रिय",
        "disabled" to "शांत",
        "privacy_policy" to "गोपनीयता नीति (Privacy Policy)",
        "privacy_content" to "स्मार्ट पेरेंट एआई पूरी तरह से एक ऑफलाइन एप्लीकेशन है। हम किसी भी क्लाउड सर्वर पर पासवर्ड, वित्तीय क्रेडेंशियल्स, चैट संदेश या व्यक्तिगत टेलीमेट्री स्टोर या साझा नहीं करते हैं। प्ले स्टोर सुरक्षा नियमों को सुनिश्चित करने के लिए केवल एंड्रॉइड के स्थानीय यूसेज एक्सेस एपीआई का उपयोग किया जाता है।",
        "wellbeing_tip" to "दैनिक डिजिटल वेलनेस टिप",
        "unlocked_on" to "अनलॉक किया गया:",
        "locked" to "ताला लगा है",
        "no_app_logs" to "इस तिथि के लिए कोई ऐप्स लॉग रिकॉर्ड नहीं मिला। ऊपर दिया गया 'सिम्युलेट यूसेज स्पाइक' बटन दबाएं!",
        "no_timeline" to "अभी तक कोई समयरेखा गति नहीं मिली है।",
        "study_session_p" to "पढ़ाई सत्र",
        "reading_session_p" to "पठन ध्यान",
        "writing_session_p" to "लेखन ध्यान"
    )

    fun t(key: String, lang: String): String {
        return if (lang == "hi") hi[key] ?: en[key] ?: key else en[key] ?: key
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainAppScreen(viewModel: ParentViewModel) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val areNotificationsEnabled by viewModel.areNotificationsEnabled.collectAsStateWithLifecycle()

    val children by viewModel.children.collectAsStateWithLifecycle()
    val selectedChildProfile by viewModel.selectedChildProfile.collectAsStateWithLifecycle()
    val monitoredApps by viewModel.monitoredApps.collectAsStateWithLifecycle()

    val todayUsageLogs by viewModel.todayUsageLogs.collectAsStateWithLifecycle()
    val allHistoryUsageLogs by viewModel.allHistoryUsageLogs.collectAsStateWithLifecycle()
    val todayTimelineEntries by viewModel.todayTimelineEntries.collectAsStateWithLifecycle()
    val focusSessionLogs by viewModel.focusSessionLogs.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()

    val activeDateStr by viewModel.selectedDate.collectAsStateWithLifecycle()

    val clipboardManager = LocalClipboardManager.current
    val localContext = LocalContext.current

    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    // Navigation setup
    var activeTab by remember { mutableStateOf("dashboard") }

    if (!isLoggedIn) {
        ParentAuthScreen(viewModel = viewModel)
    } else {
        Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                
                // Fine-grained Advertisement Sponsor info badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 2.dp, start = 12.dp, end = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SPONSOR ADVERTISEMENT",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        letterSpacing = 0.8.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(3.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "Ad",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Embedded Real Admob Banner
                com.example.ui.BannerAdView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                NavigationBar(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("app_bottom_nav_bar")
                ) {
                    val tabs = listOf(
                        Triple("dashboard", Icons.Filled.Dashboard, Dictionary.t("dashboard", language)),
                        Triple("rules", Icons.Filled.Block, Dictionary.t("app_rules", language)),
                        Triple("focus", Icons.Filled.Timer, Dictionary.t("focus_mode", language)),
                        Triple("timeline", Icons.Filled.History, Dictionary.t("timeline", language)),
                        Triple("achievements", Icons.Filled.EmojiEvents, Dictionary.t("achievements", language)),
                        Triple("settings", Icons.Filled.Settings, Dictionary.t("settings", language))
                    )

                    tabs.forEach { (tabId, icon, label) ->
                        val isActive = activeTab == tabId
                        NavigationBarItem(
                            selected = isActive,
                            onClick = { activeTab = tabId },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            modifier = Modifier.testTag("nav_tab_$tabId")
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Bar
            HeaderComponent(
                language = language,
                selectedChild = selectedChildProfile,
                children = children,
                onChildSelected = { id -> viewModel.selectChild(id) }
            )

            // Dynamic view based on tab
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { targetTab ->
                when (targetTab) {
                    "dashboard" -> DashboardTab(
                        language = language,
                        selectedChild = selectedChildProfile,
                        todayUsageLogs = todayUsageLogs,
                        allLogs = allHistoryUsageLogs,
                        viewModel = viewModel
                    )
                    "rules" -> RulesTab(
                        language = language,
                        monitoredApps = monitoredApps,
                        viewModel = viewModel,
                        onConfigChanged = { pkg, label, cat, active ->
                            viewModel.setMonitoringStatus(pkg, label, cat, active)
                        }
                    )
                    "focus" -> FocusTab(
                        language = language,
                        selectedChild = selectedChildProfile,
                        viewModel = viewModel,
                        focusSessionLogs = focusSessionLogs
                    )
                    "timeline" -> TimelineTab(
                        language = language,
                        selectedChild = selectedChildProfile,
                        timelineEntries = todayTimelineEntries,
                        viewModel = viewModel
                    )
                    "achievements" -> AchievementsTab(
                        language = language,
                        selectedChild = selectedChildProfile,
                        achievementsList = achievements
                    )
                    "settings" -> SettingsTab(
                        language = language,
                        selectedChild = selectedChildProfile,
                        children = children,
                        isDarkTheme = isDarkTheme,
                        areNotificationsEnabled = areNotificationsEnabled,
                        viewModel = viewModel,
                        onBackupExport = {
                            viewModel.backupToClipboard { json ->
                                clipboardManager.setText(AnnotatedString(json))
                                Toast.makeText(localContext, "Backup copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onRestoreImport = { json ->
                            viewModel.restoreFromBackupString(json) { success ->
                                if (success) {
                                    Toast.makeText(localContext, "Restore Completed Successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(localContext, "Invalid Backup Code", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
}

// --- COMMON DUAL HEADER COMPONENT ---
@Composable
fun HeaderComponent(
    language: String,
    selectedChild: ChildProfile?,
    children: List<ChildProfile>,
    onChildSelected: (Long) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Smart Parent AI",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("app_logo_title")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = "Safe Shield",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = Dictionary.t("offline_notice", language),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                // Small offline status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSystemInDarkTheme()) Color(0xFF1B5E20) else Color(0xFFE8F5E9))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OFFLINE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSystemInDarkTheme()) Color(0xFF81C784) else Color(0xFF10B981)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Children profile visual horizontal list
            if (children.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    children.forEach { child ->
                        val isSelected = selectedChild?.id == child.id
                        InputChip(
                            selected = isSelected,
                            onClick = { onChildSelected(child.id) },
                            label = { Text("${child.avatarEmoji}  ${child.name}", fontWeight = FontWeight.SemiBold) },
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("child_chip_${child.name}")
                        )
                    }
                }
            } else {
                Text(
                    text = Dictionary.t("no_child", language),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        }
    }
}

// ==========================================
// ====== TAB 1: DASHBOARD PAGE ===============
// ==========================================
@Composable
fun DashboardTab(
    language: String,
    selectedChild: ChildProfile?,
    todayUsageLogs: List<AppUsageLog>,
    allLogs: List<AppUsageLog>,
    viewModel: ParentViewModel
) {
    if (selectedChild == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.People,
                    contentDescription = "No profile active",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = Dictionary.t("no_child", language),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (language == "hi") 
                        "कृपया सेटिंग्स (गियर आइकन⚙️) टैब में जाकर पहले बच्चे की प्रोफ़ाइल जोड़ें ताकि आप लाइव डैशबोर्ड देख सकें।" 
                        else "Please visit Settings (gear tab⚙️) to register a child profile to enable live analytics.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
        return
    }

    val screenGoal = selectedChild.dailyGoalMinutes
    val totalTimeUsed = todayUsageLogs.sumOf { it.durationMinutes }
    val progressRatio = if (screenGoal > 0) totalTimeUsed.toFloat() / screenGoal.toFloat() else 0f
    
    val habitScore = viewModel.calculateHabitScore(todayUsageLogs, screenGoal)
    val insights = viewModel.getOfflineInsights(todayUsageLogs, selectedChild)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Goal Progress card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = Dictionary.t("goal_prog", language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Custom Circular Progress Ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(170.dp)
                ) {
                    val anglePercentage = Math.min(1.0f, progressRatio)
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)

                    Canvas(modifier = Modifier.size(150.dp)) {
                        drawArc(
                            color = trackColor,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = primaryColor,
                            startAngle = -90f,
                            sweepAngle = anglePercentage * 360f,
                            useCenter = false,
                            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalTimeUsed",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = Dictionary.t("mins_used", language),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Limits progress numeric labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Used: $totalTimeUsed mins",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${Dictionary.t("daily_goal", language)}: $screenGoal mins",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Digital Habit Score & Tip Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(115.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = Dictionary.t("habit_score", language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$habitScore",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    
                    val ratingLabel = when {
                        habitScore >= 85 -> Dictionary.t("excellent", language)
                        habitScore >= 60 -> Dictionary.t("moderate", language)
                        else -> Dictionary.t("needs_work", language)
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = ratingLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Wellness Tip Card
            Card(
                modifier = Modifier
                    .weight(1.2f)
                    .height(115.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSystemInDarkTheme()) Color(0xFF2C2215) else Color(0xFFFFFBEB)
                ),
                border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF3E2C15) else Color(0xFFFEF3C7))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = Dictionary.t("wellbeing_tip", language),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSystemInDarkTheme()) Color(0xFFFBBF24) else Color(0xFFB45309)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Simple deterministic daily tip selection based on profile ID sum
                    val tipIndex = (selectedChild.id.toInt() + totalTimeUsed.toInt() / 15) % viewModel.wellnessTips.size
                    Text(
                        text = viewModel.wellnessTips[Math.abs(tipIndex)],
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSystemInDarkTheme()) Color(0xFFFCD34D) else Color(0xFF92400E),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // --- STUDY VS ENTERTAINMENT ANALYSIS PIE ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = Dictionary.t("study_vs_ent", language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (todayUsageLogs.isNotEmpty()) {
                    val categorySums = todayUsageLogs.groupBy { it.categoryName }
                        .mapValues { (_, list) -> list.sumOf { it.durationMinutes } }

                    val totalUsage = categorySums.values.sum().toFloat()

                    // Category Colors lookup
                    val catColors = mapOf(
                        "Education" to MaterialTheme.colorScheme.primary, // Vibrant Indigo
                        "Games" to Color(0xFFFB923C), // Vibrant Orange
                        "Social Media" to Color(0xFFEC4899), // Pink
                        "Entertainment" to Color(0xFFF59E0B), // Vibrant Amber
                        "Productivity" to Color(0xFF10B981), // Vibrant Emerald
                        "Other" to Color(0xFF64748B)  // Vibrant Slate
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Drawing Pie Canvas dynamically
                        Canvas(modifier = Modifier.size(110.dp)) {
                            var startAngle = 0f
                            categorySums.forEach { (cat, mins) ->
                                val sweep = (mins.toFloat() / totalUsage) * 360f
                                drawArc(
                                    color = catColors[cat] ?: Color.Gray,
                                    startAngle = startAngle,
                                    sweepAngle = sweep,
                                    useCenter = true,
                                    size = size
                                )
                                startAngle += sweep
                            }
                        }

                        // Legends column
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            categorySums.entries.sortedByDescending { it.value }.take(4).forEach { (cat, mins) ->
                                val percentage = ((mins.toFloat() / totalUsage) * 100).toInt()
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(catColors[cat] ?: Color.Gray)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "$cat: $percentage%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = Dictionary.t("no_app_logs", language),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // --- RANKINGS ENGINE ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = Dictionary.t("app_ranking", language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (todayUsageLogs.isNotEmpty()) {
                    val sortedLogs = todayUsageLogs.sortedByDescending { it.durationMinutes }

                    Text(
                        text = Dictionary.t("top_used", language).uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    // Top 3
                    val topUsedLogs = sortedLogs.take(3)
                    for (i in topUsedLogs.indices) {
                        val log = topUsedLogs[i]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${i + 1}.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.width(20.dp)
                                )
                                Text(
                                    text = log.appName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${log.categoryName})",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            Text(
                                text = "${log.durationMinutes} mins",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = Dictionary.t("least_used", language).uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    // Least 2
                    val leastUsedLogs = sortedLogs.reversed().take(2)
                    for (i in leastUsedLogs.indices) {
                        val log = leastUsedLogs[i]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${i + 1}.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.width(20.dp)
                                )
                                Text(
                                    text = log.appName,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "${log.durationMinutes} min",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Text(
                        text = Dictionary.t("no_app_logs", language),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        // --- OFFLINE SMART INSIGHTS ALERTS ENGINE ---
        Text(
            text = Dictionary.t("offline_insights", language),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        insights.forEach { insight ->
            val cardBg = when (insight.type) {
                InsightType.GOOD -> Color(0xFFE8F5E9)
                InsightType.ALERT -> Color(0xFFFFEBEE)
                InsightType.TIP -> Color(0xFFE0F7FA)
                InsightType.INFO -> Color(0xFFF1F1F1)
            }
            val titleColor = when (insight.type) {
                InsightType.GOOD -> Color(0xFF2E7D32)
                InsightType.ALERT -> Color(0xFFC62828)
                InsightType.TIP -> Color(0xFF00838F)
                InsightType.INFO -> Color(0xFF424242)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = insight.iconEmoji,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = insight.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = insight.description,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// ====== TAB 2: INSTALLED APPS LIMITS ========
// ==========================================
@Composable
fun RulesTab(
    language: String,
    monitoredApps: List<MonitoredApp>,
    viewModel: ParentViewModel,
    onConfigChanged: (packageName: String, label: String, category: String, isMonitored: Boolean) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryTab by remember { mutableStateOf(Dictionary.t("all_categories", language)) }

    val categoriesList = listOf(
        Dictionary.t("all_categories", language),
        "Education",
        "Games",
        "Social Media",
        "Entertainment",
        "Productivity",
        "Other"
    )

    val filteredApps = monitoredApps.filter { app ->
        val matchesSearch = app.appName.lowercase().contains(searchQuery.lowercase()) ||
                app.packageName.lowercase().contains(searchQuery.lowercase())
        
        val matchesCategory = selectedCategoryTab == Dictionary.t("all_categories", language) ||
                app.categoryName.equals(selectedCategoryTab, ignoreCase = true)
        
        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = Dictionary.t("monitored_apps", language),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // --- REAL ON-DEVICE SCANNING DASHBOARD CARD ---
        val isScanning by viewModel.isScanningApps.collectAsStateWithLifecycle()
        val localContext = androidx.compose.ui.platform.LocalContext.current

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("on_device_scan_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left info section
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Android,
                            contentDescription = "On device",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "hi") "मल्टी-चैनल ऐप डिटेक्शन" else "Multi-Channel App Detection",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (language == "hi") 
                            "इस डिवाइस के सारे वास्तविक व सिस्टम स्थापित ऐप्स (जैसे Chrome, YouTube, Whatsapp) को स्कैन करके जोड़ें।" 
                            else "Identify and sync all native and system packages from this target device into rule engine.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 13.sp
                    )
                }

                // Scan Action Trigger / Spinner
                if (isScanning) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == "hi") "स्कैन जारी है..." else "Scanning...",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.scanOnDeviceApps()
                            Toast.makeText(
                                localContext,
                                if (language == "hi") "डिवाइस के सभी स्थापित ऐप्स सफलतापूर्वक सिंक कर दिए गए!" else "All installed on-device apps synchronized and loaded!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("trigger_package_scan_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Sync, contentDescription = "Sync", modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == "hi") "स्कैन करें" else "Scan Apps",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Search text field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(Dictionary.t("search_hint", language), fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("app_search_field"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Categories horizontal scroll chip bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categoriesList.forEach { category ->
                val isSelected = selectedCategoryTab == category
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryTab = category },
                    label = { Text(category, fontSize = 12.sp) },
                    modifier = Modifier.testTag("category_chip_$category")
                )
            }
        }

        // List of Apps
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 1.dp
        ) {
            if (filteredApps.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        val appLimitMins = viewModel.getAppLimitMinutes(app.packageName)
                        val limitsTrigger by viewModel.appLimitsUpdated.collectAsStateWithLifecycle()

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Dynamic App Emojis based on category
                                    val emoji = when (app.categoryName) {
                                        "Education" -> "📚"
                                        "Games" -> "🎮"
                                        "Social Media" -> "💬"
                                        "Entertainment" -> "🍿"
                                        "Productivity" -> "⏱️"
                                        else -> "📱"
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(emoji, fontSize = 20.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = app.appName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = app.categoryName,
                                                    fontSize = 9.sp,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                // LIMIT MONITOR SWITCH
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (app.isMonitored) Dictionary.t("monitored", language) else Dictionary.t("unmonitored", language),
                                        fontSize = 11.sp,
                                        color = if (app.isMonitored) MaterialTheme.colorScheme.primary else Color.Gray,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Switch(
                                        checked = app.isMonitored,
                                        onCheckedChange = { isChecked ->
                                            onConfigChanged(app.packageName, app.appName, app.categoryName, isChecked)
                                        },
                                        modifier = Modifier.scale(0.85f).testTag("app_switch_${app.appName}")
                                    )
                                }
                            }

                            // --- INDIVIDUAL APP LIMIT CONTROL ---
                            if (app.isMonitored) {
                                var tempMins by remember(app.packageName, appLimitMins, limitsTrigger) {
                                    mutableStateOf(appLimitMins.toFloat())
                                }

                                androidx.compose.material3.HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = if (language == "hi") "व्यक्तिगत दैनिक सीमा" else "App Daily Limit",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = if (language == "hi") "${tempMins.toInt()} मिनट" else "${tempMins.toInt()} minutes",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    // Quick preset chips
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        listOf(15, 30, 60, 120).forEach { pr ->
                                            val isAct = tempMins.toInt() == pr
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(
                                                        if (isAct) MaterialTheme.colorScheme.primary 
                                                        else MaterialTheme.colorScheme.surfaceVariant
                                                    )
                                                    .clickable {
                                                        tempMins = pr.toFloat()
                                                        viewModel.setAppLimitMinutes(app.packageName, pr)
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "$pr m",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isAct) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }

                                Slider(
                                    value = tempMins,
                                    onValueChange = { tempMins = it },
                                    onValueChangeFinished = {
                                        viewModel.setAppLimitMinutes(app.packageName, tempMins.toInt())
                                    },
                                    valueRange = 5f..180f,
                                    steps = 34, // intervals of 5 minutes: (180 - 5) / 5 - 1 = 34 steps
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("app_limit_slider_${app.packageName}")
                                )
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No matching packages.", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }
    }
}

// ==========================================
// ====== TAB 3: FOCUS MODE TIMER ============
// ==========================================
@Composable
fun FocusTab(
    language: String,
    selectedChild: ChildProfile?,
    viewModel: ParentViewModel,
    focusSessionLogs: List<FocusSessionLog>
) {
    if (selectedChild == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(Dictionary.t("no_child", language), color = Color.Gray)
        }
        return
    }

    val isRunning by viewModel.isFocusTimerRunning.collectAsStateWithLifecycle()
    val secondsRemaining by viewModel.focusTimeRemainingSeconds.collectAsStateWithLifecycle()
    val activeFocusCategory by viewModel.selectedFocusDurationCategory.collectAsStateWithLifecycle()

    val formattedTime = remember(secondsRemaining) {
        val mins = secondsRemaining / 60
        val secs = secondsRemaining % 60
        String.format("%02d:%02d", mins, secs)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = Dictionary.t("productivity_timer", language),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = Dictionary.t("focus_sub", language),
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Session select cards
        if (!isRunning) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sessions = listOf(
                    Triple(Dictionary.t("study_session_p", language), 25, "✏️"),
                    Triple(Dictionary.t("reading_session_p", language), 15, "📖"),
                    Triple(Dictionary.t("writing_session_p", language), 45, "📝")
                )

                sessions.forEach { (name, mins, emoji) ->
                    val localFocusContext = androidx.compose.ui.platform.LocalContext.current
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val activity = localFocusContext as? android.app.Activity
                                if (activity != null && com.example.ui.AdMobManager.isInterstitialReady()) {
                                    com.example.ui.AdMobManager.showInterstitial(activity) {
                                        viewModel.startFocusTimer(mins, name)
                                    }
                                } else {
                                    viewModel.startFocusTimer(mins, name)
                                }
                            }
                            .testTag("focus_preset_$mins"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Text(
                                text = "$mins mins",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // --- CUSTOM FOCUS DURATION SELECTOR (Set Custom Time) ---
            var customFocusMinutes by remember { mutableStateOf(30f) }
            val localFocusContext = androidx.compose.ui.platform.LocalContext.current

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("custom_focus_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == "hi") "अपना फोकस समय सेट करें ⏱️" else "Set Custom Focus Time ⏱️",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${customFocusMinutes.toInt()} mins",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Slider(
                        value = customFocusMinutes,
                        onValueChange = { customFocusMinutes = it },
                        valueRange = 5f..120f,
                        steps = 22, // steps of 5 mins: (120 - 5) / 5 = 23 ticks total (steps = 22 inside)
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_focus_slider")
                    )
                    
                    Button(
                        onClick = {
                            val mins = customFocusMinutes.toInt()
                            val name = if (language == "hi") "कस्टम ध्यान सत्र" else "Custom Session"
                            val activity = localFocusContext as? android.app.Activity
                            if (activity != null && com.example.ui.AdMobManager.isInterstitialReady()) {
                                com.example.ui.AdMobManager.showInterstitial(activity) {
                                    viewModel.startFocusTimer(mins, name)
                                }
                            } else {
                                viewModel.startFocusTimer(mins, name)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .testTag("start_custom_focus_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = if (language == "hi") "कस्टम फोकस शुरू करें 🚀" else "Start Custom Focus 🚀",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Active Timer Ring Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isRunning) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isRunning) "🌱 $activeFocusCategory" else "💤 Idle Timer",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRunning) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(160.dp)
                ) {
                    Canvas(modifier = Modifier.size(140.dp)) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.5f),
                            radius = size.minDimension / 2,
                            style = Stroke(width = 8.dp.toPx())
                        )
                    }
                    Text(
                        text = formattedTime,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isRunning) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.testTag("timer_text")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isRunning) {
                    Button(
                        onClick = { viewModel.stopFocusTimer() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.testTag("cancel_timer_btn")
                    ) {
                        Text(Dictionary.t("cancel", language))
                    }
                } else {
                    Button(
                        onClick = { viewModel.startFocusTimer(25, "Study Mode") },
                        modifier = Modifier.testTag("start_timer_btn")
                    ) {
                        Text(Dictionary.t("start_session", language))
                    }
                }
            }
        }

        // Focus Session History List
        Text(
            text = "Completed Focus Records",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 1.dp
        ) {
            if (focusSessionLogs.isNotEmpty()) {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(focusSessionLogs) { log ->
                        val dateFormatted = remember(log.timestamp) {
                            val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                            sdf.format(Date(log.timestamp))
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8F5E9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = "Done", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(log.sessionName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(dateFormatted, fontSize = 9.sp, color = Color.Gray)
                                }
                            }
                            Text(
                                text = "+${log.durationMinutes} mins",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No sessions completed yet.", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

// ==========================================
// ====== TAB 4: TIMELINE LIST =============
// ==========================================
@Composable
fun TimelineTab(
    language: String,
    selectedChild: ChildProfile?,
    timelineEntries: List<TimelineEntry>,
    viewModel: ParentViewModel
) {
    val localContext = LocalContext.current
    if (selectedChild == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(Dictionary.t("no_child", language), color = Color.Gray)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = Dictionary.t("timeline_title", language),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = Dictionary.t("timeline_sub", language),
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // QUICK ANOMALY BUTTON
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = Dictionary.t("inject_anomaly", language),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = Dictionary.t("inject_desc", language),
                        fontSize = 10.sp,
                        lineHeight = 13.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    )
                }
                IconButton(
                    onClick = {
                        viewModel.injectSimulatedUsageAnomaly(selectedChild.id)
                        Toast.makeText(localContext, "Simulated usage peak injected!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .testTag("simulate_anomaly_btn")
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = "Simulate Anomaly", tint = Color.White)
                }
            }
        }

        // Timeline items
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 1.dp
        ) {
            if (timelineEntries.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(timelineEntries) { entry ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Timeline visual line dot
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(45.dp)
                                        .background(Color.LightGray)
                                )
                            }

                            Column {
                                Text(
                                    text = entry.timeSlot,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = entry.appName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = entry.categoryName,
                                            fontSize = 8.sp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(Dictionary.t("no_timeline", language), color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

// ==========================================
// ====== TAB 5: ACHIEVEMENTS / BADGES =======
// ==========================================
@Composable
fun AchievementsTab(
    language: String,
    selectedChild: ChildProfile?,
    achievementsList: List<Achievement>
) {
    if (selectedChild == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(Dictionary.t("no_child", language), color = Color.Gray)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = Dictionary.t("achievement_milestones", language),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = Dictionary.t("milestone_sub", language),
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Grid layout representation
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(achievementsList) { ach ->
                val cardAlpha = if (ach.isUnlocked) 1.0f else 0.45f
                val cardColor = if (ach.isUnlocked) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("achievement_card_${ach.id}"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor.copy(alpha = cardAlpha)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge circular container
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (ach.isUnlocked) Color(0xFFFFD700) else Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (ach.isUnlocked) ach.iconEmoji else "🔒",
                                fontSize = 28.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = ach.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (ach.isUnlocked) MaterialTheme.colorScheme.onSecondaryContainer else Color.Gray
                            )
                            Text(
                                text = ach.description,
                                fontSize = 11.sp,
                                color = if (ach.isUnlocked) Color.DarkGray else Color.Gray,
                                lineHeight = 14.sp
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (ach.isUnlocked) "${Dictionary.t("unlocked_on", language)} ${ach.dateUnlocked}" else Dictionary.t("locked", language),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (ach.isUnlocked) Color(0xFF4CAF50) else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// ====== TAB 6: SETTINGS & BACKUP ==========
// ==========================================
@Composable
fun SettingsTab(
    language: String,
    selectedChild: ChildProfile?,
    children: List<ChildProfile>,
    isDarkTheme: Boolean,
    areNotificationsEnabled: Boolean,
    viewModel: ParentViewModel,
    onBackupExport: () -> Unit,
    onRestoreImport: (String) -> Unit
) {
    val localContext = LocalContext.current
    var backupBoxText by remember { mutableStateOf("") }
    var childNameState by remember { mutableStateOf("") }
    var childGoalLimitMinutes by remember { mutableStateOf("120") }
    var childToEdit by remember { mutableStateOf<ChildProfile?>(null) }

    val scrollState = rememberScrollState()

    // --- EDIT CHILD PROFILE DIALOG ---
    if (childToEdit != null) {
        val targetProfile = childToEdit!!
        var editName by remember(targetProfile) { mutableStateOf(targetProfile.name) }
        var editLimitMinutes by remember(targetProfile) { mutableStateOf(targetProfile.dailyGoalMinutes.toString()) }

        AlertDialog(
            onDismissRequest = { childToEdit = null },
            title = {
                Text(
                    text = if (language == "hi") "प्रोफ़ाइल समय सीमा बदलें 👤" else "Edit Profile screen-time 👤",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(
                        text = if (language == "hi") "बच्चे का नाम" else "Child's Name",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        modifier = Modifier.fillMaxWidth().testTag("edit_child_name_field"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (language == "hi") "दैनिक स्क्रीन समय सीमा (मिनट में)" else "Daily Screen Limit (in minutes)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editLimitMinutes,
                        onValueChange = { editLimitMinutes = it },
                        modifier = Modifier.fillMaxWidth().testTag("edit_child_limit_field"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newName = editName.trim()
                        val newLimit = editLimitMinutes.toIntOrNull() ?: 120
                        if (newName.isNotEmpty()) {
                            viewModel.updateChild(targetProfile.copy(name = newName, dailyGoalMinutes = newLimit))
                            childToEdit = null
                            Toast.makeText(localContext, "Profile limit updated!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("save_edit_child_limit_btn")
                ) {
                    Text(if (language == "hi") "सहेजें" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { childToEdit = null }) {
                    Text(if (language == "hi") "रद्द करें" else "Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // --- CHILD PROFILE CREATION HUB ---
        Text(
            text = Dictionary.t("profile_mgt", language),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = Dictionary.t("add_child", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = childNameState,
                    onValueChange = { childNameState = it },
                    placeholder = { Text(Dictionary.t("child_name", language), fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .testTag("dialog_child_name"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = childGoalLimitMinutes,
                    onValueChange = { childGoalLimitMinutes = it },
                    placeholder = { Text(Dictionary.t("daily_goal_label", language), fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("dialog_child_goal"),
                    shape = RoundedCornerShape(8.dp)
                )

                Button(
                    onClick = {
                        val name = childNameState.trim()
                        val mLimit = childGoalLimitMinutes.toIntOrNull() ?: 120
                        if (name.isNotEmpty()) {
                            // select deterministic cute emoji
                            val cuteEmojis = listOf("👦", "🧒", "👧", "👶", "🐼", "🦊", "🦁")
                            val selectedEmoji = cuteEmojis[children.size % cuteEmojis.size]
                            
                            viewModel.createChild(name, selectedEmoji, mLimit)
                            childNameState = ""
                            Toast.makeText(localContext, "Profile created!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_child_btn")
                ) {
                    Text(Dictionary.t("save_profile", language))
                }
            }
        }

        // --- MANAGED CHILDREN LIST ---
        if (children.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = Dictionary.t("manage_profiles", language),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    children.forEach { child ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(child.avatarEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(child.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Limit: ${child.dailyGoalMinutes} mins", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { childToEdit = child },
                                    modifier = Modifier.testTag("edit_child_${child.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Edit Profile Limit",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { viewModel.deleteChild(child.id) },
                                    modifier = Modifier.testTag("delete_child_${child.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- PREFERENCES (Language, Theme, Notifications) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Language selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Dictionary.t("language_sel", language),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row {
                        FilterChip(
                            selected = language == "en",
                            onClick = { viewModel.setLanguage("en") },
                            label = { Text("EN", fontSize = 10.sp) },
                            modifier = Modifier.testTag("lang_en_chip")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(
                            selected = language == "hi",
                            onClick = { viewModel.setLanguage("hi") },
                            label = { Text("हिन्दी", fontSize = 10.sp) },
                            modifier = Modifier.testTag("lang_hi_chip")
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 10.dp))

                // Dark/Light Theme selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Dictionary.t("theme_sel", language),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.testTag("theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 10.dp))

                // Notifications Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Dictionary.t("notifications", language),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (areNotificationsEnabled) Dictionary.t("enabled", language) else Dictionary.t("disabled", language),
                            fontSize = 11.sp,
                            color = if (areNotificationsEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Switch(
                            checked = areNotificationsEnabled,
                            onCheckedChange = { viewModel.toggleNotifications() },
                            modifier = Modifier.scale(0.8f).testTag("notif_toggle_switch")
                        )
                    }
                }
            }
        }

        // --- ADMOB SPONSOR AD SUPPORT CARD ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSystemInDarkTheme()) Color(0xFF1E1B4B) else Color(0xFFEEF2FF)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SUPPORT APPLICATION (ADMOB SPONSOR)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Smart Parent AI is offline-first & free. Tap below to view a sponsor interstitial ad. Your voluntary views directly support our continuous updates and feature development!",
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val activity = localContext as? android.app.Activity
                        if (activity != null) {
                            com.example.ui.AdMobManager.showInterstitial(activity) {
                                Toast.makeText(localContext, "Thank you for supporting Smart Parent AI!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("show_interstitial_ad_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    val isReady = com.example.ui.AdMobManager.isInterstitialReady()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "💝 Support us & Watch Sponsor Ad", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        if (!isReady) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "(Loading...)", fontSize = 9.sp, fontWeight = FontWeight.Normal, color = Color.White.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }

        // --- PARENT SECURITY & GATE SESSION ---
        val parentName by viewModel.parentName.collectAsStateWithLifecycle()
        val parentEmail by viewModel.parentEmail.collectAsStateWithLifecycle()
        val requireLoginOnStartup by viewModel.requireLoginOnStartup.collectAsStateWithLifecycle()

        Text(
            text = if (language == "hi") "अभिभावक सुरक्षा और लॉगिन" else "Parent Security & Session",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Security Security Gate",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "hi") "सुरक्षित अभिभावक सत्र नियंत्रित करें" else "Secure Parent Gate Settings",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${if (language == "hi") "अभिभावक नाम:" else "Logged In As:"} $parentName",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${if (language == "hi") "पंजीकृत ईमेल:" else "Registered Email:"} $parentEmail",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == "hi") "स्टार्टअप पर लॉगिन आवश्यक करें" else "Require Login on Startup",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (language == "hi") "सुरक्षा के लिए ऐप को पुनः चालू करते समय पासवर्ड पूछें" else "Force parent login on cold start for childproof security",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            lineHeight = 12.sp
                        )
                    }
                    Switch(
                        checked = requireLoginOnStartup,
                        onCheckedChange = { viewModel.setRequireLoginOnStartup(it) },
                        modifier = Modifier.scale(0.8f).testTag("require_login_on_startup_switch")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.logoutParent()
                        Toast.makeText(localContext, if (language == "hi") "सफलतापूर्वक लॉग आउट और डैशबोर्ड सुरक्षित किया गया!" else "Successfully logged out & dashboard locked!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parent_logout_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = "Log Out")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (language == "hi") "सुरक्षित अभिभावक मोड लॉक करें" else "Lock & Log Out Parent", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- BACKUP & RESTORE PANEL ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Dictionary.t("local_backup", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onBackupExport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = "Export")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Dictionary.t("export_backup", language), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = Dictionary.t("restore_backup", language),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = backupBoxText,
                    onValueChange = { backupBoxText = it },
                    placeholder = { Text(Dictionary.t("restore_hint", language), fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("restore_text_field"),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val textToRestore = backupBoxText.trim()
                        if (textToRestore.isNotEmpty()) {
                            onRestoreImport(textToRestore)
                            backupBoxText = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("restore_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    enabled = backupBoxText.isNotEmpty()
                ) {
                    Text(Dictionary.t("restore_btn", language), fontSize = 12.sp)
                }
            }
        }

        // --- DEMO PLAYGROUND MODE COLD SEEDER ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Demo Mode",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "hi") "डेमो मोड (सैंडबॉक्स सिमुलेटर)" else "Demo Playground (Sandbox Simulator)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (language == "hi")
                        "एक क्लिक में डेमो डेटा लोड करें (जैसे बच्चा 'राहुल', 30 दिनों का ग्राफिकल विश्लेषण रिकॉर्ड, और लक्ष्य उपलब्धियां) ताकि आप ऐप की सभी विशेषताओं और चार्ट्स का अनुभव कर सकें।"
                        else "Automatically load demo statistics instantly (includes profile 'Rahul', 30 days of synthetic logs, achievements, and timelines) to preview charts/analytics features on a clean device.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.seedDemoData {
                            Toast.makeText(
                                localContext,
                                if (language == "hi") "डेमो बच्चे राहुल की 30 दिन की रिपोर्ट सफलतापूर्वक लोड की गई!" else "Successfully loaded 30 days of data for demo child Rahul!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("seed_demo_data_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "Seed Demo Data",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == "hi") "डेमो डेटा लोड करें ✨" else "Load Demo Statistics ✨",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- OFFLINE PRIVACY AND REGULATORY COMPLIANCE PANEL ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.VerifiedUser, contentDescription = "GDPR Shield", tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Dictionary.t("privacy_policy", language),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = Dictionary.t("privacy_content", language),
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ParentAuthScreen(viewModel: ParentViewModel) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val hasParentAccount by viewModel.hasParentAccount.collectAsStateWithLifecycle()
    val localContext = LocalContext.current

    var isLoginMode by remember(hasParentAccount) { mutableStateOf(hasParentAccount) }

    // State inputs
    var nameState by remember { mutableStateOf("") }
    var emailState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }
    var confirmPasswordState by remember { mutableStateOf("") }
    var recoveryState by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val bgBrush = if (isSystemInDarkTheme()) {
        Brush.verticalGradient(colors = listOf(Color(0xFF0B0F19), Color(0xFF1E1B4B)))
    } else {
        Brush.verticalGradient(colors = listOf(Color(0xFFF5F3FF), Color(0xFFEEF2FF)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
            .windowInsetsPadding(WindowInsets.safeContent)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .testTag("auth_card"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Shield / Logo Container
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isLoginMode) Icons.Filled.Lock else Icons.Filled.VerifiedUser,
                        contentDescription = "Safe Lock Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = if (isLoginMode) "Smart Parent AI Login" else "Create Parent Account",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = if (isLoginMode) {
                        if (language == "hi") "अपने बच्चों की गतिविधि को सुरक्षित करने के लिए लॉगिन करें" else "Securely access parental controls & usage statistics"
                    } else {
                        if (language == "hi") "ऐप सीमाओं को बदलने से बच्चों को रोकने के लिए पासवर्ड बनाएं" else "Establish a passcode lock to childproof this dashboard"
                    },
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Toggle tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        .padding(4.dp)
                ) {
                    Button(
                        onClick = { 
                            isLoginMode = true 
                            errorMsg = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLoginMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (isLoginMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("select_login_tab_btn"),
                        elevation = null,
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (language == "hi") "लॉगिन" else "Lock Login", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { 
                            isLoginMode = false 
                            errorMsg = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isLoginMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (!isLoginMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("select_register_tab_btn"),
                        elevation = null,
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (language == "hi") "खाता खोलें" else "Register Parent", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Inline error display
                if (errorMsg != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = errorMsg!!, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 10.sp, lineHeight = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Input fields
                if (!isLoginMode) {
                    // Registration Mode inputs
                    OutlinedTextField(
                        value = nameState,
                        onValueChange = { 
                            nameState = it 
                            errorMsg = null
                        },
                        label = { Text(if (language == "hi") "अभिभावक नाम" else "Parent Name", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Name Icon", modifier = Modifier.size(18.dp)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("reg_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = emailState,
                    onValueChange = { 
                        emailState = it 
                        errorMsg = null
                    },
                    label = { Text(if (language == "hi") "ईमेल पता" else "Parent Email", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email Icon", modifier = Modifier.size(18.dp)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("auth_email_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = passwordState,
                    onValueChange = { 
                        passwordState = it 
                        errorMsg = null
                    },
                    label = { Text(if (language == "hi") "पासवर्ड" else "Parent Password", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password Icon", modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (!isLoginMode) 12.dp else 20.dp)
                        .testTag("auth_password_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (!isLoginMode) {
                    OutlinedTextField(
                        value = confirmPasswordState,
                        onValueChange = { 
                            confirmPasswordState = it 
                            errorMsg = null
                        },
                        label = { Text(if (language == "hi") "पासवर्ड की पुष्टि करें" else "Confirm Password", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Confirm Icon", modifier = Modifier.size(18.dp)) },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("reg_confirm_password_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = recoveryState,
                        onValueChange = { 
                            recoveryState = it 
                            errorMsg = null
                        },
                        label = { Text(if (language == "hi") "पुनर्प्राप्ति गुप्त शब्द (उदाहरण: मनपसंद रंग)" else "Recovery Secret Answer (e.g. Favorite Color)", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Filled.Help, contentDescription = "Recovery Icon", modifier = Modifier.size(18.dp)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                            .testTag("reg_recovery_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                // Action primary button
                Button(
                    onClick = {
                        val email = emailState.trim()
                        val password = passwordState.trim()
                        if (isLoginMode) {
                            if (email.isEmpty() || password.isEmpty()) {
                                errorMsg = if (language == "hi") "कृपया सभी फ़ील्ड भरें" else "Please satisfy all credentials"
                                return@Button
                            }
                            val success = viewModel.loginParent(email, password)
                            if (success) {
                                Toast.makeText(localContext, if (language == "hi") "लॉगिन सफल! स्वागत है" else "Parent credentials verified!", Toast.LENGTH_SHORT).show()
                            } else {
                                errorMsg = if (language == "hi") "अमान्य ईमेल या पासवर्ड। कृपया जाँचें।" else "Email or password incorrect. Try preset accounts below!"
                            }
                        } else {
                            val name = nameState.trim()
                            val confirm = confirmPasswordState.trim()
                            val recVal = recoveryState.trim()
                            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                                errorMsg = if (language == "hi") "कृपया सभी फ़ील्ड भरें" else "Please satisfy all credentials"
                                return@Button
                            }
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                errorMsg = if (language == "hi") "अमान्य ईमेल पता प्रारूप" else "Invalid email address format"
                                return@Button
                            }
                            if (password.length < 6) {
                                errorMsg = if (language == "hi") "पासवर्ड कम से कम 6 अक्षरों का होना चाहिए" else "Password must be at least 6 characters in length"
                                return@Button
                            }
                            if (password != confirm) {
                                errorMsg = if (language == "hi") "पासवर्ड मेल नहीं खाते" else "Passwords do not match"
                                return@Button
                            }
                            viewModel.registerParent(name, email, password)
                            Toast.makeText(localContext, if (language == "hi") "पंजीकरण सफल!" else "Parent account registered!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("auth_submit_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isLoginMode) (if (language == "hi") "डैशबोर्ड अनलॉक करें" else "Unlock Dashboard") else (if (language == "hi") "खाता बनाएं और लॉगिन करें" else "Create Parent Account"),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Preset demonstrator credentials helper grid (Beautiful visual cards)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (language == "hi") "त्वरित परीक्षण डेमो खाते" else "QUICK DEMO ACCS & RECOVERY",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Quick demo preset button 1
                        SuggestionChip(
                            onClick = {
                                isLoginMode = true
                                errorMsg = null
                                // Auto seed popular admin demo
                                emailState = "admin@parent.com"
                                passwordState = "parent123"
                                // Also auto registers in background if not exist yet to prevent blocking!
                                if (!hasParentAccount) {
                                    viewModel.registerParent("Super Parent", "admin@parent.com", "parent123")
                                    viewModel.logoutParent() // return to logged out for demonstration opening!
                                    isLoginMode = true
                                }
                            },
                            label = { Text("Demo Primary", fontSize = 10.sp) },
                            modifier = Modifier.testTag("demo_pill_primary")
                        )

                        // Quick demo preset button 2
                        SuggestionChip(
                            onClick = {
                                isLoginMode = true
                                errorMsg = null
                                emailState = "mom@smartparent.com"
                                passwordState = "security2026"
                                if (!hasParentAccount) {
                                    viewModel.registerParent("Mom Rajput", "mom@smartparent.com", "security2026")
                                    viewModel.logoutParent()
                                    isLoginMode = true
                                }
                            },
                            label = { Text("Demo Secondary", fontSize = 10.sp) },
                            modifier = Modifier.testTag("demo_pill_secondary")
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == "hi") "ऊपर दिए गए 'डेमो' चिप्स पर टैप करने से क्रेडेंशियल्स अपने-आप भर जाएंगे!" else "Tap above preset chips to satisfy fields instantly!",
                        fontSize = 9.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
