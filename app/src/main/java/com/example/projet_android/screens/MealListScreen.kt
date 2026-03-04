package com.example.projet_android.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.projet_android.data.local.MealEntity
import com.example.projet_android.viewmodel.MealViewModel

val categories = listOf("All", "Chicken", "Beef", "Seafood", "Vegetarian", "Dessert", "Pasta")

@Composable
fun MealListScreen(
    viewModel: MealViewModel,
    onMealClick: (String) -> Unit
) {
    val meals by viewModel.meals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // State de la liste pour détecter quand on arrive en bas
    val listState = rememberLazyListState()

    // Détecte quand on arrive au dernier élément
    val reachedBottom by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisible != null && lastVisible.index >= totalItems - 2
        }
    }

    // Charge la page suivante quand on arrive en bas
    LaunchedEffect(reachedBottom) {
        if (reachedBottom && meals.isNotEmpty()) {
            viewModel.loadNextPage()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.searchMeals("a")
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Barre de recherche
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchMeals(it.ifBlank { "a" })
            },
            placeholder = { Text("Search...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true
        )

        // Filtre catégories
        LazyRow(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        selectedCategory = category
                        if (category == "All")
                            viewModel.searchMeals(searchQuery.ifBlank { "a" })
                        else
                            viewModel.filterByCategory(category)
                    },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangeFood,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Contenu
        when {
            isLoading && meals.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangeFood)
                }
            }
            error != null && meals.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Erreur réseau", color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.searchMeals(searchQuery.ifBlank { "a" }) },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeFood)
                        ) { Text("Réessayer") }
                    }
                }
            }
            else -> {
                LazyColumn(state = listState) {
                    items(meals) { meal ->
                        MealCard(meal = meal, onClick = { onMealClick(meal.id) })
                    }
                    // Indicateur de chargement en bas
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = OrangeFood)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealCard(meal: MealEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.height(100.dp)) {
            AsyncImage(
                model = meal.thumbnail,
                contentDescription = meal.name,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(meal.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(meal.category, color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}