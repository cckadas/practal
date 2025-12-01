package com.itismob.s15.group7.practal.domain.model

import com.google.firebase.Timestamp

data class UserClub(
    val userId: String = "",
    val clubId: String = "",
    val joinedDate: Timestamp = Timestamp.now()
)
