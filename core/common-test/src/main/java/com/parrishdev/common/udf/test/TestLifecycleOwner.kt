package com.parrishdev.common.udf.test

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * A test-friendly [LifecycleOwner] that can be used in ViewModel tests.
 *
 * By default, creates with STARTED state so that lifecycle-aware collections
 * will begin immediately.
 *
 * Usage:
 * ```
 * private val testLifecycleOwner = TestLifecycleOwner()
 *
 * @Test
 * fun `test data observation`() = runTest {
 *     viewModel = createViewModel()
 *     viewModel.onStartObserving(testLifecycleOwner)
 *     advanceUntilIdle()
 *     // verify state...
 * }
 * ```
 */
class TestLifecycleOwner(
    initialState: Lifecycle.State = Lifecycle.State.STARTED
) : LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry.createUnsafe(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    init {
        lifecycleRegistry.currentState = initialState
    }

    /**
     * Move the lifecycle to a specific state.
     * Use this to test lifecycle-dependent behavior.
     */
    fun setState(state: Lifecycle.State) {
        lifecycleRegistry.currentState = state
    }

    /**
     * Move lifecycle to STARTED state.
     * Collections will begin when lifecycle is at least STARTED.
     */
    fun start() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    /**
     * Move lifecycle to RESUMED state.
     */
    fun resume() {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    /**
     * Move lifecycle to CREATED state.
     * Collections will pause when lifecycle goes below STARTED.
     */
    fun stop() {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    /**
     * Move lifecycle to DESTROYED state.
     * Collections will be cancelled.
     */
    fun destroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }
}
