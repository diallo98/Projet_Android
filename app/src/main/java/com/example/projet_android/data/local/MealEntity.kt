package com.example.projet_android.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val thumbnail: String,
    val instructions: String,
    val ingredients: String // on stocke les ingrédients en JSON string
)