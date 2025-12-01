package com.itismob.s15.group7.practal.domain.model

import com.google.firebase.Timestamp

data class UserAchievement(
    val userId: String = "",
    val achievementId: String = "",
    val unlockedDate: Timestamp = Timestamp.now(),
    val progress: Int = 0, // 0-100 percentage
    val completed: Boolean = false
)
