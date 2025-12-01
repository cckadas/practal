package com.itismob.s15.group7.practal.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.ui.geometry.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itismob.s15.group7.practal.*
import com.itismob.s15.group7.practal.domain.controller.*
import com.itismob.s15.group7.practal.domain.model.computeLevel
import com.itismob.s15.group7.practal.ui.theme.Poppins
import kotlinx.coroutines.*

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
    // step management
    var currentStep by remember { mutableIntStateOf(1) }
    
    // session details from step 1
    var instrument by remember { mutableStateOf("") }
    var practiceType by remember { mutableStateOf("") }
    var practiceFocus by remember { mutableStateOf("") }
    
    // session details from step 3
    var difficulty by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    // duration tracking
    var frozenDurationSeconds by remember { mutableIntStateOf(0) }
    
    // xp and rewards
    var earnedXP by remember { mutableIntStateOf(0) }
    var sessionId by remember { mutableStateOf("") }
    
    // dialog states
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }
    
    val loggedInUser by userViewModel.loggedInUser.collectAsState()

    when (currentStep) {
        1 -> Step1_SessionDetails(
            instrument = instrument,
            practiceType = practiceType,
            practiceFocus = practiceFocus,
            onInstrumentChange = { instrument = it },
            onPracticeTypeChange = { practiceType = it },
            onPracticeFocusChange = { practiceFocus = it },
            onNext = {
                Log.d("LogPracticeSession", "Step 1 → 2: $instrument, $practiceType, $practiceFocus")
                val cleanInstrument = instrument.replace(Regex("[🎹🎸🎻🪈🎷🎺🥁🎤🪕]"), "").trim()
                practiceSessionViewModel.startPracticeSession(cleanInstrument, practiceType, practiceFocus)
                currentStep = 2
            },
            onBack = { navController.popBackStack() }
        )
        
        2 -> Step2_LiveTimer(
            practiceSessionViewModel = practiceSessionViewModel,
            frozenDurationSeconds = if (frozenDurationSeconds > 0) frozenDurationSeconds else null,
            onEnd = { duration ->
                frozenDurationSeconds = duration
                Log.d("LogPracticeSession", "Step 2 → 3: Duration frozen at ${practiceSessionViewModel.formatDuration(duration)}")
                currentStep = 3
            },
            onBack = {
                Log.d("LogPracticeSession", "Step 2 → 1: Back")
                currentStep = 1
            }
        )
        
        3 -> Step3_CompleteSession(
            instrument = instrument,
            practiceType = practiceType,
            practiceFocus = practiceFocus,
            frozenDurationSeconds = frozenDurationSeconds,
            difficulty = difficulty,
            notes = notes,
            onDifficultyChange = { difficulty = it },
            onNotesChange = { notes = it },
            practiceSessionViewModel = practiceSessionViewModel,
            onComplete = {
                Log.d("LogPracticeSession", "Step 3: Completing - Difficulty: $difficulty")
                
                // calculate xp
                val durationMinutes = frozenDurationSeconds / 60
                val multiplier = practiceSessionViewModel.getDifficultyMultiplier(difficulty)
                earnedXP = (durationMinutes * multiplier).toInt()
                
                // store session ID before saving
                sessionId = practiceSessionViewModel.currentSession.value?.id ?: ""
                
                Log.d("LogPracticeSession", "XP: ${durationMinutes}min × ${multiplier}x = $earnedXP XP, SessionID: $sessionId")
                
                // save session with frozen duration
                practiceSessionViewModel.endPracticeSession(notes, difficulty, frozenDurationSeconds)
                showLoadingDialog = true
                
                // handle post-save operations
                GlobalScope.launch {
                    delay(2000) // wait for firebase save
                    userViewModel.getUserList() // refresh user data
                    delay(1000) // wait for state update
                    
                    // check achievements with updated user data
                    loggedInUser?.let { achievementViewModel.checkAndUnlockAchievements(it) }
                    
                    showLoadingDialog = false
                    showSuccessDialog = true
                    Log.d("LogPracticeSession", "Session complete: $earnedXP XP, ${practiceSessionViewModel.formatDuration(frozenDurationSeconds)}")
                }
            },
            onBack = {
                Log.d("LogPracticeSession", "Step 3 → 2: Back")
                currentStep = 2
            }
        )
    }
    
    if (showLoadingDialog) {
        LoadingDialog()
    }
    
    // dialogs
    if (showLoadingDialog) {
        LoadingDialog()
    }
    
    if (showSuccessDialog) {
        val totalXP = loggedInUser?.xp ?: 0
        val levelInfo = computeLevel(totalXP)
        SessionCompleteDialog(
            earnedXP = earnedXP,
            durationSeconds = frozenDurationSeconds,
            currentLevel = levelInfo.level,
            totalXP = totalXP,
            instrument = instrument,
            practiceType = practiceType,
            pieceOrFocus = practiceFocus,
            difficulty = difficulty,
            onDismiss = {
                showSuccessDialog = false
                navController.popBackStack()
            },
            onShare = {
                showSuccessDialog = false
                // navigate to create practice post
                navController.navigate(
                    "create_practice_post/$instrument/$practiceType/$practiceFocus/$difficulty/$frozenDurationSeconds/$earnedXP/$sessionId"
                )
            }
        )
    }
}

