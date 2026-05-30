package com.example.hopper.ui.volunteer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hopper.data.local.db.entity.VolunteerPostEntity
import com.example.hopper.domain.usecase.GetVolunteerPostsUseCase
import com.example.hopper.domain.usecase.SignUpForVolunteerShiftUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Volunteer screen.
 *
 * Exposes the festival-scoped list of volunteer posts and supports signing up
 * for a shift. Contact details are never displayed publicly.
 */
@HiltViewModel
class VolunteerViewModel @Inject constructor(
    getVolunteerPostsUseCase: GetVolunteerPostsUseCase,
    private val signUpForVolunteerShiftUseCase: SignUpForVolunteerShiftUseCase
) : ViewModel() {

    val posts: StateFlow<List<VolunteerPostEntity>> = getVolunteerPostsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Signs the user up for a volunteer shift.
     *
     * The phone number is passed through as-is for the MVP.
     * TODO: encrypt the contact before persisting so it is never stored in plain text.
     */
    fun signUp(postId: String, name: String, phone: String) {
        // TODO: replace the trivial pass-through with real encryption of the contact.
        val encryptedContact = phone
        viewModelScope.launch {
            signUpForVolunteerShiftUseCase(postId, name, encryptedContact)
        }
    }
}
