package com.example.practal


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.practal.ui.theme.Poppins

@Composable
fun SignUpScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGreen)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {

            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = DarkGreen,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign Up",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
                color = WhiteBox
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(32.dp))


                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = DarkGreen,
                        cursorColor = DarkGreen
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = DarkGreen,
                        cursorColor = DarkGreen
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = DarkGreen,
                        cursorColor = DarkGreen
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = DarkGreen,
                        cursorColor = DarkGreen
                    )
                )


                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = false, onCheckedChange = {})
                    Text("I agree to the Terms & Conditions", fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { navController.navigate("welcome_profile") },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text("Sign Up", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("OR", color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text("Connect with Google", color = DarkGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins)
                }
            }
        }
    }
}
