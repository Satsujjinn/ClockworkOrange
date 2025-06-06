package com.legendai.musichelper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Factory creating MusicViewModel with dependencies from ServiceLocator
object MusicViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicViewModel(
                ServiceLocator.repository,
                ServiceLocator.prefsRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
