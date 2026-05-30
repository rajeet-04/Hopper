package com.example.hopper.ui.exit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.domain.model.ExitRoute
import com.example.hopper.domain.usecase.GetExitRoutesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel backing the [ExitRouterSheet]. Loads exit routes either from a
 * specific pandal or from the user's current location (nearest exits).
 */
@HiltViewModel
class ExitRouterViewModel @Inject constructor(
    private val getExitRoutesUseCase: GetExitRoutesUseCase
) : ViewModel() {

    private val _routes = MutableStateFlow<List<ExitRoute>>(emptyList())
    val routes: StateFlow<List<ExitRoute>> = _routes.asStateFlow()

    /**
     * Loads exit routes originating from the given pandal.
     */
    fun loadRoutes(pandalId: String, nightSafetyMode: Boolean) {
        viewModelScope.launch {
            _routes.value = getExitRoutesUseCase.getRoutesFromPandal(pandalId, nightSafetyMode)
        }
    }

    /**
     * Loads the nearest exit per category relative to the user's current location.
     */
    fun loadNearestExits() {
        viewModelScope.launch {
            _routes.value = getExitRoutesUseCase.getNearestExits()
        }
    }
}
