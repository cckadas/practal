package com.itismob.s15.group7.practal.domain.controller

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.itismob.s15.group7.practal.domain.model.PracticeSession
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Centralized manager for calculating and updating user statistics from practice sessions.
 * Handles total practice hours, streaks, weekly/monthly counts, etc.
 */
object UserStatsManager {
    private const val TAG = "UserStatsManager"
    
    data class UserStats(
        val totalPracticeHours: Double,
        val currentStreak: Int,
        val longestStreak: Int,
        val sessionsThisWeek: Int,
        val sessionsThisMonth: Int,
        val lastPracticeDate: String,
        val sessionIds: List<String>
    )
    
    /**
     * Calculate all user stats from practice sessions
     */
    suspend fun calculateStatsFromSessions(
        userEmail: String,
        sessions: List<PracticeSession>,
        verbose: Boolean = false
    ): UserStats {
        if (verbose) {
            Log.d(TAG, "STATS CALCULATION: $userEmail---------")
            Log.d(TAG, "Total sessions found: ${sessions.size}")
        }
        
        if (sessions.isEmpty()) {
            return UserStats(0.0, 0, 0, 0, 0, "", emptyList())
        }
        
        // calculate total practice hours
        val totalMinutes = sessions.sumOf { it.durationMinutes }
        val totalSeconds = sessions.sumOf { it.durationSeconds }
        val totalHours = totalMinutes / 60.0
        
        if (verbose) {
            Log.d(TAG, "Total practice time: $totalMinutes min = $totalHours hours (${totalSeconds}s)")
            Log.d(TAG, "Session details with dates:")
            sessions.take(10).forEach { 
                Log.d(TAG, "  Session: ${it.instrument}, ${it.durationMinutes}min, date='${it.date}', startTime=${it.startTime.toDate()}")
            }
            if (sessions.size > 10) Log.d(TAG, "  ... and ${sessions.size - 10} more sessions")
        }
        
        // calculate streaks
        val streakResult = calculateStreaks(sessions, verbose)
        
        // calculate weekly and monthly sessions
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        
        val sessionsThisWeek = sessions.count { sess ->
            val sessionCal = Calendar.getInstance().apply { time = sess.startTime.toDate() }
            sessionCal.get(Calendar.WEEK_OF_YEAR) == currentWeek &&
            sessionCal.get(Calendar.YEAR) == currentYear
        }
        
        val sessionsThisMonth = sessions.count { sess ->
            val sessionCal = Calendar.getInstance().apply { time = sess.startTime.toDate() }
            sessionCal.get(Calendar.MONTH) == currentMonth &&
            sessionCal.get(Calendar.YEAR) == currentYear
        }
        
        // get last practice date
        val lastPracticeDate = sessions.maxByOrNull { it.startTime.seconds }?.date ?: ""
        
        // get session IDs
        val sessionIds = sessions.map { it.id }
        
        if (verbose) {
            Log.d(TAG, "Weekly sessions: $sessionsThisWeek, Monthly sessions: $sessionsThisMonth")
            Log.d(TAG, "Last practice date: $lastPracticeDate")
            Log.d(TAG, "------------------------------------")
        }
        
        return UserStats(
            totalPracticeHours = totalHours,
            currentStreak = streakResult.currentStreak,
            longestStreak = streakResult.longestStreak,
            sessionsThisWeek = sessionsThisWeek,
            sessionsThisMonth = sessionsThisMonth,
            lastPracticeDate = lastPracticeDate,
            sessionIds = sessionIds
        )
    }
    
    data class StreakResult(
        val currentStreak: Int,
        val longestStreak: Int
    )
    
