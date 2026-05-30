package com.example.hopper.domain.usecase

import com.example.hopper.domain.FestivalToggleController
import com.example.hopper.domain.model.Festival
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToggleFestivalUseCase @Inject constructor(
    private val festivalToggleController: FestivalToggleController
) {
    suspend operator fun invoke(festival: Festival, year: Int) {
        festivalToggleController.setFestival(festival, year)
    }
}
