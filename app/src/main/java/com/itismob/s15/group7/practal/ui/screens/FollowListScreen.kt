package com.itismob.s15.group7.practal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.itismob.s15.group7.practal.DarkGreen
import com.itismob.s15.group7.practal.LightGreen
import com.itismob.s15.group7.practal.WhiteBox
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.domain.model.User
import com.itismob.s15.group7.practal.ui.theme.Poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowListScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    userEmail: String,
    listType: String // "followers" or "following"
) {
    val allUsers by userViewModel.userList.collectAsState()
    val targetUser = allUsers.find { it.email == userEmail }
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    
    val userList = when (listType) {
        "followers" -> targetUser?.followers ?: emptyList()
        "following" -> targetUser?.following ?: emptyList()
        else -> emptyList()
    }
    
    val users = allUsers.filter { userList.contains(it.email) }
        .sortedByDescending { it.xp }
    
    val title = when (listType) {
        "followers" -> "Followers"
        "following" -> "Following"
        else -> "Users"
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontFamily = Poppins, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = WhiteBox
    ) { padding ->
        if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonOff,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = if (listType == "followers") "No followers yet" else "Not following anyone yet",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(users) { user ->
                    UserListItem(
                        user = user,
                        isCurrentUser = user.email == loggedInUser?.email,
                        onClick = {
                            if (user.email != loggedInUser?.email) {
                                val encodedEmail = java.net.URLEncoder.encode(user.email, "UTF-8")
                                navController.navigate("other_profile/$encodedEmail")
                            } else {
                                navController.navigate("profile")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserListItem(
    user: User,
    isCurrentUser: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // profile image
            if (user.image.isNotEmpty()) {
                AsyncImage(
                    model = user.image,
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(LightGreen.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = DarkGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${user.firstname} ${user.lastname}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGreen,
                        fontFamily = Poppins
                    )
                    
                    if (isCurrentUser) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DarkGreen.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "You",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreen,
                                fontFamily = Poppins,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (user.skill_level.isNotEmpty()) {
                        Text(
                            text = user.skill_level.replaceFirstChar { it.uppercase() },
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontFamily = Poppins
                        )
                        Text("•", fontSize = 12.sp, color = Color.Gray)
                    }
                    
                    Text(
                        text = "Level ${user.level}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "XP",
                        tint = LightGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${user.xp}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen,
                        fontFamily = Poppins
                    )
                }
                
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "View profile",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