    /**
     * Calculate current and longest streaks from practice sessions
     */
    fun calculateStreaks(sessions: List<PracticeSession>, verbose: Boolean = false): StreakResult {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        // get all dates and filter out empty ones
        val allDates = sessions.map { it.date }
        val emptyDates = allDates.count { it.isEmpty() }
        
        if (emptyDates > 0 && verbose) {
            Log.d(TAG, "⚠️  WARNING: $emptyDates sessions have empty dates!")
            sessions.filter { it.date.isEmpty() }.take(3).forEach {
                Log.d(TAG, "  Empty date session: ${it.id}, startTime=${it.startTime.toDate()}")
            }
        }
        
        val uniqueDates = allDates.filter { it.isNotEmpty() }.distinct().sorted()
        
        if (verbose) {
            Log.d(TAG, "Total dates: ${allDates.size}, Empty: $emptyDates, Unique practice days: ${uniqueDates.size}")
        }
        
        var currentStreak = 0
        var longestStreak = 0
        
        if (uniqueDates.isEmpty()) {
            return StreakResult(0, 0)
        }
        
        if (verbose) {
            Log.d(TAG, "Practice dates: ${uniqueDates.joinToString(", ")}")
        }
        
        // calculate current streak - count backwards from most recent practice day
        val mostRecentDate = dateFormat.parse(uniqueDates.last())
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        
        val todayStr = dateFormat.format(today)
        val mostRecentStr = uniqueDates.last()
        
        // check if most recent practice was today or yesterday (streak is still active)
        val daysSinceLastPractice = ((today.time - mostRecentDate.time) / (1000 * 60 * 60 * 24)).toInt()
        
        if (verbose) {
            Log.d(TAG, "Today: $todayStr, Most recent: $mostRecentStr, Days since: $daysSinceLastPractice")
        }
        
        if (daysSinceLastPractice <= 1) {
            currentStreak = 1
            if (verbose) Log.d(TAG, "Streak active! Starting from ${uniqueDates.last()}")
            
            // count consecutive days backwards
            if (uniqueDates.size > 1) {
                for (i in uniqueDates.size - 2 downTo 0) {
                    val nextDateStr = uniqueDates[i + 1]
                    val currDateStr = uniqueDates[i]
                    val nextDate = dateFormat.parse(nextDateStr)
                    val currDate = dateFormat.parse(currDateStr)
                    val diffDays = ((nextDate.time - currDate.time) / (1000 * 60 * 60 * 24)).toInt()
                    
                    if (verbose) Log.d(TAG, "  Checking: $currDateStr → $nextDateStr (gap: $diffDays days)")
                    
                    if (diffDays == 1) {
                        currentStreak++
                        if (verbose) Log.d(TAG, "  ✓ Consecutive! Streak now: $currentStreak")
                    } else {
                        if (verbose) Log.d(TAG, "  ✗ Gap of $diffDays days. Stopping at streak: $currentStreak")
                        break
                    }
                }
            }
            if (verbose) Log.d(TAG, "Final current streak: $currentStreak days")
        } else {
            if (verbose) Log.d(TAG, "Streak broken (last practice was $daysSinceLastPractice days ago)")
        }
        
        // calculate longest streak
        longestStreak = currentStreak
        var tempStreak = 1
        for (i in 1 until uniqueDates.size) {
            val prevDate = dateFormat.parse(uniqueDates[i - 1])
            val currDate = dateFormat.parse(uniqueDates[i])
            val diffDays = ((currDate.time - prevDate.time) / (1000 * 60 * 60 * 24)).toInt()
            if (diffDays == 1) {
                tempStreak++
                if (tempStreak > longestStreak) longestStreak = tempStreak
            } else {
                tempStreak = 1
            }
        }
        
        if (verbose) Log.d(TAG, "Longest streak: $longestStreak days")
        
        return StreakResult(currentStreak, longestStreak)
    }
    
