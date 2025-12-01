package com.itismob.s15.group7.practal.ui.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
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
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
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
    val userList by userViewModel.userList.collectAsState()
    val targetUser = userList.find { it.email == Uri.decode(email) }
    val allAchievements by achievementViewModel.achievements.collectAsState()
    val userAchievements by achievementViewModel.userAchievements.collectAsState()
    
    val userId = targetUser?.id ?: ""
    
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            achievementViewModel.loadUserAchievements(userId)
        }
    }
    
    // Log level info whenever visited user's XP changes
    LaunchedEffect(targetUser?.xp) {
        targetUser?.let { user ->
            val levelInfo = computeLevel(user.xp)
            Log.d("OtherProfileScreen", "USER LEVEL INFO---------------------")
            Log.d("OtherProfileScreen", "Viewing User: ${user.email}")
            Log.d("OtherProfileScreen", "Total XP: ${user.xp}")
            Log.d("OtherProfileScreen", "Current Level: ${levelInfo.level} (${levelInfo.title})")
            Log.d("OtherProfileScreen", "XP in Current Level: ${levelInfo.currentXP} / ${levelInfo.xpToNextLevel}")
            Log.d("OtherProfileScreen", "XP to Next Level: ${levelInfo.xpToNextLevel - levelInfo.currentXP}")
            Log.d("OtherProfileScreen", "------------------------------------")
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
                OtherProfileHeader(targetUser, userViewModel, navController)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                OtherProfileInfoSection(targetUser, userViewModel)
                Spacer(modifier = Modifier.height(24.dp))
            }


            item {
                OtherProfileRecentAchievements(targetUser, navController, achievementViewModel, userAchievements)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun OtherProfileHeader(targetUser: User?, userViewModel: UserViewModel, navController: NavHostController) {
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    val scope = rememberCoroutineScope()
    
    val isFollowing by remember(loggedInUser, targetUser?.email) {
        derivedStateOf {
            loggedInUser?.following?.contains(targetUser?.email) ?: false
        }
    }
    var isLoading by remember { mutableStateOf(false) }
    
    // Log following state changes
    LaunchedEffect(isFollowing, targetUser?.email) {
        if (targetUser != null) {
            Log.d("OtherProfileScreen", "Following state updated - isFollowing=$isFollowing for ${targetUser.email}")
            Log.d("OtherProfileScreen", "Current following list: ${loggedInUser?.following}")
        }
    }

    val xp = targetUser?.xp ?: 0
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
                        if (targetUser?.image?.isNotEmpty() == true) {
                            AsyncImage(
                                model = targetUser.image,
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
                    text = "${targetUser?.firstname} ${targetUser?.lastname}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = Poppins
                )

                Spacer(modifier = Modifier.height(12.dp))
                
                // follower/following stats
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            targetUser?.email?.let { email ->
                                navController.navigate("follow_list/$email/followers")
                            }
                        }
                    ) {
                        Text(
                            text = "${targetUser?.followers?.size ?: 0}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = Poppins
                        )
                        Text(
                            text = "Followers",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontFamily = Poppins
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            targetUser?.email?.let { email ->
                                navController.navigate("follow_list/$email/following")
                            }
                        }
                    ) {
                        Text(
                            text = "${targetUser?.following?.size ?: 0}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = Poppins
                        )
                        Text(
                            text = "Following",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontFamily = Poppins
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${targetUser?.introduction}",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    fontFamily = Poppins,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // follow/unfollow button
                Button(
                    onClick = {
                        val currentUser = loggedInUser
                        if (!isLoading && currentUser != null && targetUser != null) {
                            isLoading = true
                            Log.d("OtherProfileScreen", "Follow button clicked - Current state: isFollowing=$isFollowing")
                            Log.d("OtherProfileScreen", "Current user: ${currentUser.email}, Target user: ${targetUser.email}")
                            scope.launch {
                                val success = if (isFollowing) {
                                    Log.d("OtherProfileScreen", "Attempting to unfollow ${targetUser.email}")
                                    userViewModel.unfollowUser(currentUser.email, targetUser.email)
                                } else {
                                    Log.d("OtherProfileScreen", "Attempting to follow ${targetUser.email}")
                                    userViewModel.followUser(currentUser.email, targetUser.email)
                                }
                                Log.d("OtherProfileScreen", "Follow/unfollow operation completed - Success: $success")
                                isLoading = false
                            }
                        } else {
                            Log.w("OtherProfileScreen", "Follow button click ignored - isLoading=$isLoading, currentUser=${currentUser != null}, targetUser=${targetUser != null}")
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowing) Color.White else DarkGreen,
                        contentColor = if (isFollowing) DarkGreen else Color.White
                    ),
                    border = if (isFollowing) BorderStroke(2.dp, DarkGreen) else null
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = if (isFollowing) DarkGreen else Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.PersonAdd,
                                contentDescription = if (isFollowing) "Following" else "Follow",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isFollowing) "Following" else "Follow",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Poppins
                            )
                        }
                    }
                }
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
fun OtherProfileInfoSection(targetUser: User?, userViewModel: UserViewModel) {

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
                val instrumentList = targetUser?.preferences ?: emptyList()

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
                val interestList = targetUser?.interests ?: emptyList()

                items(interestList) { interest ->
                    ProfileInfoChip(text = interest)
                }
            }
        }
    }
}


@Composable
fun OtherProfileRecentAchievements(
    targetUser: User?,
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
