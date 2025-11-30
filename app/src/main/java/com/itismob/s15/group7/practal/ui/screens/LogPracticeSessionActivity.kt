package com.itismob.s15.group7.practal.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.itismob.s15.group7.practal.*
import com.itismob.s15.group7.practal.domain.controller.*
import com.itismob.s15.group7.practal.domain.controller.AchievementViewModel
import com.itismob.s15.group7.practal.ui.theme.Poppins
import kotlinx.coroutines.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size

// constants
private val INSTRUMENTS = listOf(
    "🎹 Piano", "🎸 Guitar", "🎻 Violin", "🎻 Cello", "🪈 Flute",
    "🎷 Clarinet", "🎷 Saxophone", "🎺 Trumpet", "🥁 Drums", "🎸 Bass",
    "🎤 Voice", "🎹 Keyboard", "🪕 Banjo", "🎺 Trombone", "Other"
)

private val PRACTICE_TYPES = listOf(
    "Scales & Technique", "Repertoire", "Sight Reading", "Improvisation",
    "Warm-up", "Etudes", "Theory", "Ear Training", "Performance Prep"
)

private val DIFFICULTIES = listOf("Beginner", "Intermediate", "Advanced", "Expert")

@Composable
fun LogPracticeSessionScreen(
    navController: NavHostController,
    practiceSessionViewModel: PracticeSessionViewModel,
    userViewModel: UserViewModel,
    achievementViewModel: AchievementViewModel = viewModel()
) {
    var currentStep by remember { mutableIntStateOf(1) }
    var instrument by remember { mutableStateOf("") }
    var practiceType by remember { mutableStateOf("") }
    var practiceFocus by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var earnedXP by remember { mutableIntStateOf(0) }
    var sessionDurationSeconds by remember { mutableIntStateOf(0) }
    
    val currentSession by practiceSessionViewModel.currentSession.collectAsState()
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    
    when (currentStep) {
        1 -> Step1_SessionDetails(
            instrument, practiceType, practiceFocus,
            { instrument = it }, { practiceType = it }, { practiceFocus = it },
            onNext = {
                practiceSessionViewModel.startPracticeSession(
                    instrument.replace(Regex("[🎹🎸🎻🪈🎷🎺🥁🎤🪕]"), "").trim(),
                    practiceType, practiceFocus
                )
                currentStep = 2
            },
            onBack = { navController.popBackStack() }
        )
        2 -> Step2_LiveTimer(
            practiceSessionViewModel,
            onEnd = { currentStep = 3 },
            onBack = { currentStep = 1 }
        )
        3 -> Step3_CompleteSession(
            difficulty, notes,
            { difficulty = it }, { notes = it },
            practiceSessionViewModel,
            onComplete = {
                currentSession?.let { session ->
                    val endTime = com.google.firebase.Timestamp.now()
                    sessionDurationSeconds = (endTime.seconds - session.startTime.seconds).toInt()
                    val durationMinutes = sessionDurationSeconds / 60
                    val multiplier = when (difficulty.lowercase()) {
                        "beginner" -> 1.0
                        "intermediate" -> 1.5
                        "advanced" -> 2.0
                        "expert" -> 2.5
                        else -> 1.0
                    }
                    earnedXP = (durationMinutes * multiplier).toInt()
                }
                
                practiceSessionViewModel.endPracticeSession(notes, difficulty)
                GlobalScope.launch {
                    delay(1500)
                    userViewModel.getUserList()
                    
                    // check and unlock achievements after session
                    loggedInUser?.let { user ->
                        achievementViewModel.checkAndUnlockAchievements(user)
                    }
                    
                    showSuccessDialog = true
                }
            },
            onBack = { currentStep = 2 }
        )
    }
    
    if (showSuccessDialog) {
        SessionCompleteDialog(
            earnedXP = earnedXP,
            durationSeconds = sessionDurationSeconds,
            currentLevel = loggedInUser?.level ?: 1,
            totalXP = loggedInUser?.xp ?: 0,
            onDismiss = {
                showSuccessDialog = false
                navController.popBackStack()
            }
        )
    }
}

