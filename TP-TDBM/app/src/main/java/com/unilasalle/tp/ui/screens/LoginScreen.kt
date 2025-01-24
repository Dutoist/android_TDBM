package com.unilasalle.tp.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.unilasalle.tp.MainActivity
import com.unilasalle.tp.services.database.controllers.UsersController
import com.unilasalle.tp.ui.components.LoginForm
import com.unilasalle.tp.viewmodels.LoginViewModel
import com.unilasalle.tp.viewmodels.LoginViewModelFactory

@Composable
fun LoginScreen(navController: NavController, usersController: UsersController) {
    val context = LocalContext.current
    val loginViewModel = ViewModelProvider(
        context as ComponentActivity,
        LoginViewModelFactory(usersController)
    )[LoginViewModel::class.java]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Login form
        LoginForm { email, password ->
            loginViewModel.login(email, password) { user ->
                if (user != null) {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("userId", user.id.toLong())
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Register button with updated design
        Button(
            onClick = { navController.navigate("register") },
            shape = RoundedCornerShape(50), // Rounded shape
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3700B3), // Primary color (deep purple)
                contentColor = Color.White // White text
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Register", fontSize = 16.sp)
        }
    }
}
