package com.itismob.s15.group7.practal.domain.model

import kotlin.collections.List

data class User(
    val id: String = "",
    val email: String = "",
    val password: String = "",

    val firstname: String = "",
    val lastname: String = "",
    val birthdate: String = "",
    val image: String = "",

    val introduction: String = "",

    val preferences: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val goals: List<String> = emptyList(),
    val skill_level: String = "",

    val demo: String = "",
    val projects: List<String> = emptyList(),

    val points: Int = 0,
    val xp: Int = 0,

    val followers: List<String> = emptyList(),
    val following: List<String> = emptyList(),

    val challenges: Map<String, Int> = emptyMap()

)
