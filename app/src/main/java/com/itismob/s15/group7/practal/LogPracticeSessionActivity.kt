package com.itismob.s15.group7.practal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.itismob.s15.group7.practal.ui.theme.Poppins

@Composable
fun LogPracticeSessionScreen(navController: NavHostController) {
    var instrument by remember { mutableStateOf("") }
    var practiceFocus by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showInstrumentDropdown by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val instruments = listOf(
        "🎹 Piano", "🎸 Guitar", "🎻 Violin", "🎻 Cello", "🪈 Flute", 
        "🎷 Clarinet", "🎷 Saxophone", "🎺 Trumpet", "🥁 Drums", "🎸 Bass", "🎤 Voice"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
            .height(64.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(DarkGreen, CardAccent)
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Log Practice",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = Poppins
                    )
                    Text(
                        text = "Track your progress and build streaks!",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontFamily = Poppins
                    )
                }
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Instrument Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = DarkGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "What did you practice?",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = DarkGreen
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Box {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showInstrumentDropdown = true }
                        ) {
                            OutlinedTextField(
                                value = instrument,
                                onValueChange = { },
                                readOnly = true,
                                enabled = false,
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select instrument",
                                        tint = DarkGreen
                                    )
                                },
                                placeholder = { Text("Choose your instrument", fontFamily = Poppins) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DarkGreen,
                                    unfocusedBorderColor = Color.LightGray,
                                    disabledBorderColor = Color.LightGray,
                                    disabledTextColor = Color.Black,
                                    disabledPlaceholderColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showInstrumentDropdown,
                            onDismissRequest = { showInstrumentDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            instruments.forEach { inst ->
                                DropdownMenuItem(
                                    text = { Text(inst, fontFamily = Poppins) },
                                    onClick = {
                                        instrument = inst
                                        showInstrumentDropdown = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = practiceFocus,
                        onValueChange = { practiceFocus = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { 
                            Text("What did you work on? (e.g., scales, Bach sonata)", fontFamily = Poppins) 
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Time Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = DarkGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "How long did you practice?",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = DarkGreen
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Started at",
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = startTime,
                                onValueChange = { startTime = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("HH:MM", fontFamily = Poppins, fontSize = 14.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DarkGreen,
                                    unfocusedBorderColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ended at",
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = endTime,
                                onValueChange = { endTime = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("HH:MM", fontFamily = Poppins, fontSize = 14.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DarkGreen,
                                    unfocusedBorderColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                    
                    // Duration Display
                    if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = LightGreen.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Time:",
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Medium,
                                    color = DarkGreen,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "⏱️ ${calculateDuration(startTime, endTime)}",
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkGreen,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Notes
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💭 Notes (Optional)",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = DarkGreen
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "How did it go? What did you learn?",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        placeholder = { 
                            Text(
                                "Jot down your thoughts...", 
                                fontFamily = Poppins,
                                fontSize = 14.sp
                            ) 
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    if (instrument.isNotEmpty() && practiceFocus.isNotEmpty() && 
                        startTime.isNotEmpty() && endTime.isNotEmpty()) {
                        showSuccessDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkGreen,
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = instrument.isNotEmpty() && practiceFocus.isNotEmpty() && 
                         startTime.isNotEmpty() && endTime.isNotEmpty()
            ) {
                Text(
                    text = if (instrument.isNotEmpty() && practiceFocus.isNotEmpty() && 
                              startTime.isNotEmpty() && endTime.isNotEmpty()) 
                              "Save Practice Session" else "Fill in required fields",
                    fontSize = 16.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
        
        // Success Dialog
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Awesome!",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = DarkGreen
                        )
                    }
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Practice session logged successfully!",
                            fontFamily = Poppins,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "You're one step closer to mastery!",
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = LightGreen.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Keep your streak going! 🔥",
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkGreen,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            showSuccessDialog = false
                            instrument = ""
                            practiceFocus = ""
                            startTime = ""
                            endTime = ""
                            notes = ""
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DarkGreen
                        )
                    ) {
                        Text("Log Another", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = WhiteBox,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

// calculate duration
fun calculateDuration(start: String, end: String): String {
    return try {
        val startParts = start.split(":")
        val endParts = end.split(":")
        
        if (startParts.size == 2 && endParts.size == 2) {
            val startMinutes = startParts[0].toInt() * 60 + startParts[1].toInt()
            val endMinutes = endParts[0].toInt() * 60 + endParts[1].toInt()
            val duration = if (endMinutes >= startMinutes) {
                endMinutes - startMinutes
            } else {
                (24 * 60) - startMinutes + endMinutes
            }
            
            val hours = duration / 60
            val minutes = duration % 60
            
            if (hours > 0) {
                "${hours}h ${minutes}m"
            } else {
                "${minutes}m"
            }
        } else {
            "Invalid time"
        }
    } catch (_: Exception) {
        "Invalid time"
    }
}
