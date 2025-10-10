package com.example.practal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search

import androidx.compose.material.icons.filled.Home

import androidx.compose.material.icons.filled.Person

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
import com.example.practal.ui.theme.Poppins



val DashboardBg = Color(0xFFD8E6D0)
val CardBg = Color(0xFFFFFFFF)
val CardAccent = Color(0xFF2E5D45)
val SoftGreen = Color(0xFFB7D3B0)


@Composable
fun DashboardScreen(navController: NavHostController, username: String) {
    Scaffold(
        bottomBar = { DashboardBottomBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: handle add action */ },
                containerColor = DarkGreen, // or CardAccent
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
        floatingActionButtonPosition = FabPosition.Center, // ⬅️ centered above bottom bar
        containerColor = DashboardBg
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(DashboardBg)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Good afternoon,", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = Poppins)
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

            Spacer(modifier = Modifier.height(24.dp))

            // Find Friends section
            SectionTitle(title = "Find Friends")
            FriendCard()

            Spacer(modifier = Modifier.height(24.dp))

            // Explore Hangouts section
            SectionTitle(title = "Explore Hangouts")
            HangoutCard()
        }
    }
}


@Composable
fun SectionTitle(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CardAccent)
        Text("See All", fontSize = 14.sp, color = Color.Gray)
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun FriendCard() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardAccent.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("thisstudent", fontWeight = FontWeight.Bold, color = Color.Black)
                Text(
                    "“I’m introducing myself in a quirky way because I’m random like that”",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
                Text("cs2 | he/him", fontSize = 12.sp, color = Color(0xFF6D826D))
            }
        }
    }
}

@Composable
fun HangoutCard() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SoftGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = CardAccent)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Cafe Hopping", fontWeight = FontWeight.Bold, color = Color.Black)
                Text("@walk21", fontSize = 13.sp, color = Color.Gray)
                Text(
                    "username, thisuser, & 10 more liked this hangout",
                    fontSize = 12.sp,
                    color = Color(0xFF6D826D)
                )
            }
        }
    }
}

@Composable
fun DashboardBottomBar() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 6.dp, // subtle shadow
        modifier = Modifier.height(80.dp) // ⬆️ taller bar
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(32.dp), // ⬆️ larger icon
                    tint = DarkGreen
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Explore",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Gray
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Chat",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Gray
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Profile",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Gray
                )
            }
        )
    }
}
