// MainActivity.kt
package com.example.practal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.practal.ui.theme.Bagel
import com.example.practal.ui.theme.Cherry
import com.example.practal.ui.theme.Montserrat
import com.example.practal.ui.theme.Poppins

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PractalApp()
        }
    }
}


@Composable
fun PractalApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "landing") {
        composable("landing") { LandingScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("welcome_profile") { WelcomeProfileScreen(navController, "username") }
        composable("photo_upload") { PhotoUploadScreen(navController) }
        composable("intro") { IntroductionScreen(navController) }
        composable("details") { MusicBackgroundRegistrationScreen(navController) }
        composable("done") { CompletedProfileScreen(navController, "username") }
        composable("dashboard") { DashboardScreen(navController, "username") }


        composable("challenges") { ChallengeScreen(navController) }
        composable("challengeDetail/{challengeId}") { backStackEntry ->
            val challengeId = backStackEntry.arguments?.getString("challengeId")?.toIntOrNull() ?: 0
            ChallengeDetailScreen(navController, challengeId)
        }
    }
}



val LightGreen = Color(0xFFB9D8A7)
val DarkGreen = Color(0xFF264E36)
val CardGreen = Color(0xFFA7C49C)
val WhiteBox = Color(0xFFF6F8F5)

@Composable
fun LandingScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(32.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy((-12).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "WELCOME TO",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = Cherry,
                    color = LightGreen
                )
                Text(
                    text = "Practal",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    letterSpacing = (-2).sp,
                    color = DarkGreen
                )
            }


            Spacer(modifier = Modifier.height(128.dp))
            Button(
                onClick = { navController.navigate("login") },
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Log In", fontSize = 18.sp, color = Color.White, fontFamily = Poppins, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { navController.navigate("signup") },
                border = ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Sign Up", fontSize = 18.sp, color = DarkGreen, fontFamily = Poppins, fontWeight = FontWeight.Bold)
            }
        }
    }
}


