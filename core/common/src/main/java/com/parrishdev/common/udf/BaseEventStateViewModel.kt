package com.parrishdev.common.udf

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Extension of [BaseStateViewModel] with one-time event support.
 *
 * Events are used for:
 * - Navigation actions
 * - Showing snackbars/toasts
 * - Triggering dialogs
 * - Any one-time UI action that shouldn't persist in state
 *
 * Generic parameters:
 * - DS: DataState - Internal state
 * - VS: ViewState - UI-ready state
 * - E: Event type (typically a sealed interface)
 *
 * Usage:
 * ```
 * sealed interface MyEvent {
 *     data class NavigateToDetail(val id: String) : MyEvent
 *     data class ShowError(val message: String) : MyEvent
 * }
 *
 * @HiltViewModel
 * class MyViewModel @Inject constructor(
 *     stateProvider: MyStateProvider,
 *     viewModelBundle: ViewModelBundle
 * ) : BaseEventStateViewModel<MyDataState, MyViewState, MyEvent>(
 *     initialDataState = MyDataState(),
 *     stateProvider = stateProvider,
 *     viewModelBundle = viewModelBundle
 * ) {
 *     fun onItemClicked(id: String) {
 *         submitEvent(MyEvent.NavigateToDetail(id))
 *     }
 * }
 *
 * // In Composable:
 * LaunchedEffect(Unit) {
 *     viewModel.eventFlow.collect { event ->
 *         when (event) {
 *             is MyEvent.NavigateToDetail -> navController.navigate(...)
 *             is MyEvent.ShowError -> showSnackbar(event.message)
 *         }
 *     }
 * }
 * ```
 */
abstract class BaseEventStateViewModel<DS : Any, VS : Any, E : Any>(
    initialDataState: DS,
    stateProvider: StateProvider<DS, VS>,
    viewModelBundle: ViewModelBundle
) : BaseStateViewModel<DS, VS>(initialDataState, stateProvider, viewModelBundle) {

    /**
     * Channel for events.
     * Uses BUFFERED to keep only the most recent unhandled event,
     * preventing event buildup if UI is slow to consume.
     */
    private val _eventChannel = Channel<E>(Channel.BUFFERED)

    /**
     * Flow of one-time events for UI consumption.
     * Each event is delivered exactly once (channel semantics).
     *
     * Collect this in a LaunchedEffect or similar lifecycle-aware collector:
     * ```
     * LaunchedEffect(Unit) {
     *     viewModel.eventFlow.collect { event ->
     *         // Handle event
     *     }
     * }
     * ```
     */
    val eventFlow: Flow<E> = _eventChannel.receiveAsFlow()

    /**
     * Submits a one-time event to be consumed by the UI.
     *
     * Events are not persisted in state and are delivered exactly once.
     * Use for navigation, toasts, dialogs, etc.
     *
     * Example:
     * ```
     * submitEvent(MyEvent.ShowError("Failed to load"))
     * ```
     */
    protected fun submitEvent(event: E) {
        _eventChannel.trySend(event)
    }
}
