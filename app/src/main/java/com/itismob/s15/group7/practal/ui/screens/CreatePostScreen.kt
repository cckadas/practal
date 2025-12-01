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
fun CreatePostScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    postViewModel: PostViewModel
) {
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    var caption by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isPosting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post", fontFamily = Poppins, fontWeight = FontWeight.Bold) },
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
            // user info
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (loggedInUser?.image?.isNotEmpty() == true) {
                            AsyncImage(
                                model = loggedInUser!!.image,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${loggedInUser?.firstname} ${loggedInUser?.lastname}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = Poppins
                        )
                        Text(
                            text = loggedInUser?.email ?: "",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontFamily = Poppins
                        )
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
                        "What's on your mind?",
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
                        placeholder = { Text("Share your music journey, tips, or thoughts...", fontFamily = Poppins) },
                        minLines = 5,
                        maxLines = 10,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                }
            }
            
            // img upload
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
                                .height(300.dp)
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
                                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(24.dp))
                            ) {
                                Icon(Icons.Default.Close, "Remove image", tint = Color.Black)
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen),
                            border = BorderStroke(2.dp, DarkGreen.copy(alpha = 0.3f))
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Choose Photo",
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                )
                                Text(
                                    "Share a photo with your post",
                                    fontFamily = Poppins,
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
            
            // post btn
            Button(
                onClick = {
                    isPosting = true
                    // create regular post
                    postViewModel.createRegularPost(
                        ownerEmail = loggedInUser?.email ?: "",
                        ownerImg = loggedInUser?.image ?: "",
                        postImg = selectedImageUri?.toString() ?: "",
                        caption = caption,
                        onSuccess = {
                            isPosting = false
                            errorMessage = null
                            navController.popBackStack()
                        },
                        onFailure = { e ->
                            isPosting = false
                            errorMessage = "Failed to create post: ${e.message}"
                        }
                    )
                },
                enabled = caption.isNotEmpty() && !isPosting,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkGreen,
                    disabledContainerColor = Color.Gray
                )
            ) {
                if (isPosting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Share Post", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            // hint
            if (caption.isEmpty()) {
                Text(
                    text = "Caption is required to post",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = Poppins,
                    modifier = Modifier.fillMaxWidth()
                )
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
