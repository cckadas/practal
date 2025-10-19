package com.itismob.s15.group7.practal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.itismob.s15.group7.practal.ui.theme.Poppins


@Composable
fun WelcomeProfileScreen(navController: NavHostController, username: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
            .padding(horizontal = 48.dp)
    ) {

        Column(
            modifier = Modifier.padding(top = 96.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Welcome, \n@username",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = DarkGreen,
                lineHeight = 36.sp
            )


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Let's create your profile card!",
                fontSize = 18.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("photo_upload") },
                modifier = Modifier
                    .height(48.dp)
                    .width(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Enter", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}



@Composable
fun PhotoUploadScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
            .padding(horizontal = 48.dp)
    ) {

        Column(
            modifier = Modifier.padding(top = 96.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Upload Your\nMusical Profile",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = DarkGreen,
                lineHeight = 36.sp
            )


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Let others see the musician\nbehind the music!",
                fontSize = 18.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )
        }

        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.Center)
                .background(LightGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Photo",
                tint = WhiteBox,
                modifier = Modifier.size(96.dp)
            )

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 6.dp, y = 6.dp)
                    .clip(CircleShape)
                    .background(DarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Photo",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }


        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("intro") },
                modifier = Modifier
                    .height(48.dp)
                    .width(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Next", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(onClick = { navController.navigate("welcome_profile") }) {
                Text("Return", color = LightGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}



@Composable
fun IntroductionScreen(navController: NavHostController) {
    var desc by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
            .padding(horizontal = 48.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 96.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Write A Short Introduction",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = DarkGreen,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Share a few words about yourself and your love for music!",
                fontSize = 18.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(80.dp))

            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Your Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(20.dp),
                textStyle = TextStyle(
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    color = Color.DarkGray
                ),
                maxLines = 5,
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DarkGreen,
                    unfocusedBorderColor = LightGreen,
                    focusedLabelColor = DarkGreen,
                    cursorColor = DarkGreen,
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5)
                )
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("music_info") },
                modifier = Modifier
                    .height(48.dp)
                    .width(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Next", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(onClick = { navController.navigate("photo_upload") }) {
                Text("Return", color = LightGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MusicInfoScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
            .padding(horizontal = 48.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 96.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Tell Us About Your Preferences",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = DarkGreen,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select the instruments and interests of your music journey",
                fontSize = 18.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(36.dp))



            var selectedInstruments by remember { mutableStateOf(setOf<String>()) }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Instruments",
                        tint = DarkGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Instruments",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen,
                        fontFamily = Poppins
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val instruments = listOf("🎹 Piano", "🎸 Guitar", "🎻 Violin", "🥁 Drums", "🎺 Trumpet", "🎼 Flute", "🎸 Bass",)

                    instruments.forEach { instrument ->
                        val isSelected = instrument in selectedInstruments

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isSelected) DarkGreen else Color.LightGray.copy(alpha = 0.4f)
                                )
                                .clickable {
                                    selectedInstruments = if (isSelected) {
                                        selectedInstruments - instrument
                                    } else {
                                        selectedInstruments + instrument
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = instrument,
                                color = if (isSelected) Color.White else Color.Gray,
                                fontFamily = Poppins,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))


            var selectedInterests by remember { mutableStateOf(setOf<String>()) }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Interests",
                        tint = DarkGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Interests",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen,
                        fontFamily = Poppins
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val interests = listOf("🎵 Classical", "🎶 Jazz", "🎸 Rock", "🎼 Blues", "🎹 Contemporary")

                    interests.forEach { interest ->
                        val isSelected = interest in selectedInterests

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isSelected) DarkGreen else Color.LightGray.copy(alpha = 0.4f)
                                )
                                .clickable {
                                    selectedInterests = if (isSelected) {
                                        selectedInterests - interest
                                    } else {
                                        selectedInterests + interest
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = interest,
                                color = if (isSelected) Color.White else Color.Gray,
                                fontFamily = Poppins,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }


        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("goal_skill_info") },
                modifier = Modifier
                    .height(48.dp)
                    .width(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Next", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(onClick = { navController.navigate("intro") }) {
                Text("Return", color = LightGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoalSkillInfoScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
            .padding(horizontal = 48.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 96.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Set Your\nMusic Goals",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = DarkGreen,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Choose your practice goals and current skill level",
                fontSize = 18.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(36.dp))

            var selectedGoals by remember { mutableStateOf(setOf<String>()) }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Practice Goals",
                        tint = DarkGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Practice Goals",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen,
                        fontFamily = Poppins
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val goals = listOf(
                        "🎵 Improvement",
                        "🧠 Learn theory",
                        "🎶 Write songs",
                        "📈 Build consistency",
                        "🎧 Explore genres",
                        "🎼 Compose"
                    )

                    goals.forEach { goal ->
                        val isSelected = goal in selectedGoals

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isSelected) DarkGreen else Color.LightGray.copy(alpha = 0.4f)
                                )
                                .clickable {
                                    selectedGoals = if (isSelected) {
                                        selectedGoals - goal
                                    } else {
                                        selectedGoals + goal
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = goal,
                                color = if (isSelected) Color.White else Color.Gray,
                                fontFamily = Poppins,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            var selectedSkillLevel by remember { mutableStateOf<String?>(null) }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Skill Level",
                        tint = DarkGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Skill Level",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen,
                        fontFamily = Poppins
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val skillLevels = listOf(
                        "🎧 Beginner",
                        "🎵 Intermediate",
                        "🎶 Advanced",
                        "🎤 Professional"
                    )

                    skillLevels.forEach { level ->
                        val isSelected = selectedSkillLevel == level

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isSelected) DarkGreen else Color.LightGray.copy(alpha = 0.4f)
                                )
                                .clickable { selectedSkillLevel = level }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = level,
                                color = if (isSelected) Color.White else Color.Gray,
                                fontFamily = Poppins,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("other_details") },
                modifier = Modifier
                    .height(48.dp)
                    .width(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Next", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(onClick = { navController.navigate("music_info") }) {
                Text("Return", color = LightGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}




@Composable
fun OtherDetailsScreen(navController: NavHostController) {
    var project by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
            .padding(horizontal = 48.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 96.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Add Additional Optional Details",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = DarkGreen,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Add extra information that helps others get to know you better",
                fontSize = 18.sp,
                fontFamily = Poppins,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(96.dp))

            Row(
                modifier = Modifier.width(300.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = project,
                    onValueChange = { project = it },
                    label = { Text("Other Project") },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    textStyle = TextStyle(
                        fontFamily = Poppins,
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = LightGreen,
                        focusedLabelColor = DarkGreen,
                        cursorColor = DarkGreen,
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {  },
                    modifier = Modifier
                        .size(48.dp).padding(top = 4.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("+", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {  },
                modifier = Modifier
                    .height(48.dp)
                    .width(300.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
            ) {
                Text("Add Demo Recordings", color = Color.White, fontSize = 18.sp)
            }
        }


        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("done") },
                modifier = Modifier
                    .height(48.dp)
                    .width(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Next", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(onClick = { navController.navigate("goal_skill_info") }) {
                Text("Return", color = LightGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}


@Composable
fun CompletedProfileScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
            .padding(horizontal = 48.dp)
    ) {

        Column(
            modifier = Modifier.padding(top = 96.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Your Profile is Ready to Go!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = DarkGreen,
                lineHeight = 36.sp
            )
        }

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .heightIn(min = 220.dp)
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(LightGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Picture",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))


                Text(
                    text = "@username",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins,
                    color = DarkGreen
                )

                Spacer(modifier = Modifier.height(8.dp))


                Text(
                    text = "Passionate musician with 8 years of experience. Love exploring different genres and techniques.",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Instruments",
                            tint = DarkGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Instruments",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen,
                            fontFamily = Poppins
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listOf("🎹 Piano", "🎸 Guitar", "🎻 Violin", "🥁 Drums")) { instrument ->
                            ProfileCardInfoChip(text = instrument)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Interests",
                            tint = DarkGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Musical Interests",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen,
                            fontFamily = Poppins
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listOf("🎵 Classical", "🎶 Jazz", "🎸 Rock", "🎼 Blues", "🎹 Contemporary")) { interest ->
                            ProfileCardInfoChip(text = interest)
                        }
                    }
                }
            }
        }


        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("dashboard") },
                modifier = Modifier
                    .height(48.dp)
                    .width(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Done", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(onClick = { navController.navigate("other_details") }) {
                Text("Return", color = LightGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun ProfileCardInfoChip(text: String) {
    Surface(
        color = LightGreen.copy(alpha = 0.2f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LightGreen.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = DarkGreen,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}