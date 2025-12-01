package com.itismob.s15.group7.practal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.itismob.s15.group7.practal.DarkGreen
import com.itismob.s15.group7.practal.LightGreen
import com.itismob.s15.group7.practal.WhiteBox
import com.itismob.s15.group7.practal.domain.controller.PracticeSessionViewModel
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.domain.model.PracticeSession
import java.util.Calendar
import java.util.Date
import com.itismob.s15.group7.practal.ui.theme.Poppins
import kotlin.math.roundToInt

fun formatDuration(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%d:%02d", minutes, secs)
    }
}

@Composable
fun ProgressAnalyticsScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    practiceViewModel: PracticeSessionViewModel
) {
    var selectedFilter by remember { mutableStateOf("Weekly") }
    val filters = listOf("Weekly", "Monthly", "Yearly")
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    val practiceSessions by practiceViewModel.practiceSessions.collectAsState()
    val isLoading by practiceViewModel.isLoading.collectAsState()

    val statsData = remember(selectedFilter, practiceSessions) {
        val filteredSessions = filterSessionsByPeriod(practiceSessions, selectedFilter)
        val totalSeconds = filteredSessions.sumOf { 
            if (it.durationSeconds > 0) it.durationSeconds else it.durationMinutes * 60 
        }
        mapOf("totalSeconds" to totalSeconds, "sessionCount" to filteredSessions.size)
    }

    LaunchedEffect(Unit) { practiceViewModel.loadUserPracticeSessions() }
    LaunchedEffect(practiceSessions.size) {
        if (practiceSessions.isNotEmpty()) userViewModel.getUserList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 92.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen)
                .padding(24.dp)
                .height(64.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hey ${loggedInUser?.firstname}! 👋",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = Poppins
                    )
                    Text(
                        text = "Your Progress",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = Poppins
                    )
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 32.sp
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) DarkGreen else Color.White)
                        .padding(vertical = 12.dp).clickable { selectedFilter = filter },
                    contentAlignment = Alignment.Center
                ) {
                    Text(filter, fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 14.sp, 
                        color = if (isSelected) Color.White else DarkGreen)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCard(
                icon = Icons.Default.AccessTime,
                title = "Practice Time",
                value = formatDuration(statsData["totalSeconds"] as? Int ?: 0),
                subtitle = selectedFilter.lowercase(),
                color = DarkGreen,
                modifier = Modifier.weight(1f)
            )

            StatsCard(
                icon = Icons.Default.MusicNote,
                title = "Sessions",
                value = "${statsData["sessionCount"] as? Int ?: 0}",
                subtitle = "logged",
                color = DarkGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        StreakCard(loggedInUser?.currentStreak ?: 0, loggedInUser?.longestStreak ?: 0)

        Spacer(modifier = Modifier.height(24.dp))
        LevelCard(loggedInUser?.level ?: 1, loggedInUser?.xp ?: 0)

        Spacer(modifier = Modifier.height(24.dp))
        SectionCard("Practice Frequency") {
            PracticeFrequencyChart(selectedFilter, practiceSessions)
        }

        Spacer(modifier = Modifier.height(24.dp))
        SectionCard("Recent Sessions", if (isLoading) Icons.Default.Refresh else null) {
            when {
                isLoading -> repeat(3) { SessionItemSkeleton(); if (it < 2) Spacer(Modifier.height(8.dp)) }
                practiceSessions.isEmpty() -> EmptyState("🎵", "No practice sessions yet", "Start logging your practice!")
                else -> {
                    practiceSessions.take(5).forEach { session ->
                        SessionItem(
                            session.instrument, session.practiceType, session.pieceOrFocus,
                            formatDuration(if (session.durationSeconds > 0) session.durationSeconds else session.durationMinutes * 60),
                            session.date, session.difficulty
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    if (!isLoading) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { navController.navigate("log_practice") }.padding(8.dp),
                            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Log New Session", fontSize = 14.sp, color = DarkGreen, fontFamily = Poppins, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.Add, null, tint = DarkGreen, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        SectionCard("Practice by Instrument") {
            val instrumentBreakdown = remember(practiceSessions) {
                val totalMinutes = practiceSessions.sumOf { it.durationMinutes }
                if (totalMinutes == 0) emptyList()
                else practiceSessions.groupBy { it.instrument }
                    .map { (instrument, sessions) ->
                        val minutes = sessions.sumOf { it.durationMinutes }
                        instrument to ((minutes.toFloat() / totalMinutes) * 100).toInt()
                    }.sortedByDescending { it.second }.take(4)
            }
            
            if (instrumentBreakdown.isEmpty()) {
                Text("No instrument data yet. Start logging sessions!", fontSize = 14.sp, color = Color.Gray, 
                    fontFamily = Poppins, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            } else {
                val colors = listOf(DarkGreen, CardAccent, SoftGreen, LightGreen)
                instrumentBreakdown.forEachIndexed { index, (instrument, percentage) ->
                    InstrumentBreakdownItem(instrument, percentage, colors.getOrElse(index) { DarkGreen })
                }
            }
        }
    }
}

@Composable
fun StatsCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                fontFamily = Poppins
            )
            
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
            
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.Gray.copy(alpha = 0.6f),
                fontFamily = Poppins
            )
        }
    }
}

