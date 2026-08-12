package com.example.ultra.wishlist.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ultra.wishlist.presentation.intent.WishlistState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WishlistViewModel () : ViewModel() {
	private val _state = MutableStateFlow(WishlistState())
	val state: StateFlow<WishlistState> = _state.asStateFlow()
	}