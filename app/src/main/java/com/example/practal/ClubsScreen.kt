package com.example.practal

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practal.ui.theme.Poppins

data class Club(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val memberCount: Int,
    val emoji: String,
    val isMember: Boolean = false
)

@Composable
fun ClubsScreen(padding: PaddingValues) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Genre", "Instrument", "Skill Level", "Location", "Activity")
    
    val clubs = listOf(
        Club(1, "Jazz Enthusiasts", "For lovers of jazz music and improvisation", "Genre", 1240, "🎷", true),
        Club(2, "Piano Masters", "Classical and contemporary piano players", "Instrument", 856, "🎹", true),
        Club(3, "Rock & Roll Club", "Rock music fans and performers", "Genre", 2103, "🎸", false),
        Club(4, "Beginner's Circle", "Just starting your musical journey? Join us!", "Skill Level", 3421, "🌱", false),
        Club(5, "Violin Virtuosos", "String players unite", "Instrument", 645, "🎻", false),
        Club(6, "Classical Music Society", "Appreciate the classics", "Genre", 1872, "🎼", true),
        Club(7, "Drummers United", "Rhythm section headquarters", "Instrument", 934, "🥁", false),
        Club(8, "Daily Practice Squad", "Commit to practicing every day", "Activity", 5234, "💪", true),
        Club(9, "Blues & Soul", "Blues, soul, and R&B community", "Genre", 1456, "🎺", false),
        Club(10, "Guitar Guild", "All things guitar", "Instrument", 3210, "🎸", false)
    )
    
    val filteredClubs = if (selectedCategory == "All") {
        clubs
    } else {
        clubs.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(WhiteBox)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen)
                .padding(horizontal = 16.dp, vertical = 24.dp)
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

        // Filter Categories
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

        // My Clubs Section
        val myClubs = clubs.filter { it.isMember }
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
                    MyClubCard(club)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Discover Clubs Section
        Text(
            text = "Discover Clubs",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
            fontFamily = Poppins,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredClubs) { club ->
                ClubCard(club)
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
        border = androidx.compose.foundation.BorderStroke(
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
fun MyClubCard(club: Club) {
    Card(
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
fun ClubCard(club: Club) {
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
            // Club Icon
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
            
            // Club Info
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
            
            // Join Button
            Button(
                onClick = { /* TODO: Handle join/leave */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (club.isMember) Color.White else DarkGreen,
                    contentColor = if (club.isMember) DarkGreen else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(36.dp)
                    .then(
                        if (club.isMember) Modifier.border(1.dp, DarkGreen, RoundedCornerShape(12.dp))
                        else Modifier
                    ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Text(
                    text = if (club.isMember) "Joined" else "Join",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
            }
        }
    }
}
