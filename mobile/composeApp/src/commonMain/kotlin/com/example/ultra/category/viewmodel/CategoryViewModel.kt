package com.example.ultra.category.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ultra.category.presentation.intent.CategoryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryViewModel : ViewModel() {
	private val _state = MutableStateFlow(CategoryState())
	val state: StateFlow<CategoryState> = _state.asStateFlow()
}