package com.example.speechtotext.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<State, Action, Event> : ViewModel() {

    abstract val state: StateFlow<State>
    private val _actions = MutableSharedFlow<Action>()
    val actions: SharedFlow<Action> = _actions.asSharedFlow()

    protected fun emitAction(action: Action) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }

    abstract fun handleEvent(event: Event)
}

abstract class SimpleViewModel<State, Action, Event>(
    initialState: State,
) : BaseViewModel<State, Action, Event>() {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<State> = _state.asStateFlow()

    protected fun updateState(update: (State) -> State) {
        _state.update(update)
    }
}

@Composable
fun <State, Action, Event> BaseViewModel<State, Action, Event>.observe(
    handleAction: (Action) -> Unit,
): State {
    val collectedState by state.collectAsStateWithLifecycle()

    LaunchedEffect(actions) {
        actions.collect {
            handleAction(it)
        }
    }

    return collectedState
}

@Composable
fun <State, Action, Event> BaseViewModel<State, Action, Event>.observeWithoutActions(): State {
    val collectedState by state.collectAsStateWithLifecycle()
    return collectedState
}