@Composable
private fun SessionCompleteDialog(
    earnedXP: Int,
    durationSeconds: Int,
    currentLevel: Int,
    totalXP: Int,
    onDismiss: () -> Unit
) {
    val xpForNextLevel = 100 + (currentLevel - 1) * 50
    val xpForCurrentLevel = (1 until currentLevel).sumOf { 100 + (it - 1) * 50 }
    val xpInCurrentLevel = totalXP - xpForCurrentLevel
    val progress = (xpInCurrentLevel.toFloat() / xpForNextLevel).coerceIn(0f, 1f)
    
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(progress, tween(1500, easing = FastOutSlowInEasing))
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎉", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("Session Complete!", fontFamily = Poppins, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Duration: ${durationSeconds / 60}:${String.format("%02d", durationSeconds % 60)}",
                    fontFamily = Poppins, fontSize = 16.sp, fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("+$earnedXP XP", fontFamily = Poppins, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                }
                Spacer(Modifier.height(20.dp))
                LevelProgressBar(currentLevel, xpInCurrentLevel, xpForNextLevel, animatedProgress.value)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Keep practicing to reach Level ${currentLevel + 1}!",
                    fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Awesome!", fontFamily = Poppins, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = WhiteBox,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun LevelProgressBar(level: Int, currentXP: Int, targetXP: Int, progress: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Level $level", fontFamily = Poppins, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
            Text("$currentXP / $targetXP XP", fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(Modifier.height(8.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(12.dp)) {
            drawRoundRect(Color.LightGray.copy(alpha = 0.3f), cornerRadius = CornerRadius(24f, 24f))
            if (progress > 0f) {
                drawRoundRect(
                    Color(0xFF2E5D45),
                    size = Size(size.width * progress, size.height),
                    cornerRadius = CornerRadius(24f, 24f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1_SessionDetails(
    instrument: String, practiceType: String, practiceFocus: String,
    onInstrumentChange: (String) -> Unit, onPracticeTypeChange: (String) -> Unit,
    onPracticeFocusChange: (String) -> Unit, onNext: () -> Unit, onBack: () -> Unit
) {
    val context = LocalContext.current
    var instrumentExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    val isValid = instrument.isNotEmpty() && practiceType.isNotEmpty() && practiceFocus.isNotEmpty()

    Column(Modifier.fillMaxSize().background(WhiteBox)) {
        StepHeader("Step 1 of 3: Session Details", onBack)

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Session Details", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkGreen)
                    StyledDropdown(instrument, "Instrument", "Select instrument", INSTRUMENTS, instrumentExpanded, { instrumentExpanded = it }, onInstrumentChange)
                    StyledDropdown(practiceType, "Practice Type", "Select type", PRACTICE_TYPES, typeExpanded, { typeExpanded = it }, onPracticeTypeChange)
                    OutlinedTextField(
                        value = practiceFocus, onValueChange = onPracticeFocusChange,
                        label = { Text("Focus / Piece", fontFamily = Poppins) },
                        placeholder = { Text("e.g. Bach Prelude, Scales", fontFamily = Poppins) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, unfocusedBorderColor = Color.LightGray)
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    if (isValid) onNext()
                    else Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                },
                enabled = isValid, modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text("Start Session", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun Step2_LiveTimer(
    practiceSessionViewModel: PracticeSessionViewModel,
    onEnd: () -> Unit,
    onBack: () -> Unit
) {
    val currentSession by practiceSessionViewModel.currentSession.collectAsState()
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(currentSession) {
        if (currentSession != null) {
            while (currentSession != null) {
                val startTime = currentSession?.startTime?.toDate()?.time ?: 0L
                elapsedSeconds = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                delay(1000)
            }
        } else {
            elapsedSeconds = 0
        }
    }
    
    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val isActive = currentSession != null
    
    Column(Modifier.fillMaxSize().background(WhiteBox)) {
        StepHeader("Step 2 of 3: Practice Session", onBack)
        
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⏱️", fontSize = 64.sp)
                    Spacer(Modifier.height(24.dp))
                    Text(
                        if (isActive) "Session in Progress" else "Ready to Start",
                        fontSize = 18.sp, fontWeight = FontWeight.Medium, color = DarkGreen, fontFamily = Poppins
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        String.format("%02d:%02d", minutes, seconds),
                        fontSize = 72.sp, fontWeight = FontWeight.Bold, color = DarkGreen, fontFamily = Poppins
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        when (minutes) {
                            0 -> "seconds"
                            1 -> "1 minute"
                            else -> "$minutes minutes"
                        },
                        fontSize = 14.sp, color = Color.Gray, fontFamily = Poppins
                    )
                    Spacer(Modifier.height(32.dp))
                    if (isActive) {
                        Button(
                            onClick = onEnd,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                        ) {
                            Text("End Session", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
            if (isActive) {
                Spacer(Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightGreen.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MusicNote, null, tint = DarkGreen)
                        Spacer(Modifier.width(12.dp))
                        Text("Keep practicing! You're building your streak.", fontFamily = Poppins, color = DarkGreen, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step3_CompleteSession(
    difficulty: String, notes: String,
    onDifficultyChange: (String) -> Unit, onNotesChange: (String) -> Unit,
    practiceSessionViewModel: PracticeSessionViewModel,
    onComplete: () -> Unit, onBack: () -> Unit
) {
    val isLoading by practiceSessionViewModel.isLoading.collectAsState()
    var difficultyExpanded by remember { mutableStateOf(false) }
    
    Column(Modifier.fillMaxSize().background(WhiteBox)) {
        StepHeader("Step 3 of 3: Session Details", onBack)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Difficulty", fontFamily = Poppins, fontWeight = FontWeight.SemiBold, color = DarkGreen)
                    StyledDropdown(difficulty, "Level", "Select level", DIFFICULTIES, difficultyExpanded, { difficultyExpanded = it }, onDifficultyChange)
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Notes (Optional)", fontFamily = Poppins, fontWeight = FontWeight.SemiBold, color = DarkGreen)
                    OutlinedTextField(
                        value = notes, onValueChange = onNotesChange,
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Reflections, obstacles, improvements", fontFamily = Poppins) },
                        shape = RoundedCornerShape(12.dp), maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, unfocusedBorderColor = Color.LightGray)
                    )
                }
            }
            Button(
                onClick = onComplete,
                enabled = difficulty.isNotEmpty() && !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Log Session", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun StepHeader(stepText: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(DarkGreen, CardAccent)))) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Log Practice", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = Poppins)
                Text(stepText, fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f), fontFamily = Poppins)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StyledDropdown(
    value: String, label: String, placeholder: String, items: List<String>,
    expanded: Boolean, onExpandedChange: (Boolean) -> Unit, onValueChange: (String) -> Unit
) {
    key(value) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { onExpandedChange(!expanded) }) {
            OutlinedTextField(
                value = value, onValueChange = {}, readOnly = true,
                label = { Text(label, fontFamily = Poppins) },
                placeholder = { Text(placeholder, fontFamily = Poppins) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkGreen, unfocusedBorderColor = Color.LightGray)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
                items.forEach { item ->
                    key(item) {
                        DropdownMenuItem(
                            text = { Text(item, fontFamily = Poppins) },
                            onClick = {
                                onValueChange(item)
                                onExpandedChange(false)
                            }
                        )
                    }
                }
            }
        }
    }
}