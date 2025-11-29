package com.itismob.s15.group7.practal.domain.model

import com.google.firebase.Timestamp

data class PracticeSession(
    val id: String = "",
    val userId: String = "",
    val instrument: String = "",
    val practiceType: String = "", // piece/technique/scales/warmup etc.
    val pieceOrFocus: String = "",
    val startTime: Timestamp = Timestamp.now(), // session time
    val endTime: Timestamp = Timestamp.now(),
    val durationMinutes: Int = 0,
    val durationSeconds: Int = 0,
    val notes: String = "",
    val difficulty: String = "", // beginner/intermediate/advanced
    val streakDay: Int = 0, // current streak day
    val date: String = "" // for grouping
)