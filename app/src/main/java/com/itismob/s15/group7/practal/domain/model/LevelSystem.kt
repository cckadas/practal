package com.itismob.s15.group7.practal.domain.model

data class UserLevel(
    val level: Int,
    val title: String,
    val currentXP: Int,        // XP within current level (0 to xpToNextLevel)
    val xpToNextLevel: Int     // Total XP needed to reach next level from current level
)

/**
 * XP thresholds for each level
 * Level 1: 0 XP
 * Level 2: 100 XP
 * Level 3: 250 XP
 * Level 4: 500 XP
 * Level 5: 1000 XP
 * Level 6: 1500 XP
 * Level 7: 2000 XP
 */
private val XP_THRESHOLDS = listOf(0, 100, 250, 500, 1000, 1500, 2000)

/**
 * computes the user level and progress information based on total XP
 * @param totalXP The user's total accumulated XP
 * @return UserLevel object with level, title, current XP within level, and XP needed for next level
 */
fun computeLevel(totalXP: Int): UserLevel {
    // Determine current level based on XP thresholds
    var level = 1
    for (i in XP_THRESHOLDS.indices) {
        if (totalXP >= XP_THRESHOLDS[i]) {
            level = i + 1
        } else break
    }

    // calculate xp within current level
    val xpForCurrentLevel = if (level > 1) XP_THRESHOLDS[level - 1] else 0
    val currentLevelXP = totalXP - xpForCurrentLevel
    
    // calculate xp needed to reach next level
    val xpNeededForNextLevel = if (level < XP_THRESHOLDS.size) {
        XP_THRESHOLDS[level] - xpForCurrentLevel
    } else {
        0 // max lvl reached
    }

    // determine title from lvl
    val title = when (level) {
        1, 2 -> "Beginner"
        3, 4 -> "Intermediate"
        5, 6 -> "Advanced"
        else -> "Master"
    }

    return UserLevel(
        level = level,
        title = title,
        currentXP = currentLevelXP,
        xpToNextLevel = xpNeededForNextLevel
    )
}

/**
 * get xp threshold for a specific level
 * @param level to get the threshold for (1-based)
 * @return total xp required to reach that level
 */
fun getXPThreshold(level: Int): Int {
    return XP_THRESHOLDS.getOrNull(level - 1) ?: XP_THRESHOLDS.last()
}

/**
 * get all xp threshold
 * @return list of xp thresholds for each level
 */
fun getXPThresholds(): List<Int> {
    return XP_THRESHOLDS
}
