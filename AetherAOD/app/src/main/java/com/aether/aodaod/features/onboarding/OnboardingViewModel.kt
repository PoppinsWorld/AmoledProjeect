package com.aether.aodaod.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.aodaod.domain.repository.AODSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Onboarding screen
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: AODSettingsRepository
) : ViewModel() {
    
    val isOnboardingCompleted: Flow<Boolean> = repository.isOnboardingCompleted()
    
    fun setOnboardingCompleted(completed: Boolean) {
        viewModelScope.launch {
            repository.setOnboardingCompleted(completed)
        }
    }
}