@Composable
fun PracticeFrequencyChart(filter: String, practiceSessions: List<PracticeSession> = emptyList()) {
    val days = when (filter) {
        "Weekly" -> listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        "Monthly" -> listOf("W1", "W2", "W3", "W4")
        else -> listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    }
    
    val heights = remember(practiceSessions, filter) {
        calculateChartHeights(practiceSessions, filter)
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEachIndexed { index, _ ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (filter == "Yearly") 20.dp else 30.dp)
                            .height((150 * heights.getOrElse(index) { 0.5f }).dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(DarkGreen.copy(alpha = 0.7f))
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { day ->
                Text(
                    text = day,
                    fontSize = if (filter == "Yearly") 10.sp else 12.sp,
                    color = Color.Gray,
                    fontFamily = Poppins,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AchievementItem(icon: String, title: String, date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 32.sp,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                fontFamily = Poppins
            )
            Text(
                text = date,
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
        }
    }
}

@Composable
fun InstrumentBreakdownItem(instrument: String, percentage: Int, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = instrument,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                fontFamily = Poppins
            )
            Text(
                text = "$percentage%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = Poppins
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun SessionItem(
    instrument: String, practiceType: String, pieceOrFocus: String,
    duration: String, date: String, difficulty: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF8F9FA))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InstrumentIcon(instrument, 18.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("$instrument - $practiceType", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DarkGreen, fontFamily = Poppins)
            if (pieceOrFocus.isNotEmpty()) {
                Text(pieceOrFocus, fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(duration, fontSize = 11.sp, color = Color.Gray.copy(alpha = 0.7f), fontFamily = Poppins)
                if (difficulty.isNotEmpty()) {
                    Text(" - $difficulty", fontSize = 11.sp, color = Color.Gray.copy(alpha = 0.7f), fontFamily = Poppins)
                }
            }
        }
        Text(date, fontSize = 11.sp, color = Color.Gray.copy(alpha = 0.6f), fontFamily = Poppins)
    }
}

@Composable 
fun SessionItemSkeleton() {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF8F9FA)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray.copy(alpha = 0.3f)))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(Modifier.fillMaxWidth(0.6f).height(14.dp).clip(RoundedCornerShape(4.dp)).background(Color.LightGray.copy(alpha = 0.3f)))
            Spacer(modifier = Modifier.height(4.dp))
            Box(Modifier.fillMaxWidth(0.4f).height(12.dp).clip(RoundedCornerShape(4.dp)).background(Color.LightGray.copy(alpha = 0.2f)))
        }
        Box(Modifier.width(50.dp).height(11.dp).clip(RoundedCornerShape(4.dp)).background(Color.LightGray.copy(alpha = 0.2f)))
    }
}

// helper
private fun filterSessionsByPeriod(
    sessions: List<PracticeSession>,
    filter: String
): List<PracticeSession> {
    return sessions.filter { session ->
        try {
            val sessionDate = session.startTime.toDate()
            val diffInMillis = System.currentTimeMillis() - sessionDate.time
            val daysAgo = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
            when (filter) {
                "Weekly" -> daysAgo in 0..6
                "Monthly" -> daysAgo in 0..27
                else -> {
                    val sessionCal = Calendar.getInstance().apply { time = sessionDate }
                    val currentCal = Calendar.getInstance()
                    val monthsAgo = (currentCal.get(Calendar.YEAR) - sessionCal.get(Calendar.YEAR)) * 12 +
                                   (currentCal.get(Calendar.MONTH) - sessionCal.get(Calendar.MONTH))
                    monthsAgo in 0..11
                }
            }
        } catch (e: Exception) {
            false
        }
    }
}

