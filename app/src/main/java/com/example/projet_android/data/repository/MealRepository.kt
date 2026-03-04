package com.example.projet_android.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.projet_android.data.local.AppDatabase
import com.example.projet_android.data.local.MealEntity
import com.example.projet_android.data.remote.MealDto
import com.example.projet_android.data.remote.RetrofitInstance

class MealRepository(private val db: AppDatabase, private val context: Context) {

    private val api = RetrofitInstance.api
    private val dao = db.mealDao()

    // Vérifie si internet est disponible
    private fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun searchMeals(query: String): List<MealEntity> {
        return if (isOnline()) {
            try {
                val response = api.searchMeals(query)
                val meals = response.meals?.map { dto -> dto.toEntity() } ?: emptyList()
                if (meals.isNotEmpty()) dao.upsertMeals(meals)
                meals
            } catch (e: Exception) {
                dao.searchMeals(query)
            }
        } else {
            dao.searchMeals(query)
        }
    }

    suspend fun getMealsByCategory(category: String): List<MealEntity> {
        return if (isOnline()) {
            try {
                val response = api.filterByCategory(category)
                val meals = response.meals?.map { dto -> dto.toEntity() } ?: emptyList()
                if (meals.isNotEmpty()) dao.upsertMeals(meals)
                meals
            } catch (e: Exception) {
                dao.getMealsByCategory(category)
            }
        } else {
            dao.getMealsByCategory(category)
        }
    }

    suspend fun getMealById(id: String): MealEntity? {
        return if (isOnline()) {
            try {
                val response = api.getMealById(id)
                val meal = response.meals?.firstOrNull()?.toEntity()
                if (meal != null) dao.upsertMeals(listOf(meal))
                meal
            } catch (e: Exception) {
                dao.getMealById(id)
            }
        } else {
            dao.getMealById(id)
        }
    }

    suspend fun getAllMealsFromCache(): List<MealEntity> {
        return dao.getAllMeals()
    }

    fun isNetworkAvailable(): Boolean = isOnline()

    private fun MealDto.toEntity(): MealEntity {
        val ingredients = listOfNotNull(
            strIngredient1?.takeIf { it.isNotBlank() }?.let { "$it - ${strMeasure1 ?: ""}" },
            strIngredient2?.takeIf { it.isNotBlank() }?.let { "$it - ${strMeasure2 ?: ""}" },
            strIngredient3?.takeIf { it.isNotBlank() }?.let { "$it - ${strMeasure3 ?: ""}" },
            strIngredient4?.takeIf { it.isNotBlank() }?.let { "$it - ${strMeasure4 ?: ""}" },
            strIngredient5?.takeIf { it.isNotBlank() }?.let { "$it - ${strMeasure5 ?: ""}" },
            strIngredient6?.takeIf { it.isNotBlank() }?.let { "$it - ${strMeasure6 ?: ""}" },
            strIngredient7?.takeIf { it.isNotBlank() }?.let { "$it - ${strMeasure7 ?: ""}" },
            strIngredient8?.takeIf { it.isNotBlank() }?.let { "$it - ${strMeasure8 ?: ""}" },
            strIngredient9?.takeIf { it.isNotBlank() }?.let { "$it - ${strMeasure9 ?: ""}" },
            strIngredient10?.takeIf { it.isNotBlank() }?.let { "$it - ${strMeasure10 ?: ""}" },
        )
        return MealEntity(
            id = idMeal,
            name = strMeal,
            category = strCategory ?: "",
            thumbnail = strMealThumb ?: "",
            instructions = strInstructions ?: "",
            ingredients = ingredients.joinToString("|")
        )
    }
}