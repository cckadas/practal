package com.example.practal

import LeaderboardScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
        bottomBar = { DashboardBottomBar(selectedTab) { selectedTab = it } },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: handle add action */ },
                containerColor = DarkGreen,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = WhiteBox
    ) { padding ->
        when (selectedTab) {
            "home" -> DashboardHome(username, padding, navController)
            "leaderboard" -> LeaderboardScreen(padding)
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
                modifier = Modifier.size(48.dp)
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

            item {
                ViewChallengeCard(navController)
                Spacer(modifier = Modifier.height(16.dp))
            }


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
                            Icons.Default.Send,
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
fun DashboardBottomBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 6.dp,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = selectedTab == "home",
            onClick = { onTabSelected("home") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(32.dp),
                    tint = if (selectedTab == "home") DarkGreen else Color.Gray
                )
            }
        )
        NavigationBarItem(
            selected = selectedTab == "progress",
            onClick = { onTabSelected("progress") },
            icon = {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Progress",
                    modifier = Modifier.size(32.dp),
                    tint = if (selectedTab == "progress") DarkGreen else Color.Gray
                )
            }
        )
        NavigationBarItem(
            selected = selectedTab == "leaderboard",
            onClick = { onTabSelected("leaderboard") },
            icon = {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Leaderboard",
                    modifier = Modifier.size(32.dp),
                    tint = if (selectedTab == "leaderboard") DarkGreen else Color.Gray
                )
            }
        )
        NavigationBarItem(
            selected = selectedTab == "friends",
            onClick = { onTabSelected("friends") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Friends",
                    modifier = Modifier.size(32.dp),
                    tint = if (selectedTab == "friends") DarkGreen else Color.Gray
                )
            }
        )
    }
}
