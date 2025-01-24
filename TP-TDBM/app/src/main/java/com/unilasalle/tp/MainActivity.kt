package com.unilasalle.tp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.unilasalle.tp.navigations.BottomNavigationBar
import com.unilasalle.tp.navigations.NavigationGraph
import com.unilasalle.tp.navigations.rememberNavigationState
import com.unilasalle.tp.services.database.AppDatabase
import com.unilasalle.tp.services.database.DatabaseProvider
import com.unilasalle.tp.services.database.entities.User
import com.unilasalle.tp.ui.theme.TPTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), DatabaseProvider {

    override val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            val navigationState = rememberNavigationState()

            var user: User? = null
            lifecycleScope.launch {
                user = database.usersController().getUserById(intent.getLongExtra("userId", 0L))
            }
            user = user ?: User(0, "Guest", "")

            TPTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = { BottomNavigationBar(navController = navController, state = navigationState) }
                    ) { innerPadding ->
                        user?.let {
                            NavigationGraph(
                                navController = navController,
                                cartItemController = database.cartItemController(),
                                context = this,
                                user = it,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}