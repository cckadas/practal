package com.example.practal

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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.practal.ui.theme.Poppins


val CardAccent = Color(0xFF2E5D45)
val SoftGreen = Color(0xFFB7D3B0)

data class LeaderboardEntry(val username: String, val score: Int)

data class Post(
    val username: String,
    val caption: String,
    val time: String,
    val likes: Int,
    val comments: Int
)


@Composable
fun DashboardScreen(navController: NavHostController, username: String) {
    var selectedTab by remember { mutableStateOf("home") }

    Scaffold(
        bottomBar = { DashboardBottomBar(selectedTab, navController) { selectedTab = it } },
        containerColor = WhiteBox
    ) { padding ->
        when (selectedTab) {
            "home" -> DashboardHome(username, padding, navController)
            "progress" -> ProgressAnalyticsScreen(username, padding)
            "leaderboard" -> LeaderboardScreen(padding)
            "clubs" -> ClubsScreen(padding)
            else -> DashboardHome(username, padding, navController)
        }
    }
}


@Composable
fun DashboardHome(username: String, padding: PaddingValues, navController: NavHostController) {
    val posts = listOf(
        Post("mozart123", "Just practicing a new chord", "2h", 128, 34),
        Post("real_sophia", "Hi", "5h", 89, 12),
        Post("musiclover", "New concept art piece 🎨", "1d", 210, 58)
    )

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(WhiteBox)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Good afternoon,",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = Poppins
                )
                Text(
                    "@$username!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CardAccent,
                    fontFamily = Poppins
                )
            }

            Surface(
                shape = CircleShape,
                color = WhiteBox,
                modifier = Modifier
                    .size(48.dp)
                    .clickable { navController.navigate("profile/$username") }
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = CardAccent,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 12.dp, end = 12.dp)
        ) {


            // XP Widget
            item {
                XPWidget()
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Challenge Widget
            item {
                ViewChallengeCard(navController)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Posts Filter Header
            item {
                PostsFilterHeader()
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Posts
            items(posts) { post ->
                PostCard(post)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@Composable
fun ViewChallengeCard(navController: NavHostController) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = CardAccent,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Available Challenges 🔥",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = Poppins
                )
                Text(
                    text = "Test your skills & Earn points!",
                    fontSize = 14.sp,
                    color = Color(0xFFE1FFE1),
                    fontFamily = Poppins
                )
            }

            Button(
                onClick = { navController.navigate("challenges") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "View",
                    color = CardAccent,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
            }
        }
    }
}


@Composable
fun PostCard(post: Post) {
    var liked by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 👤 Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CardAccent.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black,
                        fontFamily = Poppins
                    )
                    Text(
                        text = post.time,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                IconButton(onClick = { /* TODO: show post options */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.Gray)
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(SoftGreen.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = "Post Image",
                    tint = CardAccent.copy(alpha = 0.7f),
                    modifier = Modifier.size(64.dp)
                )
            }


            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = post.caption,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontFamily = Poppins
                )
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { liked = !liked }) {
                        Icon(
                            imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (liked) Color.Red else Color.Gray
                        )
                    }
                    IconButton(onClick = { /* TODO: Comment */ }) {
                        Icon(
                            Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = Color.Gray
                        )
                    }
                    IconButton(onClick = { /* TODO: Share */ }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Share",
                            tint = Color.Gray
                        )
                    }
                }

                Text(
                    text = "${post.likes + if (liked) 1 else 0} likes",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }


            Text(
                text = "View all ${post.comments} comments",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// 🧭 BOTTOM NAVIGATION BAR
@Composable
fun DashboardBottomBar(selectedTab: String, navController: NavHostController, onTabSelected: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0f),
                            Color.White.copy(alpha = 0.3f),
                            Color.White
                        )
                    )
                )
        )
        
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 6.dp,
            modifier = Modifier
                .height(80.dp)
                .align(Alignment.BottomCenter)
        ) {
            // Home
            NavigationBarItem(
                selected = selectedTab == "home",
                onClick = { onTabSelected("home") },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(28.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DarkGreen,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = DarkGreen.copy(alpha = 0.1f)
                )
            )
            
            // Progress
            NavigationBarItem(
                selected = selectedTab == "progress",
                onClick = { onTabSelected("progress") },
                icon = {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Progress",
                        modifier = Modifier.size(28.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DarkGreen,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = DarkGreen.copy(alpha = 0.1f)
                )
            )
            
            // Center space for FAB
            Spacer(modifier = Modifier.weight(1f))
            
            // Leaderboard
            NavigationBarItem(
                selected = selectedTab == "leaderboard",
                onClick = { onTabSelected("leaderboard") },
                icon = {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Leaderboard",
                        modifier = Modifier.size(28.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DarkGreen,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = DarkGreen.copy(alpha = 0.1f)
                )
            )
            
            // Clubs
            NavigationBarItem(
                selected = selectedTab == "clubs",
                onClick = { onTabSelected("clubs") },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Clubs",
                        modifier = Modifier.size(28.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DarkGreen,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = DarkGreen.copy(alpha = 0.1f)
                )
            )
        }
        
        // Center Log Practice Button
        Box(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.TopCenter)
                .offset(y = 4.dp)
                .clip(CircleShape)
                .background(DarkGreen)
                .clickable { navController.navigate("logPractice") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Log Practice",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// XP Widget
@Composable
fun XPWidget() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkGreen),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Level",
                        tint = LightGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Level 12",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = Poppins
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                progress = { 0.86f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)
                    ),
                    color = LightGreen,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "3,450 / 4,000 XP",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    fontFamily = Poppins
                )
            }
            
            Text(
                text = " 🎯",
                fontSize = 48.sp
            )
        }
    }
}

// Posts Filter
@Composable
fun PostsFilterHeader() {
    var selectedFilter by remember { mutableStateOf("latest") }
    val selectedGray = Color(0xD2B8BEC7)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Latest Posts option
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (selectedFilter == "latest") selectedGray else Color.Transparent,
            modifier = Modifier.clickable { selectedFilter = "latest" }
        ) {
            Text(
                text = "Latest Posts",
                fontSize = 16.sp,
                fontWeight = if (selectedFilter == "latest") FontWeight.Bold else FontWeight.Normal,
                color = if (selectedFilter == "latest") Color.White else Color.Black,
                fontFamily = Poppins,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Friends Posts
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (selectedFilter == "friends") selectedGray else Color.Transparent,
            modifier = Modifier.clickable { selectedFilter = "friends" }
        ) {
            Text(
                text = "Friends Posts",
                fontSize = 16.sp,
                fontWeight = if (selectedFilter == "friends") FontWeight.Bold else FontWeight.Normal,
                color = if (selectedFilter == "friends") Color.White else Color.Black,
                fontFamily = Poppins,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}