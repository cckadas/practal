package com.itismob.s15.group7.practal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itismob.s15.group7.practal.CardAccent
import com.itismob.s15.group7.practal.DarkGreen
import com.itismob.s15.group7.practal.LeaderboardEntry
import com.itismob.s15.group7.practal.WhiteBox
import com.itismob.s15.group7.practal.ui.theme.Poppins

val Gold = Color(0xFFFFD700)
val Silver = Color(0xFFC0C0C0)
val Bronze = Color(0xFFCD7F32)


@Composable
fun LeaderboardScreen(padding: PaddingValues) {
    val leaderboard = listOf(
        LeaderboardEntry("beathoven", 980),
        LeaderboardEntry("mozart123", 920),
        LeaderboardEntry("iamsteph", 900),
        LeaderboardEntry("useruser", 850),
        LeaderboardEntry("ellie", 800),
        LeaderboardEntry("real_sophia", 760),
        LeaderboardEntry("musiclover", 700)
    )

    val userRank = 24
    val userScore = 150

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(WhiteBox)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Leaderboard",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen,
            fontFamily = Poppins,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))


        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(24.dp),
            color = WhiteBox,
            shadowElevation = 8.dp
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
                    color = DarkGreen,
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(leaderboard) { index, entry ->
                val rank = index + 1
                val cardColor = when (rank) {
                    1 -> Gold.copy(alpha = 0.5f)
                    2 -> Silver.copy(alpha = 0.5f)
                    3 -> Bronze.copy(alpha = 0.5f)
                    else -> WhiteBox
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