    /**
     * Update user stats in Firebase if they differ from current values
     */
    suspend fun updateUserStatsIfChanged(
        db: FirebaseFirestore,
        userEmail: String,
        newStats: UserStats,
        verbose: Boolean = false
    ): Boolean {
        try {
            // find user document
            val userQuery = db.collection("users")
                .whereEqualTo("email", userEmail)
                .limit(1)
                .get()
                .await()
            
            if (userQuery.documents.isEmpty()) {
                Log.e(TAG, "User document not found for email: $userEmail")
                return false
            }
            
            val userDoc = userQuery.documents[0]
            
            // get current values from Firebase
            val currentTotalHours = userDoc.getDouble("totalPracticeHours") ?: 0.0
            val currentCurrentStreak = userDoc.getLong("currentStreak")?.toInt() ?: 0
            val currentLongestStreak = userDoc.getLong("longestStreak")?.toInt() ?: 0
            val currentSessionsThisWeek = userDoc.getLong("sessionsThisWeek")?.toInt() ?: 0
            val currentSessionsThisMonth = userDoc.getLong("sessionsThisMonth")?.toInt() ?: 0
            val currentLastPracticeDate = userDoc.getString("lastPracticeDate") ?: ""
            @Suppress("UNCHECKED_CAST")
            val currentSessionIds = userDoc.get("practiceSessions") as? List<String> ?: emptyList()
            
            // check if any values differ
            val hasChanges = 
                newStats.totalPracticeHours != currentTotalHours ||
                newStats.currentStreak != currentCurrentStreak ||
                newStats.longestStreak != currentLongestStreak ||
                newStats.sessionsThisWeek != currentSessionsThisWeek ||
                newStats.sessionsThisMonth != currentSessionsThisMonth ||
                newStats.lastPracticeDate != currentLastPracticeDate ||
                newStats.sessionIds != currentSessionIds
            
            if (hasChanges) {
                val updates = mapOf(
                    "totalPracticeHours" to newStats.totalPracticeHours,
                    "currentStreak" to newStats.currentStreak,
                    "longestStreak" to newStats.longestStreak,
                    "sessionsThisWeek" to newStats.sessionsThisWeek,
                    "sessionsThisMonth" to newStats.sessionsThisMonth,
                    "lastPracticeDate" to newStats.lastPracticeDate,
                    "practiceSessions" to newStats.sessionIds
                )
                
                db.collection("users").document(userDoc.id).update(updates).await()
                
                if (verbose) {
                    Log.d(TAG, "✓ UPDATED stats for $userEmail:")
                    if (newStats.totalPracticeHours != currentTotalHours) 
                        Log.d(TAG, "  Hours: $currentTotalHours → ${newStats.totalPracticeHours}")
                    if (newStats.currentStreak != currentCurrentStreak) 
                        Log.d(TAG, "  Current streak: $currentCurrentStreak → ${newStats.currentStreak}")
                    if (newStats.longestStreak != currentLongestStreak) 
                        Log.d(TAG, "  Longest streak: $currentLongestStreak → ${newStats.longestStreak}")
                    if (newStats.sessionsThisWeek != currentSessionsThisWeek) 
                        Log.d(TAG, "  Week sessions: $currentSessionsThisWeek → ${newStats.sessionsThisWeek}")
                    if (newStats.sessionsThisMonth != currentSessionsThisMonth) 
                        Log.d(TAG, "  Month sessions: $currentSessionsThisMonth → ${newStats.sessionsThisMonth}")
                    if (newStats.lastPracticeDate != currentLastPracticeDate) 
                        Log.d(TAG, "  Last practice: $currentLastPracticeDate → ${newStats.lastPracticeDate}")
                    if (newStats.sessionIds != currentSessionIds) 
                        Log.d(TAG, "  Session IDs: ${currentSessionIds.size} → ${newStats.sessionIds.size}")
                }
                return true
            } else {
                if (verbose) Log.d(TAG, "✓ Stats already in sync for $userEmail (no changes needed)")
                return false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating stats for $userEmail: ${e.message}", e)
            return false
        }
    }
}
