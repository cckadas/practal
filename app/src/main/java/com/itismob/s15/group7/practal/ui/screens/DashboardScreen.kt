package com.itismob.s15.group7.practal.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.itismob.s15.group7.practal.DarkGreen
import com.itismob.s15.group7.practal.WhiteBox
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.domain.controller.PostViewModel
import com.itismob.s15.group7.practal.domain.controller.ClubViewModel
import com.itismob.s15.group7.practal.domain.model.Post
import com.itismob.s15.group7.practal.ui.theme.Poppins
import com.google.firebase.Timestamp
import com.itismob.s15.group7.practal.domain.controller.PracticeSessionViewModel
import java.util.concurrent.TimeUnit

val CardAccent = Color(0xFF2E5D45)
val SoftGreen = Color(0xFFB7D3B0)


fun formatTimeAgo(timestamp: Timestamp): String {
    val now = System.currentTimeMillis()
    val then = timestamp.toDate().time
    val diff = now - then

    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        else -> "${days}d"
    }
}

@Composable
fun DashboardScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    postViewModel: PostViewModel,
    practiceSessionViewModel: PracticeSessionViewModel,
    clubViewModel: ClubViewModel
) {
    var selectedTab by remember { mutableStateOf("home") }
    var showFabMenu by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { DashboardBottomBar(selectedTab) { selectedTab = it } },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Menu options
                if (showFabMenu) {
                    // Create regular post option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp
                        ) {
                            Text(
                                "Create Post",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                        SmallFloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                navController.navigate("create_post")
                            },
                            containerColor = Color.White,
                            contentColor = DarkGreen
                        ) {
                            Icon(Icons.Default.Edit, "Create Post", modifier = Modifier.size(24.dp))
                        }
                    }
                    
                    // Log practice session option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp
                        ) {
                            Text(
                                "Practice Session",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                        SmallFloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                navController.navigate("log_practice")
                            },
                            containerColor = Color.White,
                            contentColor = DarkGreen
                        ) {
                            Icon(Icons.Default.MusicNote, "Log Practice", modifier = Modifier.size(24.dp))
                        }
                    }
                }
                
                // Main FAB
                FloatingActionButton(
                    onClick = { showFabMenu = !showFabMenu },
                    containerColor = DarkGreen,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (showFabMenu) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = if (showFabMenu) "Close" else "Add",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = WhiteBox
    ) { padding ->
        when (selectedTab) {
            "home" -> DashboardHome(padding, navController, userViewModel, postViewModel)
            "progress" -> ProgressAnalyticsScreen(navController, userViewModel, practiceSessionViewModel)
            "leaderboard" -> LeaderboardScreen(navController, userViewModel)
            "clubs" -> ClubsScreen(navController, clubViewModel, userViewModel)
            else -> DashboardHome(padding, navController, userViewModel, postViewModel)
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DashboardHome(
    padding: PaddingValues,
    navController: NavHostController,
    userViewModel: UserViewModel,
    postViewModel: PostViewModel
) {

    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    val posts by postViewModel.postList.collectAsState()

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(WhiteBox)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen)
                .padding(24.dp)
                .height(64.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good Afternoon,",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = Poppins
                    )
                    Text(
                        text = "${loggedInUser?.firstname} ${loggedInUser?.lastname}!",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = Poppins
                    )
                }

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { navController.navigate("profile") },
                    contentAlignment = Alignment.Center,
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
                PostCard(post, userViewModel, postViewModel, navController)
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
fun PostCard(post: Post, userViewModel: UserViewModel, postViewModel: PostViewModel, navController: NavHostController) {
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    var showComments by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable {
                            val encodedEmail = Uri.encode(post.owner_email)
                            navController.navigate("other_profile/$encodedEmail")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (post.owner_img.isNotEmpty()) {
                        AsyncImage(
                            model = post.owner_img,
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
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.owner_email,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black,
                        fontFamily = Poppins
                    )
                    Text(
                        text = formatTimeAgo(post.date_posted),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                IconButton(onClick = { }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.Gray)
                }
            }

            // Content - different based on post type
            when (post) {
                is Post.RegularPost -> {
                    // Regular post with image (only show if image exists)
                    if (post.post_img.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = post.post_img,
                                contentDescription = "Post Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                is Post.PracticePost -> {
                    // session post
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SoftGreen.copy(alpha = 0.15f))
                            .padding(16.dp)
                    ) {
                        // season header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = DarkGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Practice Session",
                                fontWeight = FontWeight.Bold,
                                color = DarkGreen,
                                fontSize = 16.sp,
                                fontFamily = Poppins
                            )
                        }
                        
                        Spacer(Modifier.height(12.dp))
                        
                        // session details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Instrument", fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
                                Text(post.instrument, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = Poppins)
                            }
                            Column {
                                Text("Duration", fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
                                Text("${post.durationMinutes} min", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = Poppins)
                            }
                            Column {
                                Text("Difficulty", fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
                                Text(post.difficulty, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = Poppins)
                            }
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Practice Type", fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
                                Text(post.practiceType, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = Poppins)
                            }
                            Column {
                                Text("XP Earned", fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
                                Text("+${post.earnedXP} XP", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkGreen, fontFamily = Poppins)
                            }
                        }
                        
                        if (post.pieceOrFocus.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Focus", fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
                            Text(post.pieceOrFocus, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = Poppins)
                        }
                        
                        // optional img
                        if (post.post_img.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = post.post_img,
                                    contentDescription = "Practice Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // caption
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            postViewModel.toggleLike(post.id, loggedInUser?.email ?: "")
                        }
                    ) {
                        Icon(
                            imageVector = if (post.likes.contains(loggedInUser?.email ?: ""))
                                Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.likes.contains(loggedInUser?.email ?: "")) Color.Red else Color.Gray
                        )
                    }

                    IconButton(onClick = { showComments = !showComments }) {
                        Icon(
                            Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = Color.Gray
                        )
                    }
                }


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${post.likes.size} likes",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${post.comments.size} comments",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }


            if (showComments) {
                Column(modifier = Modifier.padding(horizontal = 12.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(30.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedBorderColor = DarkGreen,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = DarkGreen,
                                cursorColor = DarkGreen
                            ),
                            maxLines = 3,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    postViewModel.addComment(post.id, commentText, loggedInUser?.email ?: "")
                                    commentText = ""
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = DarkGreen,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Other Comments",
                        fontSize = 16.sp,
                        color = CardAccent,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))


                    post.comments.forEach { comment ->
                        Text(
                            text = comment,
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}




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
            selected = selectedTab == "clubs",
            onClick = { onTabSelected("clubs") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Clubs",
                    modifier = Modifier.size(32.dp),
                    tint = if (selectedTab == "clubs") DarkGreen else Color.Gray
                )
            }
        )
    }
}
