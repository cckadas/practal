package com.itismob.s15.group7.practal.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.itismob.s15.group7.practal.DarkGreen
import com.itismob.s15.group7.practal.LightGreen
import com.itismob.s15.group7.practal.WhiteBox
import com.itismob.s15.group7.practal.domain.controller.AchievementViewModel
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.domain.model.User
import com.itismob.s15.group7.practal.domain.model.Achievement
import com.itismob.s15.group7.practal.domain.model.UserAchievement
import com.itismob.s15.group7.practal.domain.model.UserLevel
import com.itismob.s15.group7.practal.domain.model.computeLevel
import com.itismob.s15.group7.practal.ui.theme.Poppins
import java.text.SimpleDateFormat
import java.util.*





@Composable
fun OtherProfileScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    email: String,
    achievementViewModel: AchievementViewModel = viewModel()
) {
    val visitedUser = userViewModel.getUserByEmail(Uri.decode(email))
    val allAchievements by achievementViewModel.achievements.collectAsState()
    val userAchievements by achievementViewModel.userAchievements.collectAsState()
    
    val userId = visitedUser?.id ?: ""
    
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            achievementViewModel.loadUserAchievements(userId)
        }
    }
    
    // Log level info whenever visited user's XP changes
    LaunchedEffect(visitedUser?.xp) {
        visitedUser?.let { user ->
            val levelInfo = computeLevel(user.xp)
            android.util.Log.d("OtherProfileScreen", "USER LEVEL INFO---------------------")
            android.util.Log.d("OtherProfileScreen", "Viewing User: ${user.email}")
            android.util.Log.d("OtherProfileScreen", "Total XP: ${user.xp}")
            android.util.Log.d("OtherProfileScreen", "Current Level: ${levelInfo.level} (${levelInfo.title})")
            android.util.Log.d("OtherProfileScreen", "XP in Current Level: ${levelInfo.currentXP} / ${levelInfo.xpToNextLevel}")
            android.util.Log.d("OtherProfileScreen", "XP to Next Level: ${levelInfo.xpToNextLevel - levelInfo.currentXP}")
            android.util.Log.d("OtherProfileScreen", "------------------------------------")
        }
    }
    
    val unlockedAchievements = achievementViewModel.getUnlockedAchievements(userId)

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
                OtherProfileHeader(visitedUser, userViewModel)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                OtherProfileInfoSection(visitedUser, userViewModel)
                Spacer(modifier = Modifier.height(24.dp))
            }


            item {
                OtherProfileRecentAchievements(visitedUser, navController, achievementViewModel, userAchievements)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun OtherProfileHeader(visitedUser: User?, userViewModel: UserViewModel) {

    val xp = visitedUser?.xp ?: 0
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
                        if (visitedUser?.image?.isNotEmpty() == true) {
                            AsyncImage(
                                model = visitedUser.image,
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
                    text = "${visitedUser?.firstname} ${visitedUser?.lastname}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = Poppins
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${visitedUser?.introduction}",
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
fun OtherProfileInfoSection(visitedUser: User?, userViewModel: UserViewModel) {

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
                val instrumentList = visitedUser?.preferences ?: emptyList()

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
                val interestList = visitedUser?.interests ?: emptyList()

                items(interestList) { interest ->
                    ProfileInfoChip(text = interest)
                }
            }
        }
    }
}


@Composable
fun OtherProfileRecentAchievements(
    visitedUser: User?,
    navController: NavHostController,
    achievementViewModel: AchievementViewModel,
    userAchievements: List<UserAchievement>
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
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
                    .fillMaxWidth(),
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
