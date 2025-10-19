package com.itismob.s15.group7.practal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.itismob.s15.group7.practal.ui.theme.Poppins

@Composable
fun ProgressAnalyticsScreen(username: String, navController: NavHostController) {
    var selectedFilter by remember { mutableStateOf("Weekly") }
    val filters = listOf("Weekly", "Monthly", "Yearly")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBox)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 92.dp)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen)
                .padding(24.dp)
                .height(64.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hey $username! 👋",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = Poppins
                    )
                    Text(
                        text = "Your Progress",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = Poppins
                    )
                }
                // Streak badge
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 32.sp
                    )
                }
            }
        }

        // Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            filters.forEach { filter ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedFilter == filter) DarkGreen else Color.White)
                        .padding(vertical = 12.dp)
                        .clickable { selectedFilter = filter },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (selectedFilter == filter) Color.White else DarkGreen
                    )
                }
            }
        }

        // Stats Grid 
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Total Practice Time
            StatsCard(
                icon = Icons.Default.AccessTime,
                title = "Practice Time",
                value = getStatValue(selectedFilter, "time"),
                subtitle = "+2.5h",
                color = DarkGreen,
                modifier = Modifier.weight(1f)
            )

            // Practice Sessions
            StatsCard(
                icon = Icons.Default.MusicNote,
                title = "Sessions",
                value = getStatValue(selectedFilter, "sessions"),
                subtitle = "logged",
                color = DarkGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Streak Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 40.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "12 Day Streak!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen,
                            fontFamily = Poppins
                        )
                        Text(
                            text = "Longest: 18 days",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontFamily = Poppins
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = DarkGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Practice Frequency Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Practice Frequency",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
                    fontFamily = Poppins
                )
                Spacer(modifier = Modifier.height(16.dp))

                PracticeFrequencyChart(selectedFilter)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Achievements Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Recent Achievements",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
                    fontFamily = Poppins
                )
                Spacer(modifier = Modifier.height(12.dp))

                AchievementItem(
                    icon = "🏆",
                    title = "100 Hours Milestone",
                    date = "Oct 15, 2025"
                )
                AchievementItem(
                    icon = "🔥",
                    title = "10-Day Streak",
                    date = "Oct 12, 2025"
                )
                AchievementItem(
                    icon = "🎯",
                    title = "First Challenge Complete",
                    date = "Oct 8, 2025"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Instrument Breakdown
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Practice by Instrument",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen,
                    fontFamily = Poppins
                )
                Spacer(modifier = Modifier.height(16.dp))

                InstrumentBreakdownItem("Piano", 45, DarkGreen)
                InstrumentBreakdownItem("Guitar", 30, CardAccent)
                InstrumentBreakdownItem("Violin", 15, SoftGreen)
                InstrumentBreakdownItem("Voice", 10, LightGreen)
            }
        }
    }
}

@Composable
fun StatsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                fontFamily = Poppins
            )
            
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
            
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.Gray.copy(alpha = 0.6f),
                fontFamily = Poppins
            )
        }
    }
}

@Composable
fun PracticeFrequencyChart(filter: String) {
    val days = when (filter) {
        "Weekly" -> listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        "Monthly" -> listOf("W1", "W2", "W3", "W4")
        else -> listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    }
    
    val heights = when (filter) {
        "Weekly" -> listOf(0.8f, 0.6f, 0.9f, 0.7f, 0.85f, 0.5f, 0.4f)
        "Monthly" -> listOf(0.7f, 0.8f, 0.6f, 0.9f)
        else -> listOf(0.5f, 0.6f, 0.7f, 0.8f, 0.75f, 0.85f, 0.9f, 0.8f, 0.7f, 0.95f, 0.6f, 0.5f)
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEachIndexed { index, _ ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (filter == "Yearly") 20.dp else 30.dp)
                            .height((150 * heights.getOrElse(index) { 0.5f }).dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(DarkGreen.copy(alpha = 0.7f))
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { day ->
                Text(
                    text = day,
                    fontSize = if (filter == "Yearly") 10.sp else 12.sp,
                    color = Color.Gray,
                    fontFamily = Poppins,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AchievementItem(icon: String, title: String, date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 32.sp,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                fontFamily = Poppins
            )
            Text(
                text = date,
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = Poppins
            )
        }
    }
}

@Composable
fun InstrumentBreakdownItem(instrument: String, percentage: Int, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = instrument,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                fontFamily = Poppins
            )
            Text(
                text = "$percentage%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = Poppins
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

// Helper
fun getStatValue(filter: String, type: String): String {
    return when (type) {
        "time" -> when (filter) {
            "Weekly" -> "12.5 hrs"
            "Monthly" -> "48 hrs"
            "Yearly" -> "520 hrs"
            else -> "12.5 hrs"
        }
        "sessions" -> when (filter) {
            "Weekly" -> "15"
            "Monthly" -> "58"
            "Yearly" -> "642"
            else -> "15"
        }
        else -> "0"
    }
}

fun getSessionsCount(filter: String): Int {
    return when (filter) {
        "Weekly" -> 15
        "Monthly" -> 58
        "Yearly" -> 642
        else -> 15
    }
}
