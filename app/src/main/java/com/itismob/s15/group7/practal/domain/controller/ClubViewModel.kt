package com.itismob.s15.group7.practal.domain.controller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.itismob.s15.group7.practal.domain.model.Club
import com.itismob.s15.group7.practal.domain.model.UserClub
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ClubViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    
    private val _clubs = MutableStateFlow<List<Club>>(emptyList())
    val clubs = _clubs.asStateFlow()
    
    private val _userClubs = MutableStateFlow<List<String>>(emptyList()) // List of clubIds user has joined
    val userClubs = _userClubs.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadClubs()
        setupDefaultClubs()
    }
    
    fun loadClubs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                firestore.collection("clubs")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e(TAG, "Error loading clubs", error)
                            return@addSnapshotListener
                        }
                        
                        val clubsList = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(Club::class.java)?.copy(id = doc.id)
                        } ?: emptyList()
                        
                        _clubs.value = clubsList
                        Log.d(TAG, "Loaded ${clubsList.size} clubs")
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadUserClubs(userId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("userClubs")
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e(TAG, "Error loading user clubs", error)
                            return@addSnapshotListener
                        }
                        
                        val clubIds = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(UserClub::class.java)?.clubId
                        } ?: emptyList()
                        
                        _userClubs.value = clubIds
                        Log.d(TAG, "User $userId is member of ${clubIds.size} clubs")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user clubs", e)
            }
        }
    }

    suspend fun joinClub(userId: String, clubId: String): Boolean {
        return try {
            val userClub = UserClub(
                userId = userId,
                clubId = clubId,
                joinedDate = Timestamp.now()
            )

            val clubRef = firestore.collection("clubs").document(clubId)
            val userRef = firestore.collection("users").document(userId)
            val userClubRef = firestore.collection("userClubs").document("${userId}_${clubId}")

            firestore.runTransaction { transaction ->
                // Add to userClubs collection
                transaction.set(userClubRef, userClub)

                // Increment member count
                val clubSnapshot = transaction.get(clubRef)
                val club = clubSnapshot.toObject(Club::class.java)
                club?.let {
                    transaction.update(clubRef, "memberCount", it.memberCount + 1)
                }

                // Update user's clubs
                val userSnapshot = transaction.get(userRef)
                val currentClubs = userSnapshot.get("clubs") as? List<*> ?: emptyList<String>()
                val updatedClubs = (currentClubs + clubId).distinct()
                transaction.update(userRef, "clubs", updatedClubs)
            }.await()
            Log.d(TAG, "User $userId joined club $clubId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error joining club", e)
            false
        }
    }
    
    suspend fun leaveClub(userId: String, clubId: String): Boolean {
        return try {
            // remove from userClubs
            firestore.collection("userClubs")
                .document("${userId}_${clubId}")
                .delete()
                .await()
            
            // decrement member count
            val clubRef = firestore.collection("clubs").document(clubId)
            firestore.runTransaction { transaction ->
                val club = transaction.get(clubRef).toObject(Club::class.java)
                club?.let {
                    transaction.update(clubRef, "memberCount", maxOf(0, it.memberCount - 1))
                }
            }.await()
            
            // update user
            val userRef = firestore.collection("users").document(userId)
            firestore.runTransaction { transaction ->
                val currentClubs = transaction.get(userRef).get("clubs") as? List<*> ?: emptyList<String>()
                val updatedClubs = currentClubs.filterNot { it == clubId }
                transaction.update(userRef, "clubs", updatedClubs)
            }.await()
            
            Log.d(TAG, "User $userId left club $clubId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error leaving club", e)
            false
        }
    }
    
    suspend fun createClub(
        name: String,
        description: String,
        category: String,
        emoji: String,
        createdBy: String
    ): String? {
        return try {
            val club = Club(
                name = name,
                description = description,
                category = category,
                emoji = emoji,
                createdBy = createdBy,
                createdDate = Timestamp.now(),
                memberCount = 1, // Creator is first member
                isOfficial = false
            )
            
            val docRef = firestore.collection("clubs").add(club).await()
            val clubId = docRef.id
            
            // join creator on club creation
            joinClub(createdBy, clubId)
            
            Log.d(TAG, "Created club $clubId")
            clubId
        } catch (e: Exception) {
            Log.e(TAG, "Error creating club", e)
            null
        }
    }

    private fun setupDefaultClubs() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("clubs").limit(1).get().await()
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Setting up default clubs")
                    DEFAULT_CLUBS.forEach { club ->
                        firestore.collection("clubs").add(club).await()
                    }
                    Log.d(TAG, "Default clubs created")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up default clubs", e)
            }
        }
    }

    // helper
    
    fun isUserMember(clubId: String): Boolean {
        return _userClubs.value.contains(clubId)
    }
    
    fun getClubsByCategory(category: String): List<Club> {
        return if (category == "All") {
            _clubs.value
        } else {
            _clubs.value.filter { it.category == category }
        }
    }

    // constants
    
    companion object {
        private const val TAG = "ClubViewModel"
        
        val DEFAULT_CLUBS = listOf(
            Club(
                name = "Jazz Enthusiasts",
                description = "For lovers of jazz music and improvisation",
                category = "Genre",
                emoji = "🎷",
                createdBy = "system",
                memberCount = 1240,
                isOfficial = true
            ),
            Club(
                name = "Piano Masters",
                description = "Classical and contemporary piano players",
                category = "Instrument",
                emoji = "🎹",
                createdBy = "system",
                memberCount = 856,
                isOfficial = true
            ),
            Club(
                name = "Rock & Roll Club",
                description = "Rock music fans and performers",
                category = "Genre",
                emoji = "🎸",
                createdBy = "system",
                memberCount = 2103,
                isOfficial = true
            ),
            Club(
                name = "Beginner's Circle",
                description = "Just starting your musical journey? Join us!",
                category = "Skill Level",
                emoji = "🌱",
                createdBy = "system",
                memberCount = 3421,
                isOfficial = true
            ),
            Club(
                name = "Violin Virtuosos",
                description = "String players unite",
                category = "Instrument",
                emoji = "🎻",
                createdBy = "system",
                memberCount = 645,
                isOfficial = true
            ),
            Club(
                name = "Classical Music Society",
                description = "Appreciate the classics",
                category = "Genre",
                emoji = "🎼",
                createdBy = "system",
                memberCount = 1872,
                isOfficial = true
            ),
            Club(
                name = "Drummers United",
                description = "Rhythm section headquarters",
                category = "Instrument",
                emoji = "🥁",
                createdBy = "system",
                memberCount = 934,
                isOfficial = true
            ),
            Club(
                name = "Daily Practice Squad",
                description = "Commit to practicing every day",
                category = "Activity",
                emoji = "💪",
                createdBy = "system",
                memberCount = 5234,
                isOfficial = true
            ),
            Club(
                name = "Blues & Soul",
                description = "Blues, soul, and R&B community",
                category = "Genre",
                emoji = "🎺",
                createdBy = "system",
                memberCount = 1456,
                isOfficial = true
            ),
            Club(
                name = "Guitar Guild",
                description = "All things guitar",
                category = "Instrument",
                emoji = "🎸",
                createdBy = "system",
                memberCount = 3210,
                isOfficial = true
            )
        )
    }
}
