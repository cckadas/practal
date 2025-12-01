package com.itismob.s15.group7.practal.domain.controller

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.itismob.s15.group7.practal.domain.model.PracticeSession
import com.itismob.s15.group7.practal.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.String

class UserViewModel : ViewModel() {
    private var _userList = MutableStateFlow<List<User>>(emptyList())
    var userList = _userList.asStateFlow()

    var _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser = _loggedInUser.asStateFlow()

    var tempSignUpData = MutableStateFlow(SignUpData())

    private val db = Firebase.firestore

    init {
        getUserList()
        syncLoggedInUser()
        syncUserStatsWithPracticeSessions()
    }

    // automatically sync loggedInUser with userList updates
    private fun syncLoggedInUser() {
        viewModelScope.launch {
            _userList.collect { users ->
                _loggedInUser.value?.let { currentUser ->
                    // update loggedInUser with fresh data from userList
                    users.find { it.id == currentUser.id }?.let { updatedUser ->
                        _loggedInUser.value = updatedUser
                    }
                }
            }
        }
    }

    fun getUserList() {
        db.collection("users")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("UserViewModel", "Error in getUserList listener: ${error.message}", error)
                    return@addSnapshotListener
                }
                if (value != null) {
                    val users = value.toObjects(User::class.java)
                    _userList.value = users
                    Log.d("UserViewModel", "User list updated from Firebase - ${users.size} users loaded")
                    
                    // Log specific user data for debugging
                    _loggedInUser.value?.let { currentUser ->
                        val updatedCurrentUser = users.find { it.email == currentUser.email }
                        if (updatedCurrentUser != null) {
                            Log.d("UserViewModel", "Current user updated - followers: ${updatedCurrentUser.followers.size}, following: ${updatedCurrentUser.following.size}")
                            Log.d("UserViewModel", "Current user following list: ${updatedCurrentUser.following}")
                            Log.d("UserViewModel", "Current user followers list: ${updatedCurrentUser.followers}")
                        }
                    }
                }
            }
    }

    // sync user stats with actual practice session data on initial load
    private fun syncUserStatsWithPracticeSessions() {
        viewModelScope.launch {
            try {
                Log.d("UserViewModel", "Starting initial sync of user stats with practice sessions...")
                
                val users = db.collection("users").get().await().toObjects(User::class.java)
                
                users.forEach { user ->
                    if (user.email.isNotEmpty()) {
                        recalculateUserStats(user.email)
                    }
                }
                
                Log.d("UserViewModel", "Completed initial sync for ${users.size} users")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error syncing user stats: ${e.message}", e)
            }
        }
    }

    private suspend fun recalculateUserStats(userEmail: String) {
        try {
            // fetch all practice sessions for user
            val sessions = db.collection("practice_sessions")
                .whereEqualTo("userId", userEmail)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(PracticeSession::class.java) }
            
            if (sessions.isEmpty()) {
                Log.d("UserViewModel", "No sessions found for $userEmail, skipping stats calculation")
                return
            }
            
            // calculate stats
            val stats = UserStatsManager.calculateStatsFromSessions(userEmail, sessions, verbose = true)
            
            // update firebase on found differences
            UserStatsManager.updateUserStatsIfChanged(db, userEmail, stats, verbose = true)
            
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error recalculating stats for $userEmail: ${e.message}", e)
        }
    }


    fun login(email: String, password: String): Boolean {
        val user = _userList.value.find {
            it.email == email && it.password == password
        }
        _loggedInUser.value = user
        return user != null
    }


    fun logout() {
        _loggedInUser.value = null
    }


    fun getUserByEmail(email: String): User? {
        return _userList.value.find { it.email == email }
    }

    @Composable
    fun getUserByEmailReactive(email: String): User? {
        val userList by userList.collectAsState()
        return userList.find { it.email == email }
    }


    fun signUp(
        email: String,
        password: String,
        firstname: String,
        lastname: String,
        birthdate: String = "",
        image: String = "",
        introduction: String = "",
        preferences: List<String> = emptyList(),
        interests: List<String> = emptyList(),
        goals: List<String> = emptyList(),
        skill_level: String = "",
        demo: String = "",
        projects: List<String> = emptyList(),
        points: Int = 0,
        xp: Int = 0,
        followers: List<String> = emptyList(),
        following: List<String> = emptyList(),
        challenges: Map<String, Int> = emptyMap(),

        onSuccess: (User) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val existingUser = _userList.value.find { it.email == email }
        if (existingUser != null) {
            onFailure("Email already exists.")
            return
        }

        val newUser = User(
            id = System.currentTimeMillis().toString(),
            email = email,
            password = password,
            firstname = firstname,
            lastname = lastname,
            birthdate = birthdate,
            image = image,
            introduction = introduction,
            preferences = preferences,
            interests = interests,
            goals = goals,
            skill_level = skill_level,
            demo = demo,
            xp = xp,
            projects = projects,
            points = points,
            followers = followers,
            following = following,
            challenges = challenges

        )

        db.collection("users")
            .document(newUser.id.toString())
            .set(newUser)
            .addOnSuccessListener {
                _loggedInUser.value = newUser
                onSuccess(newUser)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to create account.")
            }
    }


    fun updateProfilePhoto(newPhotoUrl: String) {
        viewModelScope.launch {
            val current = tempSignUpData.value
            tempSignUpData.value = current.copy(image = newPhotoUrl)
        }
    }


    fun getRankedUsers(targetEmail: String): Pair<List<User>, Int?> {
        val users = _userList.value
        val rankedUsers = users.sortedByDescending { it.points }
        val rankIndex = rankedUsers.indexOfFirst { it.email == targetEmail }
        val rank = if (rankIndex != -1) rankIndex + 1 else null
        return Pair(rankedUsers, rank)
    }
    
    // follow a user
    suspend fun followUser(currentUserEmail: String, targetUserEmail: String): Boolean {
        return try {
            Log.d("UserViewModel", "FOLLOW OPERATION START--------")
            Log.d("UserViewModel", "Current user: $currentUserEmail wants to follow: $targetUserEmail")
            
            // find id
            val usersQuery = db.collection("users").get().await()
            var currentUserDoc: String? = null
            var targetUserDoc: String? = null
            
            for (doc in usersQuery.documents) {
                val user = doc.toObject(User::class.java)
                if (user?.email == currentUserEmail) {
                    currentUserDoc = doc.id
                    Log.d("UserViewModel", "Found current user document ID: ${doc.id}")
                }
                if (user?.email == targetUserEmail) {
                    targetUserDoc = doc.id
                    Log.d("UserViewModel", "Found target user document ID: ${doc.id}")
                }
            }
            
            if (currentUserDoc == null || targetUserDoc == null) {
                Log.e("UserViewModel", "Could not find document IDs - currentUserDoc: $currentUserDoc, targetUserDoc: $targetUserDoc")
                return false
            }
            
            val currentUserRef = db.collection("users").document(currentUserDoc)
            val targetUserRef = db.collection("users").document(targetUserDoc)
            
            db.runTransaction { transaction ->
                Log.d("UserViewModel", "Starting Firebase transaction...")
                val currentUser = transaction.get(currentUserRef).toObject(User::class.java)
                val targetUser = transaction.get(targetUserRef).toObject(User::class.java)
                
                Log.d("UserViewModel", "Current user loaded: ${currentUser != null}")
                Log.d("UserViewModel", "Target user loaded: ${targetUser != null}")
                
                if (currentUser != null && targetUser != null) {
                    Log.d("UserViewModel", "BEFORE - Current user following: ${currentUser.following}")
                    Log.d("UserViewModel", "BEFORE - Target user followers: ${targetUser.followers}")
                    
                    // add target to current user's following list
                    val updatedFollowing = currentUser.following.toMutableList()
                    if (!updatedFollowing.contains(targetUserEmail)) {
                        updatedFollowing.add(targetUserEmail)
                        Log.d("UserViewModel", "Adding $targetUserEmail to $currentUserEmail's following list")
                        Log.d("UserViewModel", "NEW following list: $updatedFollowing")
                        transaction.update(currentUserRef, "following", updatedFollowing)
                    } else {
                        Log.d("UserViewModel", "$targetUserEmail already in following list")
                    }
                    
                    // add current user to target's followers list
                    val updatedFollowers = targetUser.followers.toMutableList()
                    if (!updatedFollowers.contains(currentUserEmail)) {
                        updatedFollowers.add(currentUserEmail)
                        Log.d("UserViewModel", "Adding $currentUserEmail to $targetUserEmail's followers list")
                        Log.d("UserViewModel", "NEW followers list: $updatedFollowers")
                        transaction.update(targetUserRef, "followers", updatedFollowers)
                    } else {
                        Log.d("UserViewModel", "$currentUserEmail already in followers list")
                    }
                } else {
                    Log.e("UserViewModel", "Failed to load users - currentUser: ${currentUser != null}, targetUser: ${targetUser != null}")
                }
            }.await()
            
            Log.d("UserViewModel", "Firebase transaction completed successfully")
            Log.d("UserViewModel", "$currentUserEmail followed $targetUserEmail")
            Log.d("UserViewModel", "FOLLOW OPERATION END--------")
            true
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error following user: ${e.message}", e)
            Log.d("UserViewModel", "FOLLOW OPERATION FAILED--------")
            false
        }
    }
    
    // unfollow a user
    suspend fun unfollowUser(currentUserEmail: String, targetUserEmail: String): Boolean {
        return try {
            Log.d("UserViewModel", "=== UNFOLLOW OPERATION START ===")
            Log.d("UserViewModel", "Current user: $currentUserEmail wants to unfollow: $targetUserEmail")
            
            // First, find the actual document IDs by email
            val usersQuery = db.collection("users").get().await()
            var currentUserDoc: String? = null
            var targetUserDoc: String? = null
            
            for (doc in usersQuery.documents) {
                val user = doc.toObject(User::class.java)
                if (user?.email == currentUserEmail) {
                    currentUserDoc = doc.id
                    Log.d("UserViewModel", "Found current user document ID: ${doc.id}")
                }
                if (user?.email == targetUserEmail) {
                    targetUserDoc = doc.id
                    Log.d("UserViewModel", "Found target user document ID: ${doc.id}")
                }
            }
            
            if (currentUserDoc == null || targetUserDoc == null) {
                Log.e("UserViewModel", "Could not find document IDs - currentUserDoc: $currentUserDoc, targetUserDoc: $targetUserDoc")
                return false
            }
            
            val currentUserRef = db.collection("users").document(currentUserDoc)
            val targetUserRef = db.collection("users").document(targetUserDoc)
            
            db.runTransaction { transaction ->
                Log.d("UserViewModel", "Starting Firebase transaction...")
                val currentUser = transaction.get(currentUserRef).toObject(User::class.java)
                val targetUser = transaction.get(targetUserRef).toObject(User::class.java)
                
                Log.d("UserViewModel", "Current user loaded: ${currentUser != null}")
                Log.d("UserViewModel", "Target user loaded: ${targetUser != null}")
                
                if (currentUser != null && targetUser != null) {
                    Log.d("UserViewModel", "BEFORE - Current user following: ${currentUser.following}")
                    Log.d("UserViewModel", "BEFORE - Target user followers: ${targetUser.followers}")
                    
                    // remove target from current user's following list
                    val updatedFollowing = currentUser.following.toMutableList()
                    val wasRemoved = updatedFollowing.remove(targetUserEmail)
                    Log.d("UserViewModel", "Removed $targetUserEmail from following: $wasRemoved")
                    Log.d("UserViewModel", "NEW following list: $updatedFollowing")
                    transaction.update(currentUserRef, "following", updatedFollowing)
                    
                    // remove current user from target's followers list
                    val updatedFollowers = targetUser.followers.toMutableList()
                    val wasRemovedFromFollowers = updatedFollowers.remove(currentUserEmail)
                    Log.d("UserViewModel", "Removed $currentUserEmail from followers: $wasRemovedFromFollowers")
                    Log.d("UserViewModel", "NEW followers list: $updatedFollowers")
                    transaction.update(targetUserRef, "followers", updatedFollowers)
                } else {
                    Log.e("UserViewModel", "Failed to load users - currentUser: ${currentUser != null}, targetUser: ${targetUser != null}")
                }
            }.await()
            
            Log.d("UserViewModel", "Firebase transaction completed successfully")
            Log.d("UserViewModel", "$currentUserEmail unfollowed $targetUserEmail")
            Log.d("UserViewModel", "=== UNFOLLOW OPERATION END ===")
            true
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error unfollowing user: ${e.message}", e)
            Log.d("UserViewModel", "=== UNFOLLOW OPERATION FAILED ===")
            false
        }
    }
}


data class SignUpData(
    var email: String = "",
    var password: String = "",
    var firstname: String = "",
    var lastname: String = "",
    var birthdate: String = "",
    var image: String = "",
    var introduction: String = "",
    var preferences: List<String> = emptyList(),
    var interests: List<String> = emptyList(),
    var goals: List<String> = emptyList(),
    var skill_level: String = "",
    var demo: String = "",
    var projects: List<String> = emptyList()
)