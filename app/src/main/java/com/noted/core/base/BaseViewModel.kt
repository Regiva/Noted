package com.noted.core.base

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T>(
    state: T,
) : ViewModel() {

    protected val _state = mutableStateOf(state)
    val state: State<T> = _state

    fun updateState(update: T.() -> Unit) {
        update.invoke(_state.value)
    }
}