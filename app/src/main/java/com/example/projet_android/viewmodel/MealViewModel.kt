package com.example.projet_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_android.data.local.MealEntity
import com.example.projet_android.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealViewModel(private val repository: MealRepository) : ViewModel() {

    private val _meals = MutableStateFlow<List<MealEntity>>(emptyList())
    val meals: StateFlow<List<MealEntity>> = _meals

    private val _selectedMeal = MutableStateFlow<MealEntity?>(null)
    val selectedMeal: StateFlow<MealEntity?> = _selectedMeal

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories

    fun searchMeals(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _meals.value = if (query.isBlank())
                    repository.getAllMealsFromCache()
                else
                    repository.searchMeals(query)
            } catch (e: Exception) {
                _error.value = "Erreur : ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _meals.value = repository.getMealsByCategory(category)
            } catch (e: Exception) {
                _error.value = "Erreur : ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMealById(id: String) {
        viewModelScope.launch {
            _selectedMeal.value = repository.getMealById(id)
        }
    }

    fun loadInitialMeals() {
        searchMeals("")
    }
}