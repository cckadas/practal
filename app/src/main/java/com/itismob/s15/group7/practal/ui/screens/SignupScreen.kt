package com.itismob.s15.group7.practal.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.itismob.s15.group7.practal.DarkGreen
import com.itismob.s15.group7.practal.LightGreen
import com.itismob.s15.group7.practal.WhiteBox
import com.itismob.s15.group7.practal.domain.controller.UserViewModel
import com.itismob.s15.group7.practal.ui.theme.Poppins
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var birthmonth by remember { mutableStateOf("") }
    var birthyear by remember { mutableStateOf("") }
    var agree by remember { mutableStateOf(false) }

    val context = LocalContext.current


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
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Credentials",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, top = 8.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
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
                    value = password,
                    onValueChange = { password = it },
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

                Text(
                    text = "Personal Info",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, top = 8.dp)
                )

                OutlinedTextField(
                    value = firstname,
                    onValueChange = { firstname = it },
                    label = { Text("Firstname") },
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
                    value = lastname,
                    onValueChange = { lastname = it },
                    label = { Text("Lastname") },
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


                Text(
                    text = "Birthdate",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, top = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = birthday,
                        onValueChange = { birthday = it },
                        label = { Text("Day") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(55.dp),
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

                    OutlinedTextField(
                        value = birthmonth,
                        onValueChange = { birthmonth = it },
                        label = { Text("Month") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(55.dp),
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

                    OutlinedTextField(
                        value = birthyear,
                        onValueChange = { birthyear = it },
                        label = { Text("Year") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(2f)
                            .height(55.dp),
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
                }

                Spacer(modifier = Modifier.height(16.dp))


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = agree, onCheckedChange = { agree = it })
                    Text("I agree to the Terms & Conditions", fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank() || firstname.isBlank() || lastname.isBlank() || birthday.isBlank() || birthmonth.isBlank() || birthyear.isBlank()
                        ) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        } else if (!agree) {
                            Toast.makeText(context, "Please agree to Terms & Conditions", Toast.LENGTH_SHORT).show()
                        } else {

                            val existingUser = userViewModel.userList.value.find { it.email == email }
                            if (existingUser != null) {
                                Toast.makeText(context, "Email already exists", Toast.LENGTH_SHORT).show()
                            } else {
                                val birthdate = "$birthday-$birthmonth-$birthyear"

                                userViewModel.tempSignUpData.value = userViewModel.tempSignUpData.value.copy(
                                    firstname = firstname,
                                    lastname = lastname,
                                    birthdate = birthdate,
                                    email = email,
                                    password = password
                                )

                                navController.navigate("welcome_profile")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        "Sign Up",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                }

            }
        }
    }
}
