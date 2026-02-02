package com.parrishdev.common.udf

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel implementing Unidirectional Data Flow (UDF) pattern.
 *
 * Generic parameters:
 * - DS: DataState - Internal state containing raw data, nullable for loading states
 * - VS: ViewState - UI-ready state with formatted values
 *
 * State flows:
 * - DataState is internal and managed via [applyMutation]
 * - ViewState is exposed via [stateFlow] and derived from DataState via StateProvider
 *
 * Usage:
 * ```
 * @HiltViewModel
 * class MyViewModel @Inject constructor(
 *     stateProvider: MyStateProvider,
 *     viewModelBundle: ViewModelBundle
 * ) : BaseStateViewModel<MyDataState, MyViewState>(
 *     initialDataState = MyDataState(),
 *     stateProvider = stateProvider,
 *     viewModelBundle = viewModelBundle
 * ) {
 *     fun onUserAction() {
 *         applyMutation { copy(isLoading = true) }
 *         viewModelScope.launch {
 *             val result = repository.fetchData()
 *             applyMutation { copy(data = result, isLoading = false) }
 *         }
 *     }
 * }
 * ```
 */
abstract class BaseStateViewModel<DS : Any, VS : Any>(
    initialDataState: DS,
    private val stateProvider: StateProvider<DS, VS>,
    protected val viewModelBundle: ViewModelBundle
) : ViewModel() {

    /**
     * Channel for queuing state mutations.
     * Uses UNLIMITED capacity to prevent blocking callers.
     */
    private val mutations = Channel<DS.() -> DS>(Channel.UNLIMITED)

    /**
     * Internal mutable state flow holding the DataState.
     */
    private val _dataStateFlow = MutableStateFlow(initialDataState)

    /**
     * Exposed state flow of ViewState for UI consumption.
     * Transforms DataState through StateProvider on each emission.
     *
     * Uses [SharingStarted.Eagerly] to ensure initial state is immediately available.
     */
    val stateFlow: StateFlow<VS> = _dataStateFlow
        .map { stateProvider.reduce(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = stateProvider.reduce(initialDataState)
        )

    init {
        processMutations()
    }

    /**
     * Continuously processes queued mutations on the DataState.
     */
    private fun processMutations() {
        viewModelScope.launch(viewModelBundle.coroutineExceptionHandler) {
            mutations.consumeAsFlow().collect { mutation ->
                _dataStateFlow.update { currentState ->
                    currentState.mutation()
                }
            }
        }
    }

    /**
     * Queues a mutation to be applied to the DataState.
     *
     * Mutations are processed sequentially in order received.
     * Use copy() pattern for data class states.
     *
     * Example:
     * ```
     * applyMutation { copy(isLoading = true, error = null) }
     * ```
     */
    protected fun applyMutation(mutation: DS.() -> DS) {
        mutations.trySend(mutation)
    }

    /**
     * Executes a block with the current DataState.
     * Useful for conditional logic based on current state.
     *
     * Example:
     * ```
     * withDataState { state ->
     *     if (!state.isLoading) {
     *         fetchData()
     *     }
     * }
     * ```
     */
    protected fun withDataState(block: (DS) -> Unit) {
        block(_dataStateFlow.value)
    }

    /**
     * Returns current DataState value.
     * Prefer using [withDataState] for conditional checks to avoid race conditions.
     */
    protected val currentDataState: DS
        get() = _dataStateFlow.value

    /**
     * Lifecycle-aware observation of a Flow.
     * Collection starts when lifecycle reaches [minActiveState] and stops when below.
     *
     * This is the recommended pattern for observing store data as it:
     * - Automatically stops collection when UI is not visible (saves resources)
     * - Restarts collection when UI becomes visible again
     * - Properly handles configuration changes
     *
     * Usage:
     * ```
     * override fun onStartObserving(lifecycleOwner: LifecycleOwner) {
     *     observeWithLifecycle(lifecycleOwner, myStore.streamData()) { data ->
     *         applyMutation { copy(data = data) }
     *     }
     * }
     * ```
     *
     * @param lifecycleOwner The lifecycle owner (typically the Fragment/Activity)
     * @param flow The flow to observe
     * @param minActiveState The minimum lifecycle state for collection (default: STARTED)
     * @param collector The collector lambda that receives emitted values
     */
    protected fun <T> observeWithLifecycle(
        lifecycleOwner: LifecycleOwner,
        flow: Flow<T>,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        collector: suspend (T) -> Unit
    ) {
        viewModelScope.launch {
            lifecycleOwner.repeatOnLifecycle(minActiveState) {
                flow.collectLatest(collector)
            }
        }
    }

    /**
     * Called when the ViewModel should start observing data sources.
     * Override this to set up lifecycle-aware observations.
     *
     * This method is called from the Composable with the LifecycleOwner,
     * enabling proper lifecycle-aware collection.
     *
     * @param lifecycleOwner The lifecycle owner for lifecycle-aware collection
     */
    open fun onStartObserving(lifecycleOwner: LifecycleOwner) {
        // Override in subclasses to set up observations
    }

    /**
     * Convenience method for launching coroutines with proper error handling.
     * Use this instead of viewModelScope.launch directly for consistency.
     */
    protected fun launchWithErrorHandling(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(viewModelBundle.coroutineExceptionHandler, block = block)
    }
}
