package com.example.projet_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet_android.data.local.AppDatabase
import com.example.projet_android.data.repository.MealRepository
import com.example.projet_android.screens.MealDetailScreen
import com.example.projet_android.screens.MealListScreen
import com.example.projet_android.screens.SplashScreen
import com.example.projet_android.ui.theme.Projet_AndroidTheme
import com.example.projet_android.viewmodel.MealViewModel
import com.example.projet_android.viewmodel.MealViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(this)
        val repository = MealRepository(db)
        val factory = MealViewModelFactory(repository)

        setContent {
            Projet_AndroidTheme {
                val viewModel: MealViewModel = viewModel(factory = factory)
                var currentScreen by remember { mutableStateOf("splash") }
                var selectedMealId by remember { mutableStateOf("") }

                when (currentScreen) {
                    "splash" -> SplashScreen(
                        onSplashFinished = { currentScreen = "list" }
                    )
                    "list" -> MealListScreen(
                        viewModel = viewModel,
                        onMealClick = { mealId ->
                            selectedMealId = mealId
                            currentScreen = "detail"
                        }
                    )
                    "detail" -> MealDetailScreen(
                        mealId = selectedMealId,
                        viewModel = viewModel,
                        onBack = { currentScreen = "list" }
                    )
                }
            }
        }
    }
}