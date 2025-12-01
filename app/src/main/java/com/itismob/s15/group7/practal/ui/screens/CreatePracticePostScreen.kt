package com.itismob.s15.group7.practal.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.itismob.s15.group7.practal.DarkGreen
import com.itismob.s15.group7.practal.WhiteBox
import com.itismob.s15.group7.practal.domain.controller.PostViewModel
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.ui.theme.Poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePracticePostScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    postViewModel: PostViewModel,
    instrument: String,
    practiceType: String,
    pieceOrFocus: String,
    difficulty: String,
    durationMinutes: Int,
    earnedXP: Int,
    sessionId: String
) {
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    var caption by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isPosting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // convert duration
    val hours = durationMinutes / 3600
    val minutes = (durationMinutes % 3600) / 60
    val seconds = durationMinutes % 60
    val formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Practice Session", fontFamily = Poppins, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = WhiteBox
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // session details preview
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFB7D3B0).copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = DarkGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Practice Session Details",
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen,
                            fontSize = 16.sp,
                            fontFamily = Poppins
                        )
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SessionDetailItem("Instrument", instrument)
                        SessionDetailItem("Duration", formattedDuration)
                        SessionDetailItem("Difficulty", difficulty)
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SessionDetailItem("Practice Type", practiceType)
                        SessionDetailItem("XP Earned", "+$earnedXP XP")
                    }
                    
                    if (pieceOrFocus.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("Focus", fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
                        Text(pieceOrFocus, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = Poppins)
                    }
                }
            }
            
            // caption input
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Add a Caption",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = Poppins,
                        color = DarkGreen
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = caption,
                        onValueChange = { caption = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Share your thoughts about this session...", fontFamily = Poppins) },
                        minLines = 4,
                        maxLines = 8,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                }
            }
            
            // img
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Add Photo (Optional)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = Poppins,
                        color = DarkGreen
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    if (selectedImageUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { selectedImageUri = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                            ) {
                                Icon(Icons.Default.Close, "Remove image", tint = Color.Black)
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen),
                            border = BorderStroke(2.dp, DarkGreen.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Choose Photo", fontFamily = Poppins)
                        }
                    }
                }
            }
            
            // post button
            Button(
                onClick = {
                    isPosting = true
                    // practice post
                    postViewModel.createPracticePost(
                        ownerEmail = loggedInUser?.email ?: "",
                        ownerImg = loggedInUser?.image ?: "",
                        postImg = selectedImageUri?.toString() ?: "",
                        caption = caption,
                        sessionId = sessionId,
                        instrument = instrument,
                        practiceType = practiceType,
                        pieceOrFocus = pieceOrFocus,
                        durationMinutes = durationMinutes,
                        difficulty = difficulty,
                        earnedXP = earnedXP,
                        onSuccess = {
                            isPosting = false
                            errorMessage = null
                            navController.popBackStack("dashboard", inclusive = false)
                        },
                        onFailure = { e ->
                            isPosting = false
                            errorMessage = "Failed to post session: ${e.message}"
                        }
                    )
                },
                enabled = caption.isNotEmpty() && !isPosting,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                if (isPosting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Post Session", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            // error
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    fontSize = 14.sp,
                    color = Color.Red,
                    fontFamily = Poppins,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SessionDetailItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = Poppins)
    }
}
