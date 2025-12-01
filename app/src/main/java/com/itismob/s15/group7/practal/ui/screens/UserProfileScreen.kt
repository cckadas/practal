package com.itismob.s15.group7.practal.ui.screens

import android.util.Log
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
import com.itismob.s15.group7.practal.domain.controller.AchievementViewModel
import com.itismob.s15.group7.practal.domain.model.Achievement
import com.itismob.s15.group7.practal.domain.model.UserAchievement
import com.itismob.s15.group7.practal.domain.model.UserLevel
import com.itismob.s15.group7.practal.domain.model.computeLevel
import com.itismob.s15.group7.practal.ui.theme.Poppins
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Locale




@Composable
fun UserProfileScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    achievementViewModel: AchievementViewModel = viewModel()
) {
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    val allAchievements by achievementViewModel.achievements.collectAsState()
    val userAchievements by achievementViewModel.userAchievements.collectAsState()
    
    val userId = loggedInUser?.id ?: ""
    
    // refresh user data every time screen composition happens (to get latest XP)
    LaunchedEffect(key1 = true) {
        Log.d("UserProfileScreen", "Screen refreshing - userId: $userId")
        userViewModel.getUserList()
    }
    
    // load achievements when userId changes
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            achievementViewModel.loadUserAchievements(userId)
        }
    }
    
    // log level info and check achievements whenever user XP changes
    LaunchedEffect(loggedInUser?.xp) {
        loggedInUser?.let { user ->
            val levelInfo = computeLevel(user.xp)
            Log.d("UserProfileScreen", "USER LEVEL INFO---------------------")
            Log.d("UserProfileScreen", "User: ${user.email}")
            Log.d("UserProfileScreen", "Total XP: ${user.xp}")
            Log.d("UserProfileScreen", "Current Level: ${levelInfo.level} (${levelInfo.title})")
            Log.d("UserProfileScreen", "XP in Current Level: ${levelInfo.currentXP} / ${levelInfo.xpToNextLevel}")
            Log.d("UserProfileScreen", "XP to Next Level: ${levelInfo.xpToNextLevel - levelInfo.currentXP}")
            Log.d("UserProfileScreen", "------------------------------------")
            
            Log.d("UserProfileScreen", "Checking achievements for user: ${user.email}")
            achievementViewModel.checkAndUnlockAchievements(user)
        }
    }
    
    val unlockedAchievements = achievementViewModel.getUnlockedAchievements(userId)
    
    LaunchedEffect(allAchievements.size, userAchievements.size, unlockedAchievements.size) {
        Log.d("UserProfileScreen", "Total achievements: ${allAchievements.size}, User achievements: ${userAchievements.size}")
        Log.d("UserProfileScreen", "Unlocked: ${unlockedAchievements.size}")
        Log.d("UserProfileScreen", "UserAchievements details:")
        userAchievements.forEach { ua ->
            Log.d("UserProfileScreen", "  - achievementId: ${ua.achievementId}, completed: ${ua.completed}, progress: ${ua.progress}")
        }
    }

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
                ProfileRecentAchievements(navController, achievementViewModel, userAchievements)
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
fun ProfileRecentAchievements(
    navController: NavHostController,
    achievementViewModel: AchievementViewModel,
    userAchievements: List<com.itismob.s15.group7.practal.domain.model.UserAchievement>
) {
    val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
    val recentUnlocked = userAchievements
        .filter { it.completed }
        .sortedByDescending { it.unlockedDate }
        .take(6)
        .mapNotNull { ua ->
            achievementViewModel.getAchievementById(ua.achievementId)?.let { achievement ->
                Triple(achievement, ua, ua.unlockedDate?.toDate()?.let { dateFormat.format(it) } ?: "")
            }
        }
    
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
                items(recentUnlocked) { (achievement, _, unlockedDate) ->
                    ProfileAchievementBadge(
                        emoji = achievement.badgeIcon,
                        title = achievement.title,
                        unlockedDate = unlockedDate,
                        accentColor = achievementCategoryColor(when (achievement.category) {
                            "practice" -> "Practice"
                            "challenge" -> "Challenge"
                            "social" -> "Social"
                            "special" -> "Special"
                            else -> "Practice"
                        })
                    )
                }
            }
        }
    }
}