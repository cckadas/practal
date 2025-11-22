package com.itismob.s15.group7.practal.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.itismob.s15.group7.practal.WhiteBox
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.ui.theme.Poppins

val Gold = Color(0xFFFFD700)
val Silver = Color(0xFFC0C0C0)
val Bronze = Color(0xFFCD7F32)

data class LeaderboardEntry(val username: String, val score: Int, val image: String)

@Composable
fun LeaderboardScreen(navController: NavHostController, viewModel: UserViewModel) {

    val userList by viewModel.userList.collectAsState()
    val loggedInUser by viewModel.loggedInUser.collectAsState()

    val (rankedUsers, userRank) = if (loggedInUser != null) {
        viewModel.getRankedUsers(loggedInUser!!.email)
    } else {
        Pair(emptyList(), null)
    }

    val userScore = loggedInUser?.points ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .height(64.dp)
        ) {
            Text(
                text = "Leaderboard",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = Poppins
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "See how you rank among other musicians",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                fontFamily = Poppins
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
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
                    text = "Your Rank: #${userRank ?: "-"}",
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 92.dp
            )
        ) {
            itemsIndexed(rankedUsers) { index, entry ->
                val rank = index + 1
                val cardColor = when (rank) {
                    1 -> Gold.copy(alpha = 0.5f)
                    2 -> Silver.copy(alpha = 0.5f)
                    3 -> Bronze.copy(alpha = 0.5f)
                    else -> WhiteBox
                }

                LeaderboardCard(
                    entry = LeaderboardEntry("${entry.firstname} ${entry.lastname}", entry.points, entry.image),
                    rank = rank,
                    bgColor = cardColor
                )
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (rank <= 3) DarkGreen else CardAccent),
                contentAlignment = Alignment.Center
            ) {
                if (entry.image.isNotEmpty()) {
                    AsyncImage(
                        model = entry.image,
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
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    fontWeight = FontWeight.ExtraBold,
                    color = CardAccent,
                    fontSize = 20.sp
                )
            }
        }
    }
}
