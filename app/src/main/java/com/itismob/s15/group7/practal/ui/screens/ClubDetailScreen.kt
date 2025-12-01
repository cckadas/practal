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
import com.itismob.s15.group7.practal.domain.controller.ClubViewModel
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.domain.model.User
import com.itismob.s15.group7.practal.ui.theme.Poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDetailScreen(
    navController: NavHostController,
    clubViewModel: ClubViewModel,
    userViewModel: UserViewModel,
    clubId: String
) {
    val clubs by clubViewModel.clubs.collectAsState()
    val clubMembers by clubViewModel.clubMembers.collectAsState()
    val club = clubs.find { it.id == clubId }
    
    LaunchedEffect(clubId) {
        clubViewModel.loadClubMembers(clubId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(club?.name ?: "Club", fontFamily = Poppins, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { 
                        navController.navigate("clubs") {
                            popUpTo("clubs") { inclusive = true }
                        }
                    }) {
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
        if (club == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Club not found", fontFamily = Poppins)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // club header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(LightGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = club.emoji,
                                    fontSize = 48.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = club.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreen,
                                fontFamily = Poppins
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = club.description,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = Poppins
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${club.memberCount}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkGreen,
                                        fontFamily = Poppins
                                    )
                                    Text(
                                        text = "Members",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        fontFamily = Poppins
                                    )
                                }
                                
                                if (club.isOfficial) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "Official",
                                            tint = DarkGreen,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            text = "Official",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            fontFamily = Poppins
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // members section
                item {
                    Text(
                        text = "Members (${clubMembers.size})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen,
                        fontFamily = Poppins
                    )
                }
                
                items(clubMembers) { member ->
                    MemberCard(
                        member = member,
                        onClick = {
                            if (member.email != userViewModel.loggedInUser.value?.email) {
                                val encodedEmail = java.net.URLEncoder.encode(member.email, "UTF-8")
                                navController.navigate("other_profile/$encodedEmail")
                            } else {
                                navController.navigate("profile")
                            }
                        }
                    )
                }
                
                if (clubMembers.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No members yet",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = Poppins
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemberCard(member: User, onClick: () -> Unit) {
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
            if (member.image.isNotEmpty()) {
                AsyncImage(
                    model = member.image,
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(LightGreen.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = DarkGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${member.firstname} ${member.lastname}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen,
                    fontFamily = Poppins
                )
                
                if (member.skill_level.isNotEmpty()) {
                    Text(
                        text = member.skill_level.replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Level ${member.level}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGreen,
                        fontFamily = Poppins
                    )
                    Text(
                        text = "${member.xp} XP",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                }
                
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "View profile",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
