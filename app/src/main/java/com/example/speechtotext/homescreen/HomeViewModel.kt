package com.example.speechtotext.homescreen

import com.example.speechtotext.base.SimpleViewModel

class HomeViewModel : SimpleViewModel<HomeViewModel.UiState, Nothing, HomeViewModel.UiEvent>(UiState()) {

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.DisplaySpeechToTextDialog -> updateState { it.copy(shouldDisplaySpeechToTextDialog = true) }
            is UiEvent.DismissSpeechToTextDialog -> updateState { it.copy(shouldDisplaySpeechToTextDialog = false) }
            is UiEvent.DisplayPermissionDialog -> updateState { it.copy(shouldDisplayPermissionDialog = true) }
            is UiEvent.DismissPermissionDialog -> updateState { it.copy(shouldDisplayPermissionDialog = false) }
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
