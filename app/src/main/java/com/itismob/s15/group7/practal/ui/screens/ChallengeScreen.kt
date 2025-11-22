package com.itismob.s15.group7.practal.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.itismob.s15.group7.practal.DarkGreen
import com.itismob.s15.group7.practal.WhiteBox
import com.itismob.s15.group7.practal.ui.theme.Poppins
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.domain.controller.ChallengeViewModel
import com.itismob.s15.group7.practal.domain.model.Challenge
import com.itismob.s15.group7.practal.R

@Composable
fun ChallengeScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    challengeViewModel: ChallengeViewModel
) {
    val challenges by challengeViewModel.challengeList.collectAsState()

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
                ChallengeCard(challenge = challenge) {
                    Log.i("TAG", "Info: ${challenge.id}")
                    navController.navigate("challenge_detail/${challenge.id}")
                }
            }
        }
    }
}

@Composable
fun ChallengeCard(challenge: Challenge, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {

        fun getChallengeImage(title: String): Int {
            return when (title) {
                "Baroque Week" -> R.drawable.baroque
                "Jazz Improv Month" -> R.drawable.jazz
                "30-Day Piano Challenge" -> R.drawable.piano
                "Bi-Weekly Pitch Training" -> R.drawable.mic
                else -> R.drawable.baroque
            }
        }

        val imageRes = getChallengeImage(challenge.title)

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(
                Color.Black.copy(alpha = 0.4f),
                blendMode = BlendMode.Darken
            )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = challenge.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = Poppins
            )
            Text(
                text = "${challenge.participants} active participants",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
                fontFamily = Poppins
            )
        }
    }
}
