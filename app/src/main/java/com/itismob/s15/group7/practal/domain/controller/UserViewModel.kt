package com.itismob.s15.group7.practal.domain.controller

import android.content.Context
import android.net.Uri
import android.util.Log
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
                if (error != null) return@addSnapshotListener
                if (value != null) {
                    _userList.value = value.toObjects(User::class.java)
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