package com.unilasalle.tp.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.unilasalle.tp.MainActivity
import com.unilasalle.tp.services.database.controllers.UsersController
import com.unilasalle.tp.ui.components.RegisterForm
import com.unilasalle.tp.viewmodels.RegisterViewModel
import com.unilasalle.tp.viewmodels.RegisterViewModelFactory
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen(navController: NavController, usersController: UsersController) {
    val context = LocalContext.current
    val registerViewModel = ViewModelProvider(context as ComponentActivity, RegisterViewModelFactory(usersController))[RegisterViewModel::class.java]

    Column {
        RegisterForm { email, password ->
            registerViewModel.register(email, password) { user ->
                if (user != null) {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("userId", user.id.toLong())
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        Button(
            onClick = { navController.navigate("login") },
            shape = RoundedCornerShape(50), // Rounded button
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC)), // Secondary button color
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Back to Login", fontSize = 16.sp, color = Color.White) // White text for contrast
        }
    }
}
