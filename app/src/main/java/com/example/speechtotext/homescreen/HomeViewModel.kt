package com.example.speechtotext.homescreen

import com.example.speechtotext.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : BaseViewModel<HomeViewModel.UiState, Nothing, HomeViewModel.UiEvent>() {

    private val _state = MutableStateFlow(UiState())
    override val state: StateFlow<UiState> = _state.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.DisplaySpeechToTextDialog -> updateState { it.copy(shouldDisplaySpeechToTextDialog = true) }
            is UiEvent.DismissSpeechToTextDialog -> updateState { it.copy(shouldDisplaySpeechToTextDialog = false) }
            is UiEvent.DisplayPermissionDialog -> updateState { it.copy(shouldDisplayPermissionDialog = true) }
            is UiEvent.DismissPermissionDialog -> updateState { it.copy(shouldDisplayPermissionDialog = false) }
        }
    }

    private fun updateState(update: (UiState) -> UiState) {
        _state.update(update)
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
