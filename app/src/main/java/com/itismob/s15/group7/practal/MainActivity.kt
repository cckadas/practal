package com.itismob.s15.group7.practal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.itismob.s15.group7.practal.domain.controller.ChallengeViewModel
import com.itismob.s15.group7.practal.domain.controller.PostViewModel
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.domain.controller.PracticeSessionViewModel
import com.itismob.s15.group7.practal.domain.controller.AchievementViewModel
import com.itismob.s15.group7.practal.domain.controller.ClubViewModel
import com.itismob.s15.group7.practal.ui.screens.AchievementsScreen
import com.itismob.s15.group7.practal.ui.screens.ChallengeDetailScreen
import com.itismob.s15.group7.practal.ui.screens.ChallengeScreen
import com.itismob.s15.group7.practal.ui.screens.ClubsScreen
import com.itismob.s15.group7.practal.ui.screens.CompletedProfileScreen
import com.itismob.s15.group7.practal.ui.screens.CreatePostScreen
import com.itismob.s15.group7.practal.ui.screens.CreatePracticePostScreen
import com.itismob.s15.group7.practal.ui.screens.DashboardScreen
import com.itismob.s15.group7.practal.ui.screens.GoalSkillInfoScreen
import com.itismob.s15.group7.practal.ui.screens.IntroductionScreen
import com.itismob.s15.group7.practal.ui.screens.LeaderboardScreen
import com.itismob.s15.group7.practal.ui.screens.LogPracticeSessionScreen
import com.itismob.s15.group7.practal.ui.screens.LoginScreen
import com.itismob.s15.group7.practal.ui.screens.MusicInfoScreen
import com.itismob.s15.group7.practal.ui.screens.OtherDetailsScreen
import com.itismob.s15.group7.practal.ui.screens.OtherProfileScreen
import com.itismob.s15.group7.practal.ui.screens.PhotoUploadScreen
import com.itismob.s15.group7.practal.ui.screens.ProgressAnalyticsScreen
import com.itismob.s15.group7.practal.ui.screens.SignUpScreen
import com.itismob.s15.group7.practal.ui.screens.UserProfileScreen
import com.itismob.s15.group7.practal.ui.screens.WelcomeProfileScreen
import com.itismob.s15.group7.practal.ui.theme.Montserrat
import com.itismob.s15.group7.practal.ui.theme.Poppins


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PractalApp()
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PractalApp() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    val challengeViewModel: ChallengeViewModel = viewModel()
    val postViewModel: PostViewModel = viewModel()
    val practiceSessionViewModel: PracticeSessionViewModel = viewModel { PracticeSessionViewModel(userViewModel) }
    val achievementViewModel: AchievementViewModel = viewModel()
    val clubViewModel: ClubViewModel = viewModel()
    val email: String = ""

    NavHost(navController, startDestination = "landing") {
        composable("landing") { LandingScreen(navController) }
        composable("login") { LoginScreen(navController, userViewModel) }
        composable("signup") { SignUpScreen(navController, userViewModel) }

        composable("welcome_profile") { WelcomeProfileScreen(navController, userViewModel) }
        composable("photo_upload") { PhotoUploadScreen(navController, userViewModel) }
        composable("intro") { IntroductionScreen(navController, userViewModel) }
        composable("music_info") { MusicInfoScreen(navController, userViewModel) }
        composable("goal_skill_info") { GoalSkillInfoScreen(navController, userViewModel) }
        composable("other_details") { OtherDetailsScreen(navController, userViewModel) }
        composable("done") { CompletedProfileScreen(navController, userViewModel) }

        composable("dashboard") { DashboardScreen(navController, userViewModel, postViewModel, practiceSessionViewModel, clubViewModel) }

        composable("analytics") { ProgressAnalyticsScreen(navController, userViewModel, practiceSessionViewModel) }
        composable("leaderboard") { LeaderboardScreen(
            navController,
            viewModel = userViewModel
        ) }
        composable("clubs") { ClubsScreen(navController, clubViewModel, userViewModel) }

        composable("challenges") { ChallengeScreen(navController, userViewModel, challengeViewModel) }
        composable("challenge_detail/{challenge_id}") { backStackEntry ->
            val challengeId = backStackEntry.arguments?.getString("challenge_id") ?: ""
            ChallengeDetailScreen(navController, challengeId, userViewModel, challengeViewModel)
        }

        composable("log_practice") { LogPracticeSessionScreen(navController, practiceSessionViewModel, userViewModel, achievementViewModel) }
        composable("all_achievements") { AchievementsScreen(navController, userViewModel, achievementViewModel) }

        composable("profile") { UserProfileScreen(navController, userViewModel, achievementViewModel) }
        composable("other_profile/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OtherProfileScreen(navController, userViewModel, email, achievementViewModel)
        }

        // post creation routes
        composable("create_post") { 
            CreatePostScreen(navController, userViewModel, postViewModel) 
        }
        composable(
            route = "create_practice_post/{instrument}/{practiceType}/{pieceOrFocus}/{difficulty}/{durationMinutes}/{earnedXP}/{sessionId}",
            arguments = listOf(
                navArgument("instrument") { type = NavType.StringType },
                navArgument("practiceType") { type = NavType.StringType },
                navArgument("pieceOrFocus") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("durationMinutes") { type = NavType.IntType },
                navArgument("earnedXP") { type = NavType.IntType },
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val instrument = backStackEntry.arguments?.getString("instrument") ?: ""
            val practiceType = backStackEntry.arguments?.getString("practiceType") ?: ""
            val pieceOrFocus = backStackEntry.arguments?.getString("pieceOrFocus") ?: ""
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: ""
            val durationMinutes = backStackEntry.arguments?.getInt("durationMinutes") ?: 0
            val earnedXP = backStackEntry.arguments?.getInt("earnedXP") ?: 0
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            
            CreatePracticePostScreen(
                navController, userViewModel, postViewModel,
                instrument, practiceType, pieceOrFocus, difficulty, durationMinutes, earnedXP, sessionId
            )
        }

    }
}



val LightGreen = Color(0xFF9AB088)
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
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    color = LightGreen
                )
                Text(
                    text = "Practal",
                    fontSize = 68.sp,
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


