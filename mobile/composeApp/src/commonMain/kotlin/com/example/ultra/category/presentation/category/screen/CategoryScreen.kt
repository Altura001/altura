package com.example.ultra.category.presentation.category.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ultra.category.presentation.intent.CategoryState
import com.example.ultra.category.viewmodel.CategoryViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CategoryScreenRoot(viewModel: CategoryViewModel = koinViewModel()) {

val state by viewModel.state.collectAsStateWithLifecycle()

CategoryScreen(state = state)
}

@Composable
fun CategoryScreen(state: CategoryState, modifier: Modifier = Modifier) {

}