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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.itismob.s15.group7.practal.DarkGreen
import com.itismob.s15.group7.practal.LightGreen
import com.itismob.s15.group7.practal.WhiteBox
import com.itismob.s15.group7.practal.domain.controller.ClubViewModel
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.domain.model.Club
import com.itismob.s15.group7.practal.ui.theme.Poppins
import kotlinx.coroutines.launch

@Composable
fun ClubsScreen(
    navController: NavHostController,
    clubViewModel: ClubViewModel,
    userViewModel: UserViewModel
) {
    val clubs by clubViewModel.clubs.collectAsState()
    val userClubIds by clubViewModel.userClubs.collectAsState()
    val currentUser by userViewModel.loggedInUser.collectAsState()
    val scope = rememberCoroutineScope()
    
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Genre", "Instrument", "Skill Level", "Activity")
    
    // load user clubs on screen
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { userId ->
            clubViewModel.loadUserClubs(userId)
        }
    }
    
    val filteredClubs = clubViewModel.getClubsByCategory(selectedCategory)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .height(64.dp)
        ) {
            Text(
                text = "Clubs",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = Poppins
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Join communities and connect with musicians",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                fontFamily = Poppins
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            items(categories) { category ->
                ClubFilterChip(
                    label = category,
                    isSelected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        val myClubs = clubs.filter { userClubIds.contains(it.id) }
        if (myClubs.isNotEmpty()) {
            Text(
                text = "My Clubs (${myClubs.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                fontFamily = Poppins,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(myClubs) { club ->
                    MyClubCard(
                        club = club,
                        onClick = { navController.navigate("club_detail/${club.id}") }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }


        Text(
            text = "Discover Clubs",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
            fontFamily = Poppins,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 92.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredClubs) { club ->
                val isMember = userClubIds.contains(club.id)
                ClubCard(
                    club = club,
                    isMember = isMember,
                    onJoinClick = {
                        scope.launch {
                            currentUser?.id?.let { userId ->
                                if (isMember) {
                                    clubViewModel.leaveClub(userId, club.id)
                                } else {
                                    clubViewModel.joinClub(userId, club.id)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ClubFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) DarkGreen else Color.White,
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) DarkGreen else Color.LightGray.copy(alpha = 0.3f)
        )
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else DarkGreen,
            fontFamily = Poppins,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun MyClubCard(club: Club, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(140.dp)
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = club.emoji,
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = club.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                fontFamily = Poppins,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${club.memberCount} members",
                fontSize = 10.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
        }
    }
}

@Composable
fun ClubCard(
    club: Club,
    isMember: Boolean,
    onJoinClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(LightGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = club.emoji,
                    fontSize = 30.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = club.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
                    fontFamily = Poppins
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = club.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = Poppins,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Members",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${club.memberCount}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        color = LightGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = club.category,
                            fontSize = 10.sp,
                            color = DarkGreen,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            

            Button(
                onClick = onJoinClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMember) Color.White else DarkGreen,
                    contentColor = if (isMember) DarkGreen else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(36.dp)
                    .then(
                        if (isMember) Modifier.Companion.border(1.dp,
                            DarkGreen, RoundedCornerShape(12.dp))
                        else Modifier
                    ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Text(
                    text = if (isMember) "Joined" else "Join",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
            }
        }
    }
}
