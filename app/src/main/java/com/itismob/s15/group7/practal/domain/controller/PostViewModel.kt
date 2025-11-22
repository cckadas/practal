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
                if (value != null) {  _postList.value = value.toObjects(Post::class.java) }
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

}

