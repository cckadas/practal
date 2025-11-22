package com.itismob.s15.group7.practal.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.itismob.s15.group7.practal.DarkGreen
import com.itismob.s15.group7.practal.LightGreen
import com.itismob.s15.group7.practal.WhiteBox
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.ui.theme.Poppins

data class Achievement(
    val id: Int,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val progress: Int = 100,
    val category: String,
    val unlockedDate: String? = null
)

data class UserLevel(
    val level: Int,
    val title: String,
    val currentXP: Int,
    val xpToNextLevel: Int
)

fun computeLevel(xp: Int): UserLevel {
    val xpThresholds = listOf(
        0,      // Level 1
        100,    // Level 2
        250,    // Level 3
        500,    // Level 4
        1000,   // Level 5
        1500,   // Level 6
        2000    // Level 7
    )

    var level = 1
    for (i in xpThresholds.indices) {
        if (xp >= xpThresholds[i]) {
            level = i + 1
        } else break
    }

    val xpToNext =
        if (level < xpThresholds.size)
            xpThresholds[level] - xp
        else
            0

    val title = when (level) {
        1, 2 -> "Beginner"
        3, 4 -> "Intermediate"
        5, 6 -> "Advanced"
        else -> "Master"
    }

    return UserLevel(
        level = level,
        title = title,
        currentXP = xp,
        xpToNextLevel = xpToNext
    )
}




@Composable
fun UserProfileScreen(navController: NavHostController, userViewModel: UserViewModel) {

    val loggedInUser by userViewModel.loggedInUser.collectAsState()

    val achievements = listOf(
        Achievement(1, "Century Practice", "Practiced for 100+ hours", "🎯", true, 100, "Practice", "Oct 15, 2025"),
        Achievement(2, "30-Day Streak", "Maintained a 30-day practice streak", "🔥", true, 100, "Consistency", "Oct 10, 2025"),
        Achievement(3, "Challenge Master", "Completed 10 challenges", "🏆", true, 100, "Challenges", "Oct 5, 2025"),
        Achievement(4, "Technique Pro", "Mastered 5 techniques", "⚡", true, 100, "Mastery", "Sep 28, 2025"),
        Achievement(5, "Repertoire Complete", "Learned 20 pieces", "🎼", false, 65, "Repertoire", null),
        Achievement(6, "Community Star", "100+ community engagements", "⭐", false, 80, "Community", null),
        Achievement(7, "Speed Demon", "Practiced 7 days in a row", "⚡", true, 100, "Consistency", "Sep 20, 2025"),
        Achievement(8, "Night Owl", "Practiced after midnight 10 times", "🦉", false, 40, "Special", null),
        Achievement(9, "Early Bird", "Practiced before 6 AM 10 times", "🌅", false, 20, "Special", null),
        Achievement(10, "Social Butterfly", "Followed 50 musicians", "🦋", true, 100, "Community", "Sep 15, 2025")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                tint = DarkGreen,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }


                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Profile",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = Poppins
                    )
                }

                IconButton(
                    onClick = { /* TODO: Navigate to settings */ },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                ProfileHeader(userViewModel)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                ProfileInfoSection(userViewModel)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                ProfileRecentAchievements(navController, achievements)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


@Composable
fun ProfileHeader(userViewModel: UserViewModel) {

    val loggedInUser by userViewModel.loggedInUser.collectAsState()


    val xp = loggedInUser?.xp ?: 0
    val levelInfo = computeLevel(xp)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DarkGreen, CardAccent)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (loggedInUser?.image?.isNotEmpty() == true) {
                            AsyncImage(
                                model = loggedInUser!!.image,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile Picture",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = (-2).dp, y = (-2).dp)
                            .clip(CircleShape)
                            .background(LightGreen)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${levelInfo.level}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen,
                            fontFamily = Poppins
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${loggedInUser?.firstname} ${loggedInUser?.lastname}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = Poppins
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${loggedInUser?.introduction}",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    fontFamily = Poppins,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhiteBox)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = levelInfo.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGreen,
                        fontFamily = Poppins
                    )
                    Text(
                        text = "${levelInfo.currentXP} / ${levelInfo.xpToNextLevel} XP",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                }
                
                Text(
                    text = "Level ${levelInfo.level}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
                    fontFamily = Poppins
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
            progress = { levelInfo.currentXP.toFloat() / levelInfo.xpToNextLevel.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)
                ),
                color = DarkGreen,
                trackColor = Color.LightGray.copy(alpha = 0.3f),
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${levelInfo.xpToNextLevel - levelInfo.currentXP} XP to Level ${levelInfo.level + 1}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
                
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "XP",
                    tint = LightGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


@Composable
fun ProfileInfoSection(userViewModel: UserViewModel) {

    val loggedInUser by userViewModel.loggedInUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Instruments",
                    tint = DarkGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Instruments",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
                    fontFamily = Poppins
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val instrumentList = loggedInUser?.preferences ?: emptyList()

                items(instrumentList) { instrument ->
                    ProfileInfoChip(text = instrument)
                }
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Interests",
                    tint = DarkGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Musical Interests",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
                    fontFamily = Poppins
                )
            }

            Spacer(modifier = Modifier.height(12.dp))


            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val interestList = loggedInUser?.interests ?: emptyList()

                items(interestList) { interest ->
                    ProfileInfoChip(text = interest)
                }
            }
        }
    }
}

