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

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline

    // Pagination
    private var allMeals = listOf<MealEntity>()
    private var currentPage = 0
    private val pageSize = 30

    fun searchMeals(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _isOffline.value = !repository.isNetworkAvailable()
            currentPage = 0
            try {
                allMeals = if (query.isBlank())
                    repository.getAllMealsFromCache()
                else
                    repository.searchMeals(query)
                _meals.value = allMeals.take(pageSize)
            } catch (e: Exception) {
                _error.value = "Erreur réseau"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _isOffline.value = !repository.isNetworkAvailable()
            currentPage = 0
            try {
                allMeals = repository.getMealsByCategory(category)
                _meals.value = allMeals.take(pageSize)
            } catch (e: Exception) {
                _error.value = "Erreur réseau"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextPage() {
        val nextPage = currentPage + 1
        val nextItems = allMeals.take(pageSize * (nextPage + 1))
        if (nextItems.size > _meals.value.size) {
            currentPage = nextPage
            _meals.value = nextItems
        }
    }

    fun getMealById(id: String) {
        viewModelScope.launch {
            _selectedMeal.value = repository.getMealById(id)
        }
    }
}