package com.noted.core.base.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class StatefulViewModel<State>(
    initialState: State,
) : ViewModel(),
    Stateful<State> {
    private val _stateFlow by lazy { MutableStateFlow(initialState) }

    override val state: State
        get() = _stateFlow.value

    override val stateFlow: Flow<State>
        get() = _stateFlow.asStateFlow()

    override fun updateState(transform: State.() -> State) {
        _stateFlow.value = transform(state)
    }
}