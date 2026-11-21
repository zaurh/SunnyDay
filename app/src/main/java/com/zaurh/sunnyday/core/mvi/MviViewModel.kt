package com.zaurh.sunnyday.core.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/** Immutable snapshot of everything a screen renders. */
interface UiState

/** A user action (or lifecycle event) sent from the UI to the ViewModel. */
interface UiIntent

/** A one-shot event (navigation, snackbar) that must be consumed exactly once. */
interface UiEffect

/**
 * Minimal MVI base:
 *  UI --(intent)--> [onIntent] --(reduce)--> [state] --> UI
 *                              \--(emit)--> [effects] --> UI
 *
 * The UI never mutates state directly; it can only dispatch intents.
 */
abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect>(
    initialState: S,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects: Flow<E> = _effects.receiveAsFlow()

    protected val currentState: S get() = _state.value

    /** Single entry point for every user action. */
    abstract fun onIntent(intent: I)

    protected fun setState(reducer: S.() -> S) = _state.update(reducer)

    protected fun sendEffect(effect: E) {
        _effects.trySend(effect)
    }
}
