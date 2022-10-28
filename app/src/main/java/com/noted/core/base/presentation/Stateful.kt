package com.noted.core.base.presentation

import kotlinx.coroutines.flow.Flow

interface Stateful<State> {
    val state: State
    val stateFlow: Flow<State>
    fun updateState(transform: State.() -> State)
}