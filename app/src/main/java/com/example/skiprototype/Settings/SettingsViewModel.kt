package com.example.skiprototype.Settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

//NOTE: Not everything is implemented yet e.g. customisation options not completed, but placeholder
// storage values are here
class SettingsViewModel : ViewModel() {
    var voiceOption by mutableStateOf("Default") //not implemented yet
    var voiceAnnouncementsEnabled by mutableStateOf(true)
    var hapticStrength by mutableStateOf(50f) // 0-100
    var keepScreenOn by mutableStateOf(false)
    var temperatureSensing by mutableStateOf(true)
    var piCameraEnabled by mutableStateOf(true)
    var darkModeEnabled by mutableStateOf(false)
    var temperatureThreshold by mutableStateOf(10f) // default 10°C
        private set
    var showNotification by mutableStateOf(true)
    // Optional helper to toggle voice announcements
    fun toggleVoiceAnnouncements() {
        voiceAnnouncementsEnabled = !voiceAnnouncementsEnabled
    }
    fun toggleShowNotification() {
        showNotification = !showNotification
    }
    fun updateTemperatureThreshold(value: Float) {
        temperatureThreshold = value.coerceIn(-10f, 15f)
    }
}

class SettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}