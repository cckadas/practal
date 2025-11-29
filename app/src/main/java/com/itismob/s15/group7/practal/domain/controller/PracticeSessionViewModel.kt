package com.itismob.s15.group7.practal.domain.controller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.itismob.s15.group7.practal.domain.model.PracticeSession
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
                val currentHours = userDoc.getDouble("totalPracticeHours") ?: 0.0
                val newHours = currentHours + (session.durationMinutes / 60.0)
                
                // Calculate XP earned based on difficulty and duration
                val earnedXP = calculateXP(session.durationMinutes, session.difficulty)
                val currentXP = userDoc.getLong("xp")?.toInt() ?: 0
                val currentLevel = userDoc.getLong("level")?.toInt() ?: 1
                val newXP = currentXP + earnedXP
                
                // Calculate new level
                val newLevel = calculateLevel(newXP)
                
                // Calculate sessions this week and month
                val allSessions = db.collection("practice_sessions")
                    .whereEqualTo("userId", userEmail)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { it.toObject(PracticeSession::class.java) }
                
                val calendar = Calendar.getInstance()
                val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
                val currentYear = calendar.get(Calendar.YEAR)
                val currentMonth = calendar.get(Calendar.MONTH)
                
                val sessionsThisWeek = allSessions.count { sess ->
                    val sessionCal = Calendar.getInstance().apply { time = sess.startTime.toDate() }
                    sessionCal.get(Calendar.WEEK_OF_YEAR) == currentWeek &&
                    sessionCal.get(Calendar.YEAR) == currentYear
                }
                
                val sessionsThisMonth = allSessions.count { sess ->
                    val sessionCal = Calendar.getInstance().apply { time = sess.startTime.toDate() }
                    sessionCal.get(Calendar.MONTH) == currentMonth &&
                    sessionCal.get(Calendar.YEAR) == currentYear
                }
                
                val updates = mutableMapOf<String, Any>(
                    "totalPracticeHours" to newHours,
                    "lastPracticeDate" to session.date,
                    "practiceSessions" to com.google.firebase.firestore.FieldValue.arrayUnion(session.id),
                    "xp" to newXP,
                    "level" to newLevel,
                    "sessionsThisWeek" to sessionsThisWeek,
                    "sessionsThisMonth" to sessionsThisMonth
                )

                db.collection("users").document(userDoc.id).update(updates).await()
                
                if (newLevel > currentLevel) {
                    Log.d("PracticeSession", "✓ LEVEL UP! ${currentLevel} → ${newLevel} | XP: ${currentXP} → ${newXP} (+${earnedXP}) | Week: $sessionsThisWeek, Month: $sessionsThisMonth")
                } else {
                    Log.d("PracticeSession", "✓ Updated user stats: ${currentHours}h → ${newHours}h | XP: ${currentXP} → ${newXP} (+${earnedXP}) | Week: $sessionsThisWeek, Month: $sessionsThisMonth")
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
    
    /**
     * Calculate level based on total XP
     * Level 1 to 2: 100 XP
     * Level 2 to 3: 150 XP (total 250)
     * Level 3 to 4: 200 XP (total 450)
     * Formula: XP required for next level = 100 + (currentLevel - 1) * 50
     */
    private fun calculateLevel(totalXP: Int): Int {
        var level = 1
        var xpForNextLevel = 100
        var accumulatedXP = 0
        
        while (totalXP >= accumulatedXP + xpForNextLevel) {
            accumulatedXP += xpForNextLevel
            level++
            xpForNextLevel = 100 + (level - 1) * 50
        }
        
        return level
    }

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

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val uniqueDates = sessions.map { it.date }.distinct().sortedDescending()
            
            var currentStreak = 0
            var longestStreak = 0
            var tempStreak = 1
            
            // Calculate current streak from today
            val today = dateFormat.format(Date())
            if (uniqueDates.contains(today)) {
                currentStreak = 1
                for (i in 1 until uniqueDates.size) {
                    val prevDate = dateFormat.parse(uniqueDates[i - 1])
                    val currDate = dateFormat.parse(uniqueDates[i])
                    val diffDays = ((prevDate.time - currDate.time) / (1000 * 60 * 60 * 24)).toInt()
                    if (diffDays == 1) {
                        currentStreak++
                    } else {
                        break
                    }
                }
            }
            
            // Calculate longest streak
            longestStreak = currentStreak
            for (i in 1 until uniqueDates.size) {
                val prevDate = dateFormat.parse(uniqueDates[i - 1])
                val currDate = dateFormat.parse(uniqueDates[i])
                val diffDays = ((prevDate.time - currDate.time) / (1000 * 60 * 60 * 24)).toInt()
                if (diffDays == 1) {
                    tempStreak++
                    if (tempStreak > longestStreak) longestStreak = tempStreak
                } else {
                    tempStreak = 1
                }
            }
            
            // Find user document by email and update streak info
            val userQuery = db.collection("users")
                .whereEqualTo("email", userEmail)
                .limit(1)
                .get()
                .await()
            
            if (userQuery.documents.isNotEmpty()) {
                val userDocId = userQuery.documents[0].id
                db.collection("users").document(userDocId).update(
                    mapOf(
                        "currentStreak" to currentStreak,
                        "longestStreak" to longestStreak
                    )
                ).await()
                Log.d("PracticeSession", "✓ Updated streaks: current=$currentStreak, longest=$longestStreak")
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

        // Calculate start time based on duration
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