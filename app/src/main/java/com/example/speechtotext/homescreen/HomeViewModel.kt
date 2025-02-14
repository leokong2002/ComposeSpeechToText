package com.example.speechtotext.homescreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.DisplaySpeechToTextDialog -> _state.update { it.copy(shouldDisplaySpeechToTextDialog = true) }
            is UiEvent.DismissSpeechToTextDialog -> _state.update { it.copy(shouldDisplaySpeechToTextDialog = false) }
            is UiEvent.DisplayPermissionDialog -> _state.update { it.copy(shouldDisplayPermissionDialog = true) }
            is UiEvent.DismissPermissionDialog -> _state.update { it.copy(shouldDisplayPermissionDialog = false) }
        }
    }

    data class UiState(
        val shouldDisplaySpeechToTextDialog: Boolean = false,
        val shouldDisplayPermissionDialog: Boolean = false,
    )

    sealed interface UiEvent {
        data object DisplaySpeechToTextDialog : UiEvent
        data object DismissSpeechToTextDialog : UiEvent
        data object DisplayPermissionDialog : UiEvent
        data object DismissPermissionDialog : UiEvent
    }
}
