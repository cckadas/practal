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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
    
    private fun loadAchievements() {
        db.collection("achievements")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("AchievementViewModel", "Error loading achievements", error)
                    return@addSnapshotListener
                }
                if (value != null) {
                    _achievements.value = value.toObjects(Achievement::class.java)
                }
            }
    }
    
    fun loadUserAchievements(userId: String) {
        db.collection("userAchievements")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("AchievementViewModel", "Error loading user achievements", error)
                    return@addSnapshotListener
                }
                if (value != null) {
                    _userAchievements.value = value.toObjects(UserAchievement::class.java)
                }
            }
    }
    
    // Check and unlock achievements based on user activity
    suspend fun checkAndUnlockAchievements(user: User) {
        val userId = user.id
        if (userId.isEmpty()) return
        
        // Practice Hours Achievements
        checkPracticeHoursAchievements(user)
        
        // Streak Achievements
        checkStreakAchievements(user)
        
        // Challenge Achievements
        checkChallengeAchievements(user)
        
        // Community Achievements
        checkCommunityAchievements(user)
        
        // Special Time-Based Achievements
        checkSpecialAchievements(user)
    }
    
    private suspend fun checkPracticeHoursAchievements(user: User) {
        val hours = user.totalPracticeHours
        val milestones = listOf(
            "practice_10" to 10.0,
            "practice_50" to 50.0,
            "practice_100" to 100.0,
            "practice_250" to 250.0,
            "practice_500" to 500.0,
            "practice_1000" to 1000.0
        )
        
        milestones.forEach { (achievementId, requiredHours) ->
            if (hours >= requiredHours) {
                unlockAchievement(user.id, achievementId, (hours / requiredHours * 100).toInt())
            }
        }
    }
    
    private suspend fun checkStreakAchievements(user: User) {
        val streak = user.longestStreak
        val milestones = listOf(
            "streak_7" to 7,
            "streak_30" to 30,
            "streak_60" to 60,
            "streak_100" to 100,
            "streak_365" to 365
        )
        
        milestones.forEach { (achievementId, requiredDays) ->
            if (streak >= requiredDays) {
                unlockAchievement(user.id, achievementId, 100)
            } else {
                updateProgress(user.id, achievementId, (streak.toFloat() / requiredDays * 100).toInt())
            }
        }
    }
    
    private suspend fun checkChallengeAchievements(user: User) {
        val completedChallenges = user.challenges.size
        val milestones = listOf(
            "challenge_1" to 1,
            "challenge_5" to 5,
            "challenge_10" to 10,
            "challenge_25" to 25,
            "challenge_50" to 50
        )
        
        milestones.forEach { (achievementId, requiredCount) ->
            if (completedChallenges >= requiredCount) {
                unlockAchievement(user.id, achievementId, 100)
            } else {
                updateProgress(user.id, achievementId, (completedChallenges.toFloat() / requiredCount * 100).toInt())
            }
        }
    }
    
    private suspend fun checkCommunityAchievements(user: User) {
        val followers = user.followers.size
        val following = user.following.size
        
        // Follower milestones
        val followerMilestones = listOf(
            "followers_10" to 10,
            "followers_50" to 50,
            "followers_100" to 100,
            "followers_500" to 500
        )
        
        followerMilestones.forEach { (achievementId, required) ->
            if (followers >= required) {
                unlockAchievement(user.id, achievementId, 100)
            } else {
                updateProgress(user.id, achievementId, (followers.toFloat() / required * 100).toInt())
            }
        }
        
        // Following milestones
        val followingMilestones = listOf(
            "following_10" to 10,
            "following_50" to 50
        )
        
        followingMilestones.forEach { (achievementId, required) ->
            if (following >= required) {
                unlockAchievement(user.id, achievementId, 100)
            } else {
                updateProgress(user.id, achievementId, (following.toFloat() / required * 100).toInt())
            }
        }
    }
    
    private suspend fun checkSpecialAchievements(user: User) {
        try {
            // load user practice sessions, check end times
            val sessions = db.collection("practiceSessions")
                .whereEqualTo("userId", user.id)
                .get()
                .await()
                .toObjects(com.itismob.s15.group7.practal.domain.model.PracticeSession::class.java)
            
            // count sessions after midnight (00:00 - 05:59)
            val nightOwlSessions = sessions.count { session ->
                val calendar = java.util.Calendar.getInstance()
                calendar.time = session.endTime.toDate()
                val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
                hour >= 0 && hour < 6
            }
            
            // count sessions, intentional morning practice (4 AM - 6 AM)
            val earlyBirdSessions = sessions.count { session ->
                val calendar = java.util.Calendar.getInstance()
                calendar.time = session.endTime.toDate()
                val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
                hour >= 4 && hour < 6
            }
            
            // night owl achievement (practice ending after midnight)
            if (nightOwlSessions >= 10) {
                unlockAchievement(user.id, "night_owl", 100)
            } else {
                updateProgress(user.id, "night_owl", (nightOwlSessions.toFloat() / 10 * 100).toInt())
            }
            
            // early bird achievement (practice ending in early morning)
            if (earlyBirdSessions >= 10) {
                unlockAchievement(user.id, "early_bird", 100)
            } else {
                updateProgress(user.id, "early_bird", (earlyBirdSessions.toFloat() / 10 * 100).toInt())
            }
            
        } catch (e: Exception) {
            Log.e("AchievementViewModel", "Error checking special achievements", e)
        }
    }
    
    private suspend fun unlockAchievement(userId: String, achievementId: String, progress: Int) {
        try {
            val docRef = db.collection("userAchievements")
                .document("${userId}_${achievementId}")
            
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
                
                // Award XP to user if achievement is completed
                if (progress >= 100) {
                    awardAchievementXP(userId, achievementId)
                }
                
                Log.d("AchievementViewModel", "Achievement $achievementId unlocked for user $userId")
            }
        } catch (e: Exception) {
            Log.e("AchievementViewModel", "Error unlocking achievement", e)
        }
    }
    
    private suspend fun updateProgress(userId: String, achievementId: String, progress: Int) {
        try {
            val docRef = db.collection("userAchievements")
                .document("${userId}_${achievementId}")
            
            val doc = docRef.get().await()
            
            if (!doc.exists()) {
                val userAchievement = UserAchievement(
                    userId = userId,
                    achievementId = achievementId,
                    unlockedDate = Timestamp.now(),
                    progress = progress,
                    completed = false
                )
                docRef.set(userAchievement).await()
            } else if (doc.getBoolean("completed") != true) {
                docRef.update("progress", progress).await()
            }
        } catch (e: Exception) {
            Log.e("AchievementViewModel", "Error updating achievement progress", e)
        }
    }
    
    private suspend fun awardAchievementXP(userId: String, achievementId: String) {
        try {
            val achievement = _achievements.value.find { it.id == achievementId }
            if (achievement != null) {
                val userRef = db.collection("users").document(userId)
                val userDoc = userRef.get().await()
                
                if (userDoc.exists()) {
                    val currentXP = userDoc.getLong("xp")?.toInt() ?: 0
                    userRef.update("xp", currentXP + achievement.xpReward).await()
                    Log.d("AchievementViewModel", "Awarded ${achievement.xpReward} XP for achievement $achievementId")
                }
            }
        } catch (e: Exception) {
            Log.e("AchievementViewModel", "Error awarding XP", e)
        }
    }
    
    fun getAchievementProgress(userId: String, achievementId: String): Int {
        val userAchievement = _userAchievements.value.find {
            it.userId == userId && it.achievementId == achievementId
        }
        return userAchievement?.progress ?: 0
    }
    
    fun isAchievementUnlocked(userId: String, achievementId: String): Boolean {
        val userAchievement = _userAchievements.value.find {
            it.userId == userId && it.achievementId == achievementId
        }
        return userAchievement?.completed ?: false
    }
    
    // setup default achievements in firestore
    private fun setupDefaultAchievements() {
        viewModelScope.launch {
            try {
                val existingAchievements = db.collection("achievements").get().await()
                
                if (existingAchievements.isEmpty) {
                    val defaultAchievements = listOf(
                        // practice hours
                        Achievement("practice_10", "First Steps", "Practice for 10 hours", "🎵", "practice", 10, 50),
                        Achievement("practice_50", "Dedicated Musician", "Practice for 50 hours", "🎼", "practice", 50, 100),
                        Achievement("practice_100", "Century Practice", "Practice for 100 hours", "🎯", "practice", 100, 200),
                        Achievement("practice_250", "Practice Master", "Practice for 250 hours", "⭐", "practice", 250, 500),
                        Achievement("practice_500", "Half Thousand", "Practice for 500 hours", "💎", "practice", 500, 1000),
                        Achievement("practice_1000", "Practice Legend", "Practice for 1000 hours", "👑", "practice", 1000, 2000),
                        
                        // streaks
                        Achievement("streak_7", "Week Warrior", "Maintain a 7-day practice streak", "🔥", "practice", 7, 50),
                        Achievement("streak_30", "30-Day Champion", "Maintain a 30-day practice streak", "💪", "practice", 30, 150),
                        Achievement("streak_60", "Two Month Master", "Maintain a 60-day practice streak", "🏆", "practice", 60, 300),
                        Achievement("streak_100", "Hundred Day Hero", "Maintain a 100-day practice streak", "🌟", "practice", 100, 500),
                        Achievement("streak_365", "Year-Long Dedication", "Maintain a 365-day practice streak", "👑", "practice", 365, 2000),
                        
                        // challenges
                        Achievement("challenge_1", "Challenge Accepted", "Complete your first challenge", "✅", "challenge", 1, 50),
                        Achievement("challenge_5", "Challenge Enthusiast", "Complete 5 challenges", "🎖️", "challenge", 5, 100),
                        Achievement("challenge_10", "Challenge Master", "Complete 10 challenges", "🏆", "challenge", 10, 200),
                        Achievement("challenge_25", "Challenge Champion", "Complete 25 challenges", "⭐", "challenge", 25, 500),
                        Achievement("challenge_50", "Challenge Legend", "Complete 50 challenges", "👑", "challenge", 50, 1000),
                        
                        // community - followers
                        Achievement("followers_10", "Rising Star", "Gain 10 followers", "🌠", "social", 10, 50),
                        Achievement("followers_50", "Popular Musician", "Gain 50 followers", "⭐", "social", 50, 150),
                        Achievement("followers_100", "Community Star", "Gain 100 followers", "✨", "social", 100, 300),
                        Achievement("followers_500", "Influencer", "Gain 500 followers", "👑", "social", 500, 1000),
                        
                        // community - following
                        Achievement("following_10", "Social Butterfly", "Follow 10 musicians", "🦋", "social", 10, 30),
                        Achievement("following_50", "Community Builder", "Follow 50 musicians", "🤝", "social", 50, 100),
                        
                        // special
                        Achievement("night_owl", "Night Owl", "Complete 10 practice sessions ending after midnight", "🦉", "special", 10, 100),
                        Achievement("early_bird", "Early Bird", "Complete 10 practice sessions ending between 4-6 AM", "🌅", "special", 10, 100)
                    )
                    
                    defaultAchievements.forEach { achievement ->
                        db.collection("achievements")
                            .document(achievement.id)
                            .set(achievement)
                            .await()
                    }
                    
                    Log.d("AchievementViewModel", "Default achievements created successfully")
                }
            } catch (e: Exception) {
                Log.e("AchievementViewModel", "Error setting up default achievements", e)
            }
        }
    }
}
