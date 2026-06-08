package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.example.data.local.ParentDatabase
import com.example.data.repository.ParentRepository
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ParentViewModel
import com.example.ui.viewmodel.ParentViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize our offline-first core architecture layers
        val database = ParentDatabase.getDatabase(applicationContext)
        val repository = ParentRepository(database.parentDao(), applicationContext)
        
        // Initialize AdMob Mobile Ads SDK
        com.example.ui.AdMobManager.initialize(applicationContext)
        
        // Instantiate ViewModel with Activity/Fragment lifecycle awareness
        val viewModel = ViewModelProvider(
            this, 
            ParentViewModelFactory(repository)
        )[ParentViewModel::class.java]

        enableEdgeToEdge()

        setContent {
            // Hot swap Dark Theme on-the-fly based on Settings toggles
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

