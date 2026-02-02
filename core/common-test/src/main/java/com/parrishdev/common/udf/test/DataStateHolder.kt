package com.parrishdev.common.udf.test

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Thread-safe holder for capturing DataState emissions during ViewModel tests.
 *
 * Used in conjunction with MockK to intercept all DataState transformations
 * through the StateProvider.
 *
 * Usage:
 * ```
 * private val stateHolder = DataStateHolder<MyDataState>()
 * private val stateProvider = mockk<MyStateProvider> {
 *     every { reduce(any()) } answers {
 *         val dataState = firstArg<MyDataState>()
 *         stateHolder.capture(dataState)
 *         MyStateProvider().reduce(dataState)
 *     }
 * }
 *
 * @Test
 * fun `verify loading state`() = runTest {
 *     viewModel.loadData()
 *     advanceUntilIdle()
 *
 *     val states = stateHolder.all()
 *     assertTrue(states[0].isLoading)
 *     assertFalse(states[1].isLoading)
 * }
 * ```
 */
class DataStateHolder<DS : Any> {
    private val states = ConcurrentLinkedQueue<DS>()
    private var currentIndex = 0

    /**
     * Called by MockK capture to record a DataState.
     * @return the captured state for pass-through
     */
    fun capture(state: DS): DS {
        states.add(state)
        return state
    }

    /**
     * Returns the most recently captured DataState.
     * @throws NoSuchElementException if no states have been captured
     */
    fun latest(): DS = states.lastOrNull()
        ?: throw NoSuchElementException("No DataStates have been captured yet")

    /**
     * Returns the first captured DataState.
     * @throws NoSuchElementException if no states have been captured
     */
    fun first(): DS = states.firstOrNull()
        ?: throw NoSuchElementException("No DataStates have been captured yet")

    /**
     * Returns all captured DataStates in order.
     */
    fun all(): List<DS> = states.toList()

    /**
     * Returns the next DataState after the current index and advances the index.
     * Useful for iterating through state changes sequentially.
     *
     * @throws NoSuchElementException if no more states are available
     */
    fun next(): DS {
        val stateList = states.toList()
        if (currentIndex >= stateList.size) {
            throw NoSuchElementException(
                "No more states available. Current index: $currentIndex, Total states: ${stateList.size}"
            )
        }
        return stateList[currentIndex++]
    }

    /**
     * Returns the previous DataState before the latest.
     * @throws NoSuchElementException if fewer than 2 states have been captured
     */
    fun previous(): DS {
        val stateList = states.toList()
        if (stateList.size < 2) {
            throw NoSuchElementException(
                "No previous state available. Total states: ${stateList.size}"
            )
        }
        return stateList[stateList.size - 2]
    }

    /**
     * Returns the DataState at the specified index.
     * @throws IndexOutOfBoundsException if index is invalid
     */
    fun at(index: Int): DS = states.toList().getOrNull(index)
        ?: throw IndexOutOfBoundsException(
            "Index $index is out of bounds. Total states: ${states.size}"
        )

    /**
     * Resets the sequential iteration index without clearing states.
     */
    fun resetIndex() {
        currentIndex = 0
    }

    /**
     * Clears all captured states and resets the index.
     */
    fun clear() {
        states.clear()
        currentIndex = 0
    }

    /**
     * Returns the number of captured states.
     */
    fun size(): Int = states.size

    /**
     * Returns true if no states have been captured.
     */
    fun isEmpty(): Boolean = states.isEmpty()
}
