package com.itismob.s15.group7.practal.domain.controller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.itismob.s15.group7.practal.domain.model.Achievement
import com.itismob.s15.group7.practal.domain.model.UserAchievement
import com.itismob.s15.group7.practal.domain.model.User
import com.itismob.s15.group7.practal.domain.model.PracticeSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class AchievementViewModel : ViewModel() {
    private val db = Firebase.firestore
    
    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements = _achievements.asStateFlow()
    
    private val _userAchievements = MutableStateFlow<List<UserAchievement>>(emptyList())
    val userAchievements = _userAchievements.asStateFlow()
    
    init {
        loadAchievements()
        setupDefaultAchievements()
    }
    
    // load data
    
    private fun loadAchievements() {
        db.collection("achievements")
            .addSnapshotListener { value, error ->
                error?.let {
                    Log.e(TAG, "Error loading achievements", it)
                    return@addSnapshotListener
                }
                _achievements.value = value?.toObjects(Achievement::class.java) ?: emptyList()
            }
    }
    
    fun loadUserAchievements(userId: String) {
        Log.d(TAG, "Loading user achievements for userId: $userId")
        db.collection("userAchievements")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { value, error ->
                error?.let {
                    Log.e(TAG, "Error loading user achievements", it)
                    return@addSnapshotListener
                }
                
                val userAchs = value?.toObjects(UserAchievement::class.java) ?: emptyList()
                _userAchievements.value = userAchs
                Log.d(TAG, "Loaded ${userAchs.size} user achievements")
                userAchs.forEach { Log.d(TAG, "  UserAchievement: id=${it.achievementId}, completed=${it.completed}, progress=${it.progress}") }
            }
    }
    
    // check achievements
    
    suspend fun checkAndUnlockAchievements(user: User) {
        if (user.id.isEmpty()) return
        
        checkPracticeHoursAchievements(user)
        checkStreakAchievements(user)
        checkChallengeAchievements(user)
        checkCommunityAchievements(user)
        checkSpecialAchievements(user)
    }
    
    private suspend fun checkPracticeHoursAchievements(user: User) {
        PRACTICE_HOURS_MILESTONES.forEach { (id, hours) ->
            val progress = (user.totalPracticeHours / hours * 100).toInt()
            if (user.totalPracticeHours >= hours) {
                unlockAchievement(user.id, id, progress)
            }
        }
    }
    
    private suspend fun checkStreakAchievements(user: User) {
        STREAK_MILESTONES.forEach { (id, days) ->
            if (user.longestStreak >= days) {
                unlockAchievement(user.id, id, 100)
            } else {
                updateProgress(user.id, id, calculateProgress(user.longestStreak, days))
            }
        }
    }
    
    private suspend fun checkChallengeAchievements(user: User) {
        val count = user.challenges.size
        CHALLENGE_MILESTONES.forEach { (id, required) ->
            if (count >= required) {
                unlockAchievement(user.id, id, 100)
            } else {
                updateProgress(user.id, id, calculateProgress(count, required))
            }
        }
    }
    
    private suspend fun checkCommunityAchievements(user: User) {
        checkMilestones(user.id, user.followers.size, FOLLOWER_MILESTONES)
        checkMilestones(user.id, user.following.size, FOLLOWING_MILESTONES)
    }
    
    private suspend fun checkSpecialAchievements(user: User) {
        try {
            val sessions = db.collection("practice_sessions")
                .whereEqualTo("userId", user.id)
                .get()
                .await()
                .toObjects(PracticeSession::class.java)
            
            val nightOwlCount = sessions.count { it.endTime.isInHourRange(0, 6) }
            val earlyBirdCount = sessions.count { it.endTime.isInHourRange(4, 6) }
            
            checkMilestone(user.id, nightOwlCount, "night_owl", 10)
            checkMilestone(user.id, earlyBirdCount, "early_bird", 10)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking special achievements", e)
        }
    }
    
    // helper funcs
    
    private suspend fun checkMilestones(userId: String, current: Int, milestones: List<Pair<String, Int>>) {
        milestones.forEach { (id, required) ->
            if (current >= required) {
                unlockAchievement(userId, id, 100)
            } else {
                updateProgress(userId, id, calculateProgress(current, required))
            }
        }
    }
    
    private suspend fun checkMilestone(userId: String, current: Int, achievementId: String, required: Int) {
        if (current >= required) {
            unlockAchievement(userId, achievementId, 100)
        } else {
            updateProgress(userId, achievementId, calculateProgress(current, required))
        }
    }
    
    private fun calculateProgress(current: Int, required: Int): Int =
        ((current.toFloat() / required) * 100).toInt()
    
    private fun calculateProgress(current: Double, required: Double): Int =
        ((current / required) * 100).toInt()
    
    private fun Timestamp.isInHourRange(startHour: Int, endHour: Int): Boolean {
        val calendar = Calendar.getInstance().apply { time = toDate() }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return hour >= startHour && hour < endHour
    }
    
    // db ops
    
    private suspend fun unlockAchievement(userId: String, achievementId: String, progress: Int) {
        try {
            val docRef = db.collection("userAchievements").document("${userId}_${achievementId}")
            val doc = docRef.get().await()
            
            if (!doc.exists() || doc.getBoolean("completed") != true) {
                val userAchievement = UserAchievement(
                    userId = userId,
                    achievementId = achievementId,
                    unlockedDate = Timestamp.now(),
                    progress = progress,
                    completed = progress >= 100
                )
                docRef.set(userAchievement).await()
                
                if (progress >= 100) awardAchievementXP(userId, achievementId)
                Log.d(TAG, "Achievement $achievementId unlocked for user $userId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error unlocking achievement", e)
        }
    }
    
    private suspend fun updateProgress(userId: String, achievementId: String, progress: Int) {
        try {
            val docRef = db.collection("userAchievements").document("${userId}_${achievementId}")
            val doc = docRef.get().await()
            
            if (!doc.exists()) {
                docRef.set(UserAchievement(userId, achievementId, Timestamp.now(), progress, false)).await()
            } else if (doc.getBoolean("completed") != true) {
                docRef.update("progress", progress).await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating achievement progress", e)
        }
    }
    
    private suspend fun awardAchievementXP(userId: String, achievementId: String) {
        try {
            val achievement = _achievements.value.find { it.id == achievementId } ?: return
            val userRef = db.collection("users").document(userId)
            val userDoc = userRef.get().await()
            
            if (userDoc.exists()) {
                val currentXP = userDoc.getLong("xp")?.toInt() ?: 0
                userRef.update("xp", currentXP + achievement.xpReward).await()
                Log.d(TAG, "Awarded ${achievement.xpReward} XP for achievement $achievementId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error awarding XP", e)
        }
    }
    
    fun getAchievementProgress(userId: String, achievementId: String): Int =
        _userAchievements.value.find { it.userId == userId && it.achievementId == achievementId }?.progress ?: 0
    
    fun isAchievementUnlocked(userId: String, achievementId: String): Boolean =
        _userAchievements.value.find { it.userId == userId && it.achievementId == achievementId }?.completed ?: false
    
    // setup
    
    private fun setupDefaultAchievements() {
        viewModelScope.launch {
            try {
                val existingAchievements = db.collection("achievements").get().await()
                if (existingAchievements.isEmpty) {
                    DEFAULT_ACHIEVEMENTS.forEach {
                        db.collection("achievements").document(it.id).set(it).await()
                    }
                    Log.d(TAG, "Default achievements created successfully")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up default achievements", e)
            }
        }
    }
    
    // constants
    
    companion object {
        private const val TAG = "AchievementViewModel"
        
        // requirements
        private val PRACTICE_HOURS_MILESTONES = listOf(
            "practice_10" to 10.0, "practice_50" to 50.0, "practice_100" to 100.0,
            "practice_250" to 250.0, "practice_500" to 500.0, "practice_1000" to 1000.0
        )
        
        private val STREAK_MILESTONES = listOf(
            "streak_7" to 7, "streak_30" to 30, "streak_60" to 60,
            "streak_100" to 100, "streak_365" to 365
        )
        
        private val CHALLENGE_MILESTONES = listOf(
            "challenge_1" to 1, "challenge_5" to 5, "challenge_10" to 10,
            "challenge_25" to 25, "challenge_50" to 50
        )
        
        private val FOLLOWER_MILESTONES = listOf(
            "followers_10" to 10, "followers_50" to 50,
            "followers_100" to 100, "followers_500" to 500
        )
        
        private val FOLLOWING_MILESTONES = listOf(
            "following_10" to 10, "following_50" to 50
        )
        
        // definitions
        private val DEFAULT_ACHIEVEMENTS = listOf(
            // Practice Hours
            Achievement("practice_10", "First Steps", "Practice for 10 hours", "🎵", "practice", 10, 50),
            Achievement("practice_50", "Dedicated Musician", "Practice for 50 hours", "🎼", "practice", 50, 100),
            Achievement("practice_100", "Century Practice", "Practice for 100 hours", "🎯", "practice", 100, 200),
            Achievement("practice_250", "Practice Master", "Practice for 250 hours", "⭐", "practice", 250, 500),
            Achievement("practice_500", "Half Thousand", "Practice for 500 hours", "💎", "practice", 500, 1000),
            Achievement("practice_1000", "Practice Legend", "Practice for 1000 hours", "👑", "practice", 1000, 2000),
            
            // Streaks
            Achievement("streak_7", "Week Warrior", "Maintain a 7-day practice streak", "🔥", "practice", 7, 50),
            Achievement("streak_30", "30-Day Champion", "Maintain a 30-day practice streak", "💪", "practice", 30, 150),
            Achievement("streak_60", "Two Month Master", "Maintain a 60-day practice streak", "🏆", "practice", 60, 300),
            Achievement("streak_100", "Hundred Day Hero", "Maintain a 100-day practice streak", "🌟", "practice", 100, 500),
            Achievement("streak_365", "Year-Long Dedication", "Maintain a 365-day practice streak", "👑", "practice", 365, 2000),
            
            // Challenges
            Achievement("challenge_1", "Challenge Accepted", "Complete your first challenge", "✅", "challenge", 1, 50),
            Achievement("challenge_5", "Challenge Enthusiast", "Complete 5 challenges", "🎖️", "challenge", 5, 100),
            Achievement("challenge_10", "Challenge Master", "Complete 10 challenges", "🏆", "challenge", 10, 200),
            Achievement("challenge_25", "Challenge Champion", "Complete 25 challenges", "⭐", "challenge", 25, 500),
            Achievement("challenge_50", "Challenge Legend", "Complete 50 challenges", "👑", "challenge", 50, 1000),
            
            // Community - Followers
            Achievement("followers_10", "Rising Star", "Gain 10 followers", "🌠", "social", 10, 50),
            Achievement("followers_50", "Popular Musician", "Gain 50 followers", "⭐", "social", 50, 150),
            Achievement("followers_100", "Community Star", "Gain 100 followers", "✨", "social", 100, 300),
            Achievement("followers_500", "Influencer", "Gain 500 followers", "👑", "social", 500, 1000),
            
            // Community - Following
            Achievement("following_10", "Social Butterfly", "Follow 10 musicians", "🦋", "social", 10, 30),
            Achievement("following_50", "Community Builder", "Follow 50 musicians", "🤝", "social", 50, 100),
            
            // Special
            Achievement("night_owl", "Night Owl", "Complete 10 practice sessions ending after midnight", "🦉", "special", 10, 100),
            Achievement("early_bird", "Early Bird", "Complete 10 practice sessions ending between 4-6 AM", "🌅", "special", 10, 100)
        )
    }
}
