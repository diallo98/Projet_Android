package com.example.projet_android.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.projet_android.data.local.MealEntity

@Dao
interface MealDao {

    @Query("SELECT * FROM meals")
    suspend fun getAllMeals(): List<MealEntity>

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealById(id: String): MealEntity?

    @Query("SELECT * FROM meals WHERE name LIKE '%' || :query || '%'")
    suspend fun searchMeals(query: String): List<MealEntity>

    @Query("SELECT * FROM meals WHERE category = :category")
    suspend fun getMealsByCategory(category: String): List<MealEntity>

    @Upsert
    suspend fun upsertMeals(meals: List<MealEntity>)

    @Query("DELETE FROM meals")
    suspend fun clearAll()
}