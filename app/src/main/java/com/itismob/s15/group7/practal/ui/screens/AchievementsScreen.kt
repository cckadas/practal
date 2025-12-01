package com.itismob.s15.group7.practal.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itismob.s15.group7.practal.DarkGreen
import com.itismob.s15.group7.practal.LightGreen
import com.itismob.s15.group7.practal.WhiteBox
import com.itismob.s15.group7.practal.domain.controller.AchievementViewModel
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.domain.model.Achievement
import com.itismob.s15.group7.practal.domain.model.UserAchievement
import com.itismob.s15.group7.practal.ui.theme.Poppins
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AchievementsScreen(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel(),
    achievementViewModel: AchievementViewModel = viewModel()
) {
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    val achievements by achievementViewModel.achievements.collectAsState()
    val userAchievements by achievementViewModel.userAchievements.collectAsState()
    val userId = loggedInUser?.id ?: ""
    
    // load and check achievements when screen opens or userId changes
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            loggedInUser?.let { achievementViewModel.checkAndUnlockAchievements(it) }
            achievementViewModel.loadUserAchievements(userId)
        }
    }
    var selectedCategory by remember { mutableStateOf("All") }
    
    val filteredAchievements = when (selectedCategory) {
        "All" -> achievements
        "In Progress" -> {
            val inProgressIds = achievementViewModel.getInProgressAchievements(userId).map { it.achievementId }
            achievements.filter { it.id in inProgressIds }
        }
        else -> achievements.filter { mapCategory(it.category) == selectedCategory }
    }
    
    val unlockedCount = achievementViewModel.getUnlockedAchievements(userId).size
    val earnedXP = achievementViewModel.getTotalAchievementXP(userId)
    val progressPercentage = if (achievements.isNotEmpty()) (unlockedCount * 100 / achievements.size) else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier.size(36.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DarkGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Achievements",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                fontFamily = Poppins
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        DarkGreen.copy(alpha = 0.1f),
                                        LightGreen.copy(alpha = 0.2f)
                                    )
                                )
                            )
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏆", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "$unlockedCount / ${achievements.size} Unlocked",
                            fontSize = 24.sp, fontWeight = FontWeight.Bold,
                            color = DarkGreen, fontFamily = Poppins
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$progressPercentage% Complete",
                            fontSize = 14.sp, color = Color.Gray, fontFamily = Poppins
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { progressPercentage / 100f },
                            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                            color = DarkGreen,
                            trackColor = Color.LightGray.copy(alpha = 0.2f),
                            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            val inProgressCount = achievementViewModel.getInProgressAchievements(userId).size
                            AchievementMiniStat("🔓", unlockedCount.toString(), "Unlocked")
                            AchievementMiniStat("🔒", (achievements.size - unlockedCount).toString(), "Locked")
                            AchievementMiniStat("🎯", inProgressCount.toString(), "In Progress")
                            AchievementMiniStat("⭐", earnedXP.toString(), "XP Earned")
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Filter by Category",
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        color = Color.Gray, fontFamily = Poppins,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(CATEGORIES) { category ->
                            AchievementFilterChip(
                                label = category,
                                emoji = getCategoryEmoji(category),
                                isSelected = selectedCategory == category,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            if (filteredAchievements.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📋", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (achievements.isEmpty()) "Loading achievements..." else "No achievements in this category",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                fontFamily = Poppins,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredAchievements.chunked(2)) { rowAchievements ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowAchievements.forEach { achievement ->
                            AchievementCard(
                                achievement = achievement,
                                isUnlocked = achievementViewModel.isAchievementUnlocked(userId, achievement.id),
                                progress = achievementViewModel.getAchievementProgress(userId, achievement.id),
                                userAchievements = userAchievements,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowAchievements.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

private fun mapCategory(category: String): String = when (category) {
    "practice" -> "Practice"
    "challenge" -> "Challenge"
    "social" -> "Social"
    "special" -> "Special"
    else -> "Practice"
}

@Composable
fun AchievementCard(
    achievement: Achievement,
    isUnlocked: Boolean,
    progress: Int,
    userAchievements: List<UserAchievement>,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val unlockedDate = if (isUnlocked) {
        userAchievements.find { it.achievementId == achievement.id }?.unlockedDate?.toDate()?.let { dateFormat.format(it) }
    } else null
    
    val cardColor = if (isUnlocked) {
        achievementCategoryColor(mapCategory(achievement.category)).copy(alpha = 0.1f)
    } else {
        Color.LightGray.copy(alpha = 0.1f)
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isUnlocked) 6.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().background(cardColor).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp).clip(CircleShape).background(
                    if (isUnlocked) {
                        achievementCategoryColor(mapCategory(achievement.category)).copy(alpha = 0.15f)
                    } else {
                        Color.LightGray.copy(alpha = 0.1f)
                    }
                )
            ) {
                Text(
                    achievement.badgeIcon, fontSize = 42.sp,
                    modifier = Modifier.alpha(if (isUnlocked) 1f else 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                achievement.title, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color = if (isUnlocked) DarkGreen else Color.Gray,
                fontFamily = Poppins, maxLines = 2, textAlign = TextAlign.Center,
                modifier = Modifier.height(36.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                achievement.description, fontSize = 11.sp, color = Color.Gray,
                fontFamily = Poppins, maxLines = 2, textAlign = TextAlign.Center
            )

            if (!isUnlocked && progress > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = achievementCategoryColor(mapCategory(achievement.category)),
                    trackColor = Color.LightGray.copy(alpha = 0.2f),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${progress}% Complete", fontSize = 10.sp,
                    color = Color.Gray, fontFamily = Poppins, fontWeight = FontWeight.Medium
                )
            } else if (isUnlocked && unlockedDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle, "Unlocked",
                        tint = achievementCategoryColor(mapCategory(achievement.category)),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        unlockedDate, fontSize = 10.sp,
                        color = achievementCategoryColor(mapCategory(achievement.category)),
                        fontFamily = Poppins, fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementFilterChip(label: String, emoji: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) DarkGreen else Color.White,
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) DarkGreen else Color.LightGray.copy(alpha = 0.3f)
        ),
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                label, fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else DarkGreen,
                fontFamily = Poppins
            )
        }
    }
}

@Composable
fun AchievementMiniStat(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen, fontFamily = Poppins)
        Text(label, fontSize = 10.sp, color = Color.Gray, fontFamily = Poppins)
    }
}

@Composable
fun ProfileAchievementBadge(emoji: String, title: String, unlockedDate: String, accentColor: Color = DarkGreen) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f))
                .border(2.dp, accentColor.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 32.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            title, fontSize = 12.sp, color = Color.Black,
            fontFamily = Poppins, fontWeight = FontWeight.Medium,
            maxLines = 2, textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            unlockedDate, fontSize = 10.sp, color = Color.Gray,
            fontFamily = Poppins, fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun achievementCategoryColor(category: String): Color = when (category) {
    "Practice" -> DarkGreen
    "Challenge" -> Color(0xFFFFD700)
    "Social" -> Color(0xFF4CAF50)
    "Special" -> Color(0xFFFF6B9D)
    else -> DarkGreen
}

private fun getCategoryEmoji(category: String): String = when (category) {
    "All" -> "🎯"
    "In Progress" -> "⏳"
    "Practice" -> "🎵"
    "Challenge" -> "🏆"
    "Social" -> "⭐"
    "Special" -> "✨"
    else -> "🎯"
}

// constants
private val CATEGORIES = listOf("All", "In Progress", "Practice", "Challenge", "Social", "Special")
