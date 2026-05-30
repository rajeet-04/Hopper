package com.example.hopper.ui.nearme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.domain.model.Pandal
import com.example.hopper.domain.usecase.GetNearestPandalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NearMeViewModel @Inject constructor(
    getNearestPandalsUseCase: GetNearestPandalsUseCase
) : ViewModel() {

    val nearestPandals: StateFlow<List<Pandal>> = getNearestPandalsUseCase(limit = 10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
