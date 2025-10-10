package com.example.practal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.practal.ui.theme.Poppins


@Composable
fun WelcomeProfileScreen(navController: NavHostController, username: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(LightGreen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Welcome,\n@$username!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = DarkGreen,
                fontFamily = Poppins
            )
            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = "Let's create your \nprofile card.",
                fontSize = 24.sp,
                color = WhiteBox,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,

            )
            Spacer(modifier = Modifier.height(96.dp))
            Button(
                onClick = { navController.navigate("photo_upload") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Next", color = Color.White, fontSize = 22.sp)
            }
        }
    }
}


@Composable
fun PhotoUploadScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGreen)
            .padding(horizontal = 32.dp)
    ) {

        Text(
            text = "Upload a photo",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = DarkGreen,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 128.dp)
        )

        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.Center)
                .background(WhiteBox, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Profile Photo",
                tint = Color(0xFF8FA98F),
                modifier = Modifier.size(72.dp)
            )
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
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Enter", color = Color.White, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("intro") }) {
                Text("Skip", color = WhiteBox, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
        }
    }
}



@Composable
fun IntroductionScreen(navController: NavHostController) {
    var intro by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGreen)
            .padding(horizontal = 32.dp)
    ) {

        Text(
            text = "Write a short introduction",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = DarkGreen,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 128.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(WhiteBox, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Write a short introduction", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = intro,
                onValueChange = { intro = it },
                placeholder = { Text("Keep it short and sweet...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(WhiteBox, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
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
                onClick = { navController.navigate("details") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Enter", color = Color.White, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("details") }) {
                Text("Skip", color = WhiteBox, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicBackgroundRegistrationScreen(navController: NavHostController) {
    var instruments by remember { mutableStateOf("") }
    var skillLevel by remember { mutableStateOf("") }
    var goals by remember { mutableStateOf("") }
    var genres by remember { mutableStateOf("") }
    var projects by remember { mutableStateOf("") }


    Scaffold(
        containerColor = LightGreen,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        ""

                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkGreen)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .fillMaxSize()
                .background(WhiteBox, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Tell us about your musical background",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = CardAccent
            )

            Spacer(modifier = Modifier.height(24.dp))

            InputField("Instruments Played", instruments) { instruments = it }
            Spacer(modifier = Modifier.height(16.dp))

            InputField("Skill Level", skillLevel) { skillLevel = it }
            Spacer(modifier = Modifier.height(16.dp))

            InputField("Practice Goals", goals) { goals = it }
            Spacer(modifier = Modifier.height(24.dp))

            Divider(thickness = 1.dp, color = LightGreen)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Optional Information",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = DarkGreen
            )

            Spacer(modifier = Modifier.height(16.dp))
            InputField("Favorite Genres", genres) { genres = it }
            Spacer(modifier = Modifier.height(16.dp))
            InputField("Current Projects", projects) { projects = it }

            Spacer(modifier = Modifier.height(24.dp))


            UploadButton(
                icon = Icons.Default.MusicNote,
                label = "Add Demo Recordings"
            ) { /* TODO: Implement audio picker */ }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("done") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Submit", color = Color.White, fontSize = 18.sp, fontFamily = Poppins)
            }
        }
    }
}


@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    )
}

@Composable
fun UploadButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 16.sp, fontFamily = Poppins)
    }
}


@Composable
fun CompletedProfileScreen(navController: NavHostController, username: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGreen)
            .padding(horizontal = 32.dp)
    ) {

        Text(
            text = "Yay! You’ve completed your profile card.",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = DarkGreen,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 128.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(WhiteBox, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = WhiteBox,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(72.dp), tint = Color(0xFF8FA98F))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("@$username", fontWeight = FontWeight.Bold, color = DarkGreen)
                    Text(
                        "“Lorem ipsum dolor sit amet, consectetur adipiscing elit.”",
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
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
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Enter", color = Color.White, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("dashboard") }) {
                Text("Skip", color = WhiteBox, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
        }
    }
}
