package com.itismob.s15.group7.practal.domain.controller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.itismob.s15.group7.practal.domain.model.PracticeSession
import com.itismob.s15.group7.practal.domain.model.computeLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class PracticeSessionViewModel(private val userViewModel: UserViewModel) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _practiceSessions = MutableStateFlow<List<PracticeSession>>(emptyList())
    val practiceSessions: StateFlow<List<PracticeSession>> = _practiceSessions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentSession = MutableStateFlow<PracticeSession?>(null)
    val currentSession: StateFlow<PracticeSession?> = _currentSession

    init {
        loadUserPracticeSessions()
    }

    fun startPracticeSession(
        instrument: String,
        practiceType: String,
        pieceOrFocus: String
    ) {
        val userId = userViewModel.loggedInUser.value?.email
        if (userId == null) {
            Log.e("PracticeSession", "Cannot start session: User not authenticated")
            return
        }
        
        val now = Timestamp.now()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(now.toDate())

        val session = PracticeSession(
            id = UUID.randomUUID().toString(),
            userId = userId,
            instrument = instrument,
            practiceType = practiceType,
            pieceOrFocus = pieceOrFocus,
            startTime = now,
            endTime = now,
            date = dateString
        )

        _currentSession.value = session
        Log.d("PracticeSession", "✓ Started session: ${session.id}, user: $userId, instrument: $instrument")
    }

    fun endPracticeSession(
        notes: String = "",
        difficulty: String = ""
    ) {
        val currentSession = _currentSession.value ?: return
        val endTime = Timestamp.now()
        val durationSeconds = (endTime.seconds - currentSession.startTime.seconds).toInt()
        val durationMinutes = durationSeconds / 60 // Keep for backward compatibility

        val completedSession = currentSession.copy(
            endTime = endTime,
            durationMinutes = durationMinutes,
            durationSeconds = durationSeconds,
            notes = notes,
            difficulty = difficulty
        )

        viewModelScope.launch {
            try {
                _isLoading.value = true
                savePracticeSession(completedSession)
                updateUserStats(completedSession)
                _currentSession.value = null
                loadUserPracticeSessionsSuspend(completedSession.userId)
                calculateAndUpdateStreak(completedSession.userId)
                Log.d("PracticeSession", "Successfully ended practice session: ${completedSession.id}")
            } catch (e: Exception) {
                Log.e("PracticeSession", "Error ending practice session", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun savePracticeSession(session: PracticeSession) {
        try {
            val sessionData = hashMapOf(
                "id" to session.id,
                "userId" to session.userId,
                "instrument" to session.instrument,
                "practiceType" to session.practiceType,
                "pieceOrFocus" to session.pieceOrFocus,
                "startTime" to session.startTime,
                "endTime" to session.endTime,
                "durationMinutes" to session.durationMinutes,
                "durationSeconds" to session.durationSeconds,
                "notes" to session.notes,
                "difficulty" to session.difficulty,
                "streakDay" to session.streakDay,
                "date" to session.date
            )
            
            Log.d("PracticeSession", "Attempting to save session ${session.id} to Firestore...")
            
            db.collection("practice_sessions")
                .document(session.id)
                .set(sessionData)
                .await()
            
            Log.d("PracticeSession", "✓ Successfully saved session ${session.id}: ${session.durationMinutes}min, ${session.instrument}")
        } catch (e: Exception) {
            Log.e("PracticeSession", "✗ Failed to save session ${session.id}: ${e.message}", e)
            throw e
        }
    }

    private suspend fun updateUserStats(session: PracticeSession) {
        val userEmail = session.userId
        
        try {
            Log.d("PracticeSession", "Updating stats for user: $userEmail")
            
            // Find user document by email
            val userQuery = db.collection("users")
                .whereEqualTo("email", userEmail)
                .limit(1)
                .get()
                .await()
            
            if (userQuery.documents.isNotEmpty()) {
                val userDoc = userQuery.documents[0]
                
                // Calculate XP earned based on difficulty and duration
                val earnedXP = calculateXP(session.durationMinutes, session.difficulty)
                val currentXP = userDoc.getLong("xp")?.toInt() ?: 0
                val currentLevel = userDoc.getLong("level")?.toInt() ?: 1
                val newXP = currentXP + earnedXP
                
                // Get detailed level information
                val currentLevelInfo = computeLevel(currentXP)
                val newLevelInfo = computeLevel(newXP)
                val newLevel = newLevelInfo.level
                
                // Log detailed XP progress
                Log.d("PracticeSession", "XP PROGRESS-------------------------")
                Log.d("PracticeSession", "Current Level: ${currentLevelInfo.level} (${currentLevelInfo.title})")
                Log.d("PracticeSession", "Current XP in Level: ${currentLevelInfo.currentXP} / ${currentLevelInfo.xpToNextLevel}")
                Log.d("PracticeSession", "XP Earned: +$earnedXP XP")
                Log.d("PracticeSession", "New Total XP: $currentXP → $newXP")
                Log.d("PracticeSession", "New Level: ${newLevelInfo.level} (${newLevelInfo.title})")
                Log.d("PracticeSession", "New XP in Level: ${newLevelInfo.currentXP} / ${newLevelInfo.xpToNextLevel}")
                if (newLevel > currentLevel) {
                    Log.d("PracticeSession", "🎉 LEVEL UP! ${currentLevelInfo.level} → ${newLevelInfo.level}")
                } else {
                    Log.d("PracticeSession", "Progress: ${newLevelInfo.currentXP - currentLevelInfo.currentXP} XP added to current level")
                }
                Log.d("PracticeSession", "------------------------------------")
                
                // Update XP and level first
                db.collection("users").document(userDoc.id).update(
                    mapOf(
                        "xp" to newXP,
                        "level" to newLevel
                    )
                ).await()
                
                // Fetch all sessions for this user to recalculate comprehensive stats
                val allSessions = db.collection("practice_sessions")
                    .whereEqualTo("userId", userEmail)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { it.toObject(PracticeSession::class.java) }
                
                // Use centralized manager to calculate all stats (hours, streaks, weekly/monthly counts)
                val stats = UserStatsManager.calculateStatsFromSessions(userEmail, allSessions, verbose = true)
                
                // Update Firebase with calculated stats
                UserStatsManager.updateUserStatsIfChanged(db, userEmail, stats, verbose = true)
                
                if (newLevel > currentLevel) {
                    Log.d("PracticeSession", "✓ LEVEL UP! ${currentLevel} → ${newLevel} | XP: ${currentXP} → ${newXP} (+${earnedXP})")
                } else {
                    Log.d("PracticeSession", "✓ Updated user stats: XP: ${currentXP} → ${newXP} (+${earnedXP})")
                }
            } else {
                Log.e("PracticeSession", "✗ User document not found for email: $userEmail")
            }
        } catch (e: Exception) {
            Log.e("PracticeSession", "✗ Error updating user stats for $userEmail: ${e.message}", e)
        }
    }
    
    /**
     * Calculate XP earned based on practice duration and difficulty
     * Base XP: 1 XP per minute
     * Difficulty multipliers: Beginner (1.0x), Intermediate (1.5x), Advanced (2.0x), Expert (2.5x)
     */
    private fun calculateXP(durationMinutes: Int, difficulty: String): Int {
        val baseXP = durationMinutes
        val multiplier = when (difficulty.lowercase()) {
            "beginner" -> 1.0
            "intermediate" -> 1.5
            "advanced" -> 2.0
            "expert" -> 2.5
            else -> 1.0 // Default to beginner if difficulty not specified
        }
        return (baseXP * multiplier).toInt()
    }
    
    // Note: Level calculation is handled by the centralized computeLevel() function
    // from domain.model.LevelSystem.kt

    fun loadUserPracticeSessions() {
        val userId = userViewModel.loggedInUser.value?.email
        if (userId == null) {
            Log.e("PracticeSession", "Cannot load sessions: User not authenticated")
            return
        }

        viewModelScope.launch {
            loadUserPracticeSessionsSuspend(userId)
        }
    }
    
    private suspend fun loadUserPracticeSessionsSuspend(userId: String) {
        try {
            _isLoading.value = true
            Log.d("PracticeSession", "Loading sessions for user: $userId")
            
            val sessionsSnapshot = db.collection("practice_sessions")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val sessions = sessionsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(PracticeSession::class.java)?.copy(id = doc.id)
            }.sortedByDescending { it.startTime.toDate() }

            _practiceSessions.value = sessions
            Log.d("PracticeSession", "✓ Loaded ${sessions.size} practice sessions for $userId")
            sessions.forEach { 
                Log.d("PracticeSession", "  - ${it.id}: ${it.instrument}, ${it.durationMinutes}min, ${it.date}")
            }
        } catch (e: Exception) {
            Log.e("PracticeSession", "✗ Error loading sessions: ${e.message}", e)
            _practiceSessions.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    fun getWeeklyStats(): Map<String, Any> {
        val sessions = _practiceSessions.value
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)

        val weeklySessions = sessions.filter { session ->
            val sessionCalendar = Calendar.getInstance().apply {
                time = session.startTime.toDate()
            }
            sessionCalendar.get(Calendar.WEEK_OF_YEAR) == currentWeek &&
                    sessionCalendar.get(Calendar.YEAR) == currentYear
        }

        val totalMinutes = weeklySessions.sumOf { it.durationMinutes }
        val sessionCount = weeklySessions.size

        return mapOf(
            "totalHours" to (totalMinutes / 60.0),
            "sessionCount" to sessionCount,
            "averageRating" to 0.0
        )
    }

    fun getMonthlyStats(): Map<String, Any> {
        val sessions = _practiceSessions.value
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val monthlySessions = sessions.filter { session ->
            val sessionCalendar = Calendar.getInstance().apply {
                time = session.startTime.toDate()
            }
            sessionCalendar.get(Calendar.MONTH) == currentMonth &&
                    sessionCalendar.get(Calendar.YEAR) == currentYear
        }

        val totalMinutes = monthlySessions.sumOf { it.durationMinutes }
        val sessionCount = monthlySessions.size

        return mapOf(
            "totalHours" to (totalMinutes / 60.0),
            "sessionCount" to sessionCount,
            "averageRating" to 0.0
        )
    }

    fun isSessionActive(): Boolean {
        return _currentSession.value != null
    }

    private suspend fun calculateAndUpdateStreak(userEmail: String) {
        try {
            val sessions = _practiceSessions.value
            if (sessions.isEmpty()) return

            // calculate streak
            val streakResult = UserStatsManager.calculateStreaks(sessions, verbose = true)
            
            // find user by email and update streak
            val userQuery = db.collection("users")
                .whereEqualTo("email", userEmail)
                .limit(1)
                .get()
                .await()
            
            if (userQuery.documents.isNotEmpty()) {
                val userDocId = userQuery.documents[0].id
                db.collection("users").document(userDocId).update(
                    mapOf(
                        "currentStreak" to streakResult.currentStreak,
                        "longestStreak" to streakResult.longestStreak
                    )
                ).await()
                Log.d("PracticeSession", "✓ Updated streaks: current=${streakResult.currentStreak}, longest=${streakResult.longestStreak}")
            } else {
                Log.e("PracticeSession", "✗ User document not found for email: $userEmail")
            }
        } catch (e: Exception) {
            Log.e("PracticeSession", "Error calculating streak", e)
        }
    }

    fun getCurrentSessionDuration(): Int {
        val current = _currentSession.value ?: return 0
        val now = System.currentTimeMillis() / 1000
        return ((now - current.startTime.seconds) / 60).toInt()
    }

    fun logManualSession(
        instrument: String,
        practiceType: String,
        pieceOrFocus: String,
        durationMinutes: Int,
        notes: String = "",
        difficulty: String = ""
    ) {
        val userId = userViewModel.loggedInUser.value?.email ?: return
        val now = Timestamp.now()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(now.toDate())

        // calculate start time based on duration
        val startTimeMillis = now.toDate().time - (durationMinutes * 60 * 1000)
        val startTime = Timestamp(Date(startTimeMillis))

        val session = PracticeSession(
            id = UUID.randomUUID().toString(),
            userId = userId,
            instrument = instrument,
            practiceType = practiceType,
            pieceOrFocus = pieceOrFocus,
            startTime = startTime,
            endTime = now,
            durationMinutes = durationMinutes,
            notes = notes,
            difficulty = difficulty,
            date = dateString
        )

        viewModelScope.launch {
            try {
                _isLoading.value = true
                savePracticeSession(session)
                updateUserStats(session)
                loadUserPracticeSessionsSuspend(session.userId)
                calculateAndUpdateStreak(session.userId)
                Log.d("PracticeSession", "Successfully logged manual practice session: ${session.id}")
            } catch (e: Exception) {
                Log.e("PracticeSession", "Error logging manual practice session", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}