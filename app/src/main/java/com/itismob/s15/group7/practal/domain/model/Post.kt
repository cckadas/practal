package com.itismob.s15.group7.practal.domain.model

import com.google.firebase.Timestamp

sealed class Post {
    abstract val id: String
    abstract val owner_email: String
    abstract val owner_img: String
    abstract val caption: String
    abstract val likes: List<String>
    abstract val comments: List<String>
    abstract val date_posted: Timestamp
    abstract val postType: String
    
    // regular post
    data class RegularPost(
        override val id: String = "",
        override val owner_email: String = "",
        override val owner_img: String = "",
        val post_img: String = "", // optional
        override val caption: String = "",
        override val likes: List<String> = emptyList(),
        override val comments: List<String> = emptyList(),
        override val date_posted: Timestamp = Timestamp.now(),
        override val postType: String = "regular"
    ) : Post()
    
    // practice session
    data class PracticePost(
        override val id: String = "",
        override val owner_email: String = "",
        override val owner_img: String = "",
        override val caption: String = "",
        val post_img: String = "", // optional image
        override val likes: List<String> = emptyList(),
        override val comments: List<String> = emptyList(),
        override val date_posted: Timestamp = Timestamp.now(),
        override val postType: String = "practice",
        
        // practice session details
        val sessionId: String = "", //  practice session
        val instrument: String = "",
        val practiceType: String = "",
        val pieceOrFocus: String = "",
        val durationMinutes: Int = 0,
        val difficulty: String = "",
        val earnedXP: Int = 0
    ) : Post()
}
