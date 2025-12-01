package com.itismob.s15.group7.practal.domain.controller

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.itismob.s15.group7.practal.domain.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostViewModel : ViewModel() {

    private var _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList = _postList.asStateFlow()

    private val db = Firebase.firestore


    init {
        getPostList()
    }


    private fun migrateExistingPostsWithId() {
        db.collection("posts").get().addOnSuccessListener { snapshot ->
            for (doc in snapshot.documents) {
                if (!doc.data!!.containsKey("id")) {
                    val docId = doc.id
                    db.collection("posts").document(doc.id)
                        .update("id", docId)
                        .addOnSuccessListener { println("Updated post ${doc.id} with id $docId") }
                        .addOnFailureListener { e -> e.printStackTrace() }
                }
            }
        }
    }


    fun getPostList() {
        db.collection("posts")
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                if (value != null) {
                    val posts = value.documents.mapNotNull { doc ->
                        try {
                            val data = doc.data ?: return@mapNotNull null
                            val postType = data["postType"] as? String ?: "regular"
                            
                            when (postType) {
                                "practice" -> doc.toObject(Post.PracticePost::class.java)
                                else -> doc.toObject(Post.RegularPost::class.java)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                    _postList.value = posts
                }
            }
    }


    fun toggleLike(postId: String, userEmail: String) {
        val postRef = db.collection("posts").document(postId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val currentLikes = snapshot.get("likes") as? List<String> ?: emptyList()

            val updatedLikes = if (currentLikes.contains(userEmail)) {
                FieldValue.arrayRemove(userEmail)
            } else {
                FieldValue.arrayUnion(userEmail)
            }

            transaction.update(postRef, "likes", updatedLikes)
        }
    }


    fun addComment(postId: String, commentText: String, userEmail: String) {
        val comment = "$commentText — $userEmail"
        db.collection("posts").document(postId)
            .update("comments", FieldValue.arrayUnion(comment))
            .addOnSuccessListener { println("Comment added") }
            .addOnFailureListener { it.printStackTrace() }
    }

    fun createRegularPost(
        ownerEmail: String,
        ownerImg: String,
        postImg: String,
        caption: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val postId = db.collection("posts").document().id
        val postData = hashMapOf(
            "id" to postId,
            "owner_email" to ownerEmail,
            "owner_img" to ownerImg,
            "post_img" to postImg,
            "caption" to caption,
            "likes" to emptyList<String>(),
            "comments" to emptyList<String>(),
            "date_posted" to com.google.firebase.Timestamp.now(),
            "postType" to "regular"
        )
        
        db.collection("posts").document(postId)
            .set(postData)
            .addOnSuccessListener { 
                println("Regular post created: $postId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onFailure(e)
            }
    }

    fun createPracticePost(
        ownerEmail: String,
        ownerImg: String,
        postImg: String,
        caption: String,
        sessionId: String,
        instrument: String,
        practiceType: String,
        pieceOrFocus: String,
        durationMinutes: Int,
        difficulty: String,
        earnedXP: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val postId = db.collection("posts").document().id
        val postData = hashMapOf(
            "id" to postId,
            "owner_email" to ownerEmail,
            "owner_img" to ownerImg,
            "post_img" to postImg,
            "caption" to caption,
            "likes" to emptyList<String>(),
            "comments" to emptyList<String>(),
            "date_posted" to com.google.firebase.Timestamp.now(),
            "postType" to "practice",
            "sessionId" to sessionId,
            "instrument" to instrument,
            "practiceType" to practiceType,
            "pieceOrFocus" to pieceOrFocus,
            "durationMinutes" to durationMinutes,
            "difficulty" to difficulty,
            "earnedXP" to earnedXP
        )
        
        db.collection("posts").document(postId)
            .set(postData)
            .addOnSuccessListener { 
                println("Practice post created: $postId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onFailure(e)
            }
    }

}

