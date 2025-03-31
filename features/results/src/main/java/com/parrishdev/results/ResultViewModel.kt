package com.parrishdev.results

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ResultViewModel @Inject constructor(): ViewModel() {
    private val _viewState: MutableStateFlow<ResultViewState> = MutableStateFlow(ResultViewState())
    val viewState: StateFlow<ResultViewState> = _viewState.asStateFlow()
}