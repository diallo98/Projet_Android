package com.example.projet_android.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.projet_android.viewmodel.MealViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(
    mealId: String,
    viewModel: MealViewModel,
    onBack: () -> Unit
) {
    val meal by viewModel.selectedMeal.collectAsState()

    LaunchedEffect(mealId) {
        viewModel.getMealById(mealId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(meal?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OrangeFood
                )
            )
        }
    ) { padding ->
        meal?.let { m ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = m.thumbnail,
                    contentDescription = m.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(m.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(m.category, color = OrangeFood, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(16.dp))

                    Text("Ingrédients", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    m.ingredients.split("|").filter { it.isNotBlank() }.forEach { ingredient ->
                        Text("• $ingredient", fontSize = 14.sp)
                        Spacer(Modifier.height(4.dp))
                    }

                    if (m.instructions.isNotBlank()) {
                        Spacer(Modifier.height(16.dp))
                        Text("Instructions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(m.instructions, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}