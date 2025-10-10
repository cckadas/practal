package com.example.practal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.practal.ui.theme.Poppins

// Medal colors
val Gold = Color(0xFFFFD700)
val Silver = Color(0xFFC0C0C0)
val Bronze = Color(0xFFCD7F32)

data class LeaderboardEntry(
    val username: String,
    val score: Int
)

@Composable
fun LeaderboardScreen(navController: NavHostController) {
    val leaderboard = listOf(
        LeaderboardEntry("thisstudent", 980),
        LeaderboardEntry("codingwizard", 920),
        LeaderboardEntry("devgenius", 900),
        LeaderboardEntry("chillmate", 850),
        LeaderboardEntry("gamerx", 800),
        LeaderboardEntry("techgal", 760),
        LeaderboardEntry("booklover", 700)
    )

    val userRank = 4
    val userScore = 850

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBg)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Header
        Text(
            text = "Leaderboard",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
            fontFamily = Poppins,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // User summary card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(24.dp),
            color = WhiteBox,
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Your Rank: #$userRank",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = CardAccent,
                    fontFamily = Poppins
                )
                Text(
                    text = "Your Score: $userScore pts",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Scrollable leaderboard
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(leaderboard) { index, entry ->
                val rank = index + 1
                val cardColor = when (rank) {
                    1 -> Gold.copy(alpha = 0.3f)
                    2 -> Silver.copy(alpha = 0.3f)
                    3 -> Bronze.copy(alpha = 0.3f)
                    else -> Color.White
                }

                LeaderboardCard(entry, rank, cardColor)
            }
        }
    }
}

@Composable
fun LeaderboardCard(entry: LeaderboardEntry, rank: Int, bgColor: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (rank <= 3) DarkGreen else CardAccent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Username and score
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.username,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = Poppins
                )
                Text(
                    text = "${entry.score} pts",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
            }

            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Trophy",
                tint = if (rank <= 3) DarkGreen else Color.Gray,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