@Composable
fun ProfileInfoChip(text: String) {
    Surface(
        color = LightGreen.copy(alpha = 0.2f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, LightGreen.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = DarkGreen,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}


@Composable
fun ProfileRecentAchievements(navController: NavHostController, allAchievements: List<Achievement>) {
    val recentAchievements = allAchievements.filter { it.isUnlocked }.take(6)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("all_achievements") },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Achievements",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
                    fontFamily = Poppins
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View all",
                    tint = DarkGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentAchievements) { achievement ->
                    ProfileAchievementBadge(
                        emoji = achievement.icon,
                        title = achievement.title,
                        unlockedDate = achievement.unlockedDate ?: "",
                        accentColor = achievementCategoryColor(achievement.category)
                    )
                }
            }
        }
    }
}

@Composable
fun achievementCategoryColor(category: String): Color {
    return when (category) {
        "Practice" -> DarkGreen
        "Consistency" -> Color(0xFFE07B39)
        "Challenges" -> Color(0xFFFFD700)
        "Mastery" -> Color(0xFF9C27B0)
        "Repertoire" -> Color(0xFF2196F3)
        "Community" -> Color(0xFF4CAF50)
        "Special" -> Color(0xFFFF6B9D)
        else -> DarkGreen
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
            Text(
                text = emoji,
                fontSize = 32.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.Black,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = unlockedDate,
            fontSize = 10.sp,
            color = Color.Gray,
            fontFamily = Poppins,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AchievementsScreen(navController: NavHostController) {
    val achievements = listOf(
        Achievement(1, "Century Practice", "Practiced for 100+ hours", "🎯", true, 100, "Practice", "Oct 15, 2025"),
        Achievement(2, "30-Day Streak", "Maintained a 30-day practice streak", "🔥", true, 100, "Consistency", "Oct 10, 2025"),
        Achievement(3, "Challenge Master", "Completed 10 challenges", "🏆", true, 100, "Challenges", "Oct 5, 2025"),
        Achievement(4, "Technique Pro", "Mastered 5 techniques", "⚡", true, 100, "Mastery", "Sep 28, 2025"),
        Achievement(5, "Repertoire Complete", "Learned 20 pieces", "🎼", false, 65, "Repertoire", null),
        Achievement(6, "Community Star", "100+ community engagements", "⭐", false, 80, "Community", null),
        Achievement(7, "Speed Demon", "Practiced 7 days in a row", "⚡", true, 100, "Consistency", "Sep 20, 2025"),
        Achievement(8, "Night Owl", "Practiced after midnight 10 times", "🦉", false, 40, "Special", null),
        Achievement(9, "Early Bird", "Practiced before 6 AM 10 times", "🌅", false, 20, "Special", null),
        Achievement(10, "Social Butterfly", "Followed 50 musicians", "🦋", true, 100, "Community", "Sep 15, 2025")
    )
    
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Practice", "Consistency", "Challenges", "Mastery", "Community", "Special")
    
    val filteredAchievements = if (selectedCategory == "All") {
        achievements
    } else {
        achievements.filter { it.category == selectedCategory }
    }
    
    val unlockedCount = achievements.count { it.isUnlocked }
    val totalCount = achievements.size
    val progressPercentage = (unlockedCount.toFloat() / totalCount * 100).toInt()

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
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
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
                        Text(
                            text = "🏆",
                            fontSize = 48.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        

                        Text(
                            text = "$unlockedCount / $totalCount Unlocked",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen,
                            fontFamily = Poppins
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "$progressPercentage% Complete",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontFamily = Poppins
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        

                        LinearProgressIndicator(
                        progress = { progressPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)
                            ),
                            color = DarkGreen,
                            trackColor = Color.LightGray.copy(alpha = 0.2f),
                            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AchievementMiniStat("🔓", unlockedCount.toString(), "Unlocked")
                            AchievementMiniStat("⏳", (totalCount - unlockedCount).toString(), "Locked")
                            AchievementMiniStat("⭐", "${unlockedCount * 10}", "XP Earned")
                        }
                    }
                }
            }


            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Filter by Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            AchievementFilterChip(
                                label = category,
                                emoji = achievementCategoryEmoji(category),
                                isSelected = selectedCategory == category,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }
                }
            }


            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            items(filteredAchievements.chunked(2)) { rowAchievements ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowAchievements.forEach { achievement ->
                        AchievementCard(
                            achievement = achievement,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (rowAchievements.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement, modifier: Modifier = Modifier) {
    val cardColor = if (achievement.isUnlocked) {
        achievementCategoryColor(achievement.category).copy(alpha = 0.1f)
    } else {
        Color.LightGray.copy(alpha = 0.1f)
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (achievement.isUnlocked) 6.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isUnlocked) {
                            achievementCategoryColor(achievement.category).copy(alpha = 0.15f)
                        } else {
                            Color.LightGray.copy(alpha = 0.1f)
                        }
                    )
            ) {
                Text(
                    text = achievement.icon,
                    fontSize = 42.sp,
                    modifier = Modifier.alpha(if (achievement.isUnlocked) 1f else 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = achievement.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (achievement.isUnlocked) DarkGreen else Color.Gray,
                fontFamily = Poppins,
                maxLines = 2,
                textAlign = TextAlign.Center,
                modifier = Modifier.height(36.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = achievement.description,
                fontSize = 11.sp,
                color = Color.Gray,
                fontFamily = Poppins,
                maxLines = 2,
                textAlign = TextAlign.Center
            )

            if (!achievement.isUnlocked && achievement.progress > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                    progress = { achievement.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)
                        ),
                        color = achievementCategoryColor(achievement.category),
                        trackColor = Color.LightGray.copy(alpha = 0.2f),
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${achievement.progress}% Complete",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium
                )
            } else if (achievement.isUnlocked && achievement.unlockedDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Unlocked",
                        tint = achievementCategoryColor(achievement.category),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = achievement.unlockedDate,
                        fontSize = 10.sp,
                        color = achievementCategoryColor(achievement.category),
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium
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
            Text(
                text = emoji,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else DarkGreen,
                fontFamily = Poppins
            )
        }
    }
}


@Composable
fun AchievementMiniStat(emoji: String, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
            fontFamily = Poppins
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Gray,
            fontFamily = Poppins
        )
    }
}


@Composable
fun achievementCategoryEmoji(category: String): String {
    return when (category) {
        "All" -> "🎯"
        "Practice" -> "🎵"
        "Consistency" -> "🔥"
        "Challenges" -> "🏆"
        "Mastery" -> "⚡"
        "Community" -> "⭐"
        "Special" -> "✨"
        else -> "🎯"
    }
}