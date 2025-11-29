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

    val points: Int = 0, // challenges
    val xp: Int = 0, // practicing, engagement, and achievements

    val followers: List<String> = emptyList(),
    val following: List<String> = emptyList(),

    val challenges: Map<String, Int> = emptyMap(),

    val totalPracticeHours: Double = 0.0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val sessionsThisWeek: Int = 0,
    val sessionsThisMonth: Int = 0,
    val lastPracticeDate: String = "",
    val practiceSessions: List<String> = emptyList(), // session IDs
    val weeklyGoalHours: Double = 5.0,
    val level: Int = 1

)
