package com.itismob.s15.group7.practal.domain.model

data class Challenge(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val participants: Int = 0,
    val goal: Int = 30
)