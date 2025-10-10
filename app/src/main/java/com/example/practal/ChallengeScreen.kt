package com.example.practal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.practal.ui.theme.Poppins

data class Challenge(
    val id: Int,
    val title: String,
    val participants: Int,
    val color: Color,
    val icon: ImageVector
)

@Composable
fun ChallengeScreen(navController: NavHostController) {
    val challenges = listOf(
        Challenge(1, "Baroque Week", 65, Color(0xFFB3E5FC), Icons.Default.LibraryMusic),
        Challenge(2, "Jazz Improv Month", 112, Color(0xFFFFCC80), Icons.Default.Mic),
        Challenge(3, "30-Day Scale Challenge", 89, Color(0xFFC5E1A5), Icons.Default.Piano),
        Challenge(4, "Bi-Weekly Pitch Training", 54, Color(0xFFD1C4E9), Icons.Default.MusicNote)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier.size(48.dp)
            ) {
                IconButton(onClick = { navController.navigate("dashboard") }) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = DarkGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Text(
                text = "Challenges",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                fontFamily = Poppins
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(challenges) { challenge ->
                ChallengeCard(challenge = challenge, onClick = {
                    navController.navigate("challengeDetail/${challenge.id}")
                })
            }
        }
    }
}

@Composable
fun ChallengeCard(challenge: Challenge, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = challenge.color,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = challenge.icon,
                contentDescription = null,
                tint = DarkGreen,
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 16.dp)
            )


            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = challenge.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DarkGreen,
                    fontFamily = Poppins
                )
                Text(
                    text = "${challenge.participants} active participants",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    fontFamily = Poppins
                )
            }
        }
    }
}
