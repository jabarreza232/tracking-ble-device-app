package id.co.evolution.trackingdevice.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.evolution.trackingdevice.data.preference.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val userPreferences: UserPreferences): ViewModel(){
    val isOnboardingCompleted = userPreferences.isOnboardingCompleted.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun finishOnboarding(){
        viewModelScope.launch {
            userPreferences.saveOnboardingCompleted(true)
        }
    }
}