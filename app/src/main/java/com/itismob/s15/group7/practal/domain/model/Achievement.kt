package com.itismob.s15.group7.practal.domain.model

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val badgeIcon: String = "",
    val category: String = "", // practice, challenge, social, special
    val requirement: Int = 0,
    val xpReward: Int = 0
)
