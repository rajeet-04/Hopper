package com.example.hopper.ui.pandal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.domain.model.Pandal
import com.example.hopper.domain.repository.PandalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PandalDetailViewModel @Inject constructor(
    private val pandalRepository: PandalRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _pandal = MutableStateFlow<Pandal?>(null)
    val pandal: StateFlow<Pandal?> = _pandal.asStateFlow()

    fun loadPandal(pandalId: String) {
        viewModelScope.launch {
            _pandal.value = pandalRepository.getPandalById(pandalId)
        }
    }
}
