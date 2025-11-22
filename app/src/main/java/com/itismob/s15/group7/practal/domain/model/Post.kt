package com.itismob.s15.group7.practal.domain.model

import com.google.firebase.Timestamp

data class Post(
    val id: String = "",
    val owner_email: String = "",
    val owner_img: String = "",
    val post_img: String = "",
    val caption: String = "",
    val likes: List<String> = emptyList(),
    val comments: List<String> = emptyList(),
    val date_posted: Timestamp = Timestamp.now()
)