@Composable
private fun LoadingDialog() {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⏳", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("Saving Session...", fontFamily = Poppins, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(color = DarkGreen, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text(
                    "Updating your progress",
                    fontFamily = Poppins, fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = { },
        containerColor = WhiteBox,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun SessionCompleteDialog(
    earnedXP: Int,
    durationSeconds: Int,
    currentLevel: Int,
    totalXP: Int,
    instrument: String,
    practiceType: String,
    pieceOrFocus: String,
    difficulty: String,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    // calculate start and end xp
    val startXP = totalXP - earnedXP
    val endXP = totalXP
    
    val startLevelInfo = computeLevel(startXP)
    val endLevelInfo = computeLevel(endXP)
    
    val startProgress = (startLevelInfo.currentXP.toFloat() / startLevelInfo.xpToNextLevel).coerceIn(0f, 1f)
    val endProgress = (endLevelInfo.currentXP.toFloat() / endLevelInfo.xpToNextLevel).coerceIn(0f, 1f)
    
    // animate progress bar
    val animatedProgress = remember { Animatable(startProgress) }
    
    // animate xp value
    val animatedXP = remember { Animatable(startLevelInfo.currentXP.toFloat()) }
    
    LaunchedEffect(Unit) {
        // animate both progress and xp value together
        launch {
            animatedProgress.animateTo(endProgress, tween(1500, easing = FastOutSlowInEasing))
        }
        launch {
            animatedXP.animateTo(endLevelInfo.currentXP.toFloat(), tween(1000, easing = FastOutSlowInEasing))
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("⭐", fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("+$earnedXP XP", fontFamily = Poppins, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                }
                Spacer(Modifier.height(20.dp))
                LevelProgressBar(
                    level = endLevelInfo.level,
                    currentXP = animatedXP.value.toInt(),
                    targetXP = endLevelInfo.xpToNextLevel,
                    progress = animatedProgress.value
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Keep practicing to reach Level ${endLevelInfo.level + 1}!",
                    fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onShare,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Share Session", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, DarkGreen)
                ) {
                    Text("Close", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                }
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
    frozenDurationSeconds: Int?,
    onEnd: (Int) -> Unit,
    onBack: () -> Unit
) {
    val currentSession by practiceSessionViewModel.currentSession.collectAsState()
    
    // timer state
    var pausedElapsedSeconds by remember { mutableIntStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var timeOffset by remember { mutableLongStateOf(0L) }
    
    // calculate elapsed time: base (frozen) + additional time in this step
    val elapsedSeconds = if (isTimerRunning && currentSession != null) {
        val baseSeconds = frozenDurationSeconds ?: 0
        val additionalSeconds = ((currentTime - timeOffset) / 1000).toInt()
        baseSeconds + additionalSeconds
    } else {
        pausedElapsedSeconds
    }
    
    // initialize timer on mount
    LaunchedEffect(Unit) {
        timeOffset = System.currentTimeMillis()
        isTimerRunning = true
        val message = if (frozenDurationSeconds != null) {
            "Resuming from ${practiceSessionViewModel.formatDuration(frozenDurationSeconds)}"
        } else {
            "Starting fresh timer"
        }
        Log.d("LogPracticeSession", "Step 2: $message")
    }
    
    // timer tick loop
    LaunchedEffect(currentSession, isTimerRunning) {
        if (currentSession != null && isTimerRunning) {
            while (isTimerRunning) {
                delay(1000)
                currentTime = System.currentTimeMillis()
            }
        }
    }
    
    // stop timer when leaving
    DisposableEffect(Unit) {
        onDispose {
            val finalElapsed = if (currentSession != null) {
                val baseSeconds = frozenDurationSeconds ?: 0
                val additionalSeconds = ((System.currentTimeMillis() - timeOffset) / 1000).toInt()
                baseSeconds + additionalSeconds
            } else {
                pausedElapsedSeconds
            }
            pausedElapsedSeconds = finalElapsed
            isTimerRunning = false
            Log.d("LogPracticeSession", "Step 2: Stopped at ${practiceSessionViewModel.formatDuration(finalElapsed)}")
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
                            onClick = {
                                // always capture current elapsed time
                                pausedElapsedSeconds = elapsedSeconds
                                isTimerRunning = false
                                onEnd(elapsedSeconds)
                            },
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
    instrument: String, practiceType: String, practiceFocus: String,
    frozenDurationSeconds: Int,
    difficulty: String, notes: String,
    onDifficultyChange: (String) -> Unit, onNotesChange: (String) -> Unit,
    practiceSessionViewModel: PracticeSessionViewModel,
    onComplete: () -> Unit, onBack: () -> Unit
) {
    val isLoading by practiceSessionViewModel.isLoading.collectAsState()
    var difficultyExpanded by remember { mutableStateOf(false) }
    
    // use the frozen duration from when end session was pressed
    val hours = frozenDurationSeconds / 3600
    val minutes = (frozenDurationSeconds % 3600) / 60
    val seconds = frozenDurationSeconds % 60
    
    Column(Modifier.fillMaxSize().background(WhiteBox)) {
        StepHeader("Step 3 of 3: Session Details", onBack)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // session recap
            Card(
                colors = CardDefaults.cardColors(containerColor = LightGreen.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, LightGreen.copy(alpha = 0.3f))
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MusicNote, null, tint = DarkGreen, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Session Recap", fontFamily = Poppins, fontWeight = FontWeight.Bold, color = DarkGreen, fontSize = 16.sp)
                    }
                    HorizontalDivider(
                        Modifier,
                        DividerDefaults.Thickness,
                        color = LightGreen.copy(alpha = 0.3f)
                    )

                    SessionRecapRow("Instrument", instrument)
                    SessionRecapRow("Practice Type", practiceType)
                    SessionRecapRow("Focus", practiceFocus)
                    SessionRecapRow("Duration", String.format("%02d:%02d:%02d", hours, minutes, seconds))
                }
            }
            
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
private fun SessionRecapRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            fontSize = 13.sp
        )
        Text(
            text = value,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            color = DarkGreen,
            fontSize = 14.sp
        )
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