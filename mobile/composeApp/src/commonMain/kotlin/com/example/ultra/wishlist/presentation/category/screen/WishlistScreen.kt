package com.example.ultra.wishlist.presentation.category.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ultra.wishlist.presentation.intent.WishlistState
import com.example.ultra.wishlist.viewmodel.WishlistViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WishlistScreenRoot(viewModel: WishlistViewModel = koinViewModel()) {
val state by viewModel.state.collectAsStateWithLifecycle()
	WishlistScreen(state = state)
}

@Composable
fun WishlistScreen(state: WishlistState, modifier: Modifier = Modifier) {

}