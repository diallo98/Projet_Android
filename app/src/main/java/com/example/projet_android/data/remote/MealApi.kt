package com.example.projet_android.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class MealListResponse(val meals: List<MealDto>?)
data class CategoryListResponse(val categories: List<CategoryDto>?)

data class CategoryDto(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String
)

interface MealApi {

    // Recherche par nom
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealListResponse

    // Détail par ID
    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): MealListResponse

    // Liste des catégories
    @GET("categories.php")
    suspend fun getCategories(): CategoryListResponse

    // Filtre par catégorie
    @GET("filter.php")
    suspend fun filterByCategory(@Query("c") category: String): MealListResponse
}