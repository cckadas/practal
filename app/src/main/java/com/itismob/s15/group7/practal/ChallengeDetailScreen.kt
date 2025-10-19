package com.itismob.s15.group7.practal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.itismob.s15.group7.practal.ui.theme.Poppins
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Piano

@Composable
fun ChallengeDetailScreen(
    navController: NavHostController,
    challengeId: Int
) {

    val challenge = when (challengeId) {
        1 -> Challenge(
            id = 1,
            title = "Baroque Week",
            participants = 65,
            color = Color(0xFFB3E5FC),
            icon = Icons.Default.LibraryMusic
        )
        2 -> Challenge(
            id = 2,
            title = "Jazz Improv Month",
            participants = 112,
            color = Color(0xFFFFCC80),
            icon = Icons.Default.Mic
        )
        3 -> Challenge(
            id = 3,
            title = "30-Day Scale Challenge",
            participants = 89,
            color = Color(0xFFC5E1A5),
            icon = Icons.Default.Piano
        )
        4 -> Challenge(
            id = 4,
            title = "Bi-Weekly Pitch Training",
            participants = 54,
            color = Color(0xFFD1C4E9),
            icon = Icons.Default.MusicNote
        )
        else -> Challenge(0, "Unknown Challenge", 0, Color.LightGray, Icons.Default.ArrowBack)
    }

    val description = when (challengeId) {
        1 -> "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
        2 -> "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
        3 -> "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
        4 -> "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
        else -> "No details available for this challenge."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier.size(48.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = DarkGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = challenge.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                fontFamily = Poppins
            )
        }


        Surface(
            shape = RoundedCornerShape(24.dp),
            color = challenge.color.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Participants: ${challenge.participants}",
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    fontFamily = Poppins
                )
                Text(
                    text = description,
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontFamily = Poppins
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))


        Button(
            onClick = { /* TODO: Implement join logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
        ) {
            Text(
                "Join Challenge",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = Poppins
            )
        }
    }
}