private fun calculateChartHeights(
    sessions: List<PracticeSession>,
    filter: String
): List<Float> {
    val buckets = when (filter) {
        "Weekly" -> MutableList(7) { 0 }.also { buckets ->
            sessions.forEach { session ->
                try {
                    val sessionDate = session.startTime.toDate()
                    val sessionCal = Calendar.getInstance().apply { time = sessionDate }
                    val diffInMillis = System.currentTimeMillis() - sessionDate.time
                    val daysAgo = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
                    if (daysAgo in 0..6) {
                        val dayOfWeek = when (sessionCal.get(Calendar.DAY_OF_WEEK)) {
                            Calendar.MONDAY -> 0
                            Calendar.TUESDAY -> 1
                            Calendar.WEDNESDAY -> 2
                            Calendar.THURSDAY -> 3
                            Calendar.FRIDAY -> 4
                            Calendar.SATURDAY -> 5
                            Calendar.SUNDAY -> 6
                            else -> 0
                        }
                        buckets[dayOfWeek] += 1
                    }
                } catch (e: Exception) {}
            }
        }
        "Monthly" -> MutableList(4) { 0 }.also { buckets ->
            sessions.forEach { session ->
                try {
                    val sessionDate = session.startTime.toDate()
                    val diffInMillis = System.currentTimeMillis() - sessionDate.time
                    val daysAgo = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
                    val weekIndex = when {
                        daysAgo in 0..6 -> 3
                        daysAgo in 7..13 -> 2
                        daysAgo in 14..20 -> 1
                        daysAgo in 21..27 -> 0
                        else -> -1
                    }
                    if (weekIndex >= 0) buckets[weekIndex] += 1
                } catch (e: Exception) {}
            }
        }
        else -> MutableList(12) { 0 }.also { buckets ->
            sessions.forEach { session ->
                try {
                    val sessionDate = session.startTime.toDate()
                    val sessionCal = Calendar.getInstance().apply { time = sessionDate }
                    val currentCal = Calendar.getInstance()
                    val monthsAgo = (currentCal.get(Calendar.YEAR) - sessionCal.get(Calendar.YEAR)) * 12 +
                                   (currentCal.get(Calendar.MONTH) - sessionCal.get(Calendar.MONTH))
                    if (monthsAgo in 0..11) buckets[11 - monthsAgo] += 1
                } catch (e: Exception) {}
            }
        }
    }
    val maxCount = buckets.maxOrNull() ?: 1
    return buckets.map { count -> if (maxCount > 0) (count.toFloat() / maxCount).coerceIn(0f, 1f) else 0f }
}

@Composable
private fun InstrumentIcon(instrument: String, fontSize: androidx.compose.ui.unit.TextUnit = 20.sp) {
    Box(
        modifier = Modifier.size(40.dp).clip(CircleShape).background(LightGreen),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when(instrument) {
                "Piano" -> "🎹"
                "Guitar" -> "🎸"
                "Violin" -> "🎻"
                "Drums" -> "🥁"
                "Voice" -> "🎤"
                "Flute" -> "🪈"
                "Saxophone" -> "🎷"
                "Trumpet" -> "🎺"
                else -> "🎵"
            },
            fontSize = fontSize
        )
    }
}

@Composable
private fun StreakCard(currentStreak: Int, longestStreak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 40.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        if (currentStreak > 0) "$currentStreak Day Streak!" else "Start Your Streak!",
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkGreen, fontFamily = Poppins
                    )
                    Text(
                        if (longestStreak > 0) "Best: $longestStreak days" else "Practice daily to build streaks!",
                        fontSize = 13.sp, color = Color.Gray, fontFamily = Poppins
                    )
                }
            }
            Icon(
                Icons.Default.LocalFireDepartment, null,
                tint = if (currentStreak > 0) Color(0xFFFF6B35) else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun LevelCard(level: Int, xp: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎵", fontSize = 40.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Level $level", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkGreen, fontFamily = Poppins)
                    Text("$xp XP", fontSize = 13.sp, color = Color.Gray, fontFamily = Poppins)
                }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = DarkGreen, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun SectionCard(title: String, icon: ImageVector? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen, fontFamily = Poppins)
                icon?.let { CircularProgressIndicator(modifier = Modifier.size(16.dp), color = DarkGreen) }
            }
            Spacer(Modifier.height(if (icon != null) 12.dp else 16.dp))
            content()
        }
    }
}

@Composable
private fun EmptyState(emoji: String, title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 32.sp)
        Spacer(Modifier.height(8.dp))
        Text(title, fontSize = 14.sp, color = Color.Gray, fontFamily = Poppins)
        Text(subtitle, fontSize = 12.sp, color = Color.Gray.copy(alpha = 0.7f), fontFamily = Poppins)
    }
}
