package com.parrishdev.common.udf

/**
 * Interface for transforming internal DataState to UI-ready ViewState.
 *
 * Implementations should be pure functions with no side effects.
 * Can be used as inline lambda for simple cases or as injectable class
 * for complex transformations needing Resources, formatters, etc.
 *
 * Example:
 * ```
 * class MyStateProvider @Inject constructor(
 *     private val resources: Resources
 * ) : StateProvider<MyDataState, MyViewState> {
 *     override fun reduce(dataState: MyDataState) = MyViewState(
 *         displayText = dataState.text ?: resources.getString(R.string.loading),
 *         isLoading = dataState.isLoading
 *     )
 * }
 * ```
 */
fun interface StateProvider<in DS : Any, out VS : Any> {
    fun reduce(dataState: DS): VS
}
