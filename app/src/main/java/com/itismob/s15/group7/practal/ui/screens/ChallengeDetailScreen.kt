package com.itismob.s15.group7.practal.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.itismob.s15.group7.practal.DarkGreen
import com.itismob.s15.group7.practal.WhiteBox
import com.itismob.s15.group7.practal.R
import com.itismob.s15.group7.practal.domain.controller.ChallengeViewModel
import com.itismob.s15.group7.practal.ui.theme.Poppins
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.domain.model.Challenge


@Composable
fun ChallengeDetailScreen(
    navController: NavHostController,
    challengeId: String,
    userViewModel: UserViewModel,
    challengeViewModel: ChallengeViewModel
) {
    val db = FirebaseFirestore.getInstance()
    val loggedInUser by userViewModel.loggedInUser.collectAsState()

    val challenges by challengeViewModel.challengeList.collectAsState()
    val challenge = challenges.find { it.id == challengeId } ?: Challenge(id="", title="Unknown", description="", participants=0, goal=1)


    val userChallenges = loggedInUser?.challenges ?: emptyMap()
    val hasJoined = userChallenges.containsKey(challenge.title)
    val currentProgress = userChallenges[challenge.title] ?: 0

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
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = DarkGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Text(
                text = "Challenge Info",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                fontFamily = Poppins
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
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
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = Poppins
                )
            }
        }

        if (hasJoined) {
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Progress",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
                    fontFamily = Poppins
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = currentProgress.toFloat() / challenge.goal.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = DarkGreen,
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${currentProgress}/${challenge.goal}",
                    fontSize = 18.sp,
                    color = DarkGreen,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Participants: ${challenge.participants}",
            fontSize = 20.sp,
            color = DarkGreen,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = challenge.description,
            fontSize = 16.sp,
            color = Color.Black,
            fontFamily = Poppins
        )

        Spacer(modifier = Modifier.height(32.dp))


        if (!hasJoined) {
            Button(
                onClick = {
                    val userId = loggedInUser!!.id
                    val challengeTitle = challenge.title
                    val challengeIdField = challenge.id

                    val userRef = db.collection("users").document(userId)


                    userRef.update("challenges.$challengeTitle", 0)
                        .addOnSuccessListener {
                            val updatedChallenges = loggedInUser!!.challenges + (challengeTitle to 0)
                            userViewModel._loggedInUser.value = loggedInUser!!.copy(
                                challenges = updatedChallenges
                            )
                        }

                    db.collection("challenges")
                        .whereEqualTo("id", challengeIdField)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val doc = querySnapshot.documents[0]
                                val challengeRef = db.collection("challenges").document(doc.id)
                                challengeRef.update("participants", FieldValue.increment(1))
                            } else {
                                Log.e("ChallengeDetail", "No challenge found with id=$challengeIdField")
                            }
                        }
                },

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
}
