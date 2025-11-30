package com.itismob.s15.group7.practal.domain.model

import com.google.firebase.Timestamp

data class Club(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val emoji: String = "",
    val createdBy: String = "", // userId
    val createdDate: Timestamp = Timestamp.now(),
    val memberCount: Int = 0,
    val isOfficial: Boolean = false // community created or not
)
