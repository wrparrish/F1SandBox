package com.parrishdev.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SharedViewModel : ViewModel() {
    private val _currentTitle = MutableStateFlow("F1 Sandbox")
    val currentTitle: StateFlow<String> = _currentTitle.asStateFlow()

    fun updateTitle(newTitle: String) {
        _currentTitle.update {
            newTitle
        }
    }
}