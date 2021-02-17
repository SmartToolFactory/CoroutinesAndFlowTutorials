package com.smarttoolfactory.tutorial2_1flowbasics.data.model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*


class ViewState<T>(
    val status: Status,
    val data: T? = null,
    val error: Throwable? = null
) {

    fun isSuccess() = status == Status.SUCCESS

    fun isLoading() = status == Status.LOADING

    fun getErrorMessage() = error?.message

    fun shouldShowErrorMessage() = error != null && status == Status.ERROR
}

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}

fun <T> Flow<T>.convertToFlowViewState(dispatcher: CoroutineDispatcher): Flow<ViewState<T>> {

    return this
        .map { postList -> ViewState(status = Status.SUCCESS, data = postList) }
        .catch { cause: Throwable -> emitAll(flowOf(ViewState(Status.ERROR, error = cause))) }
        .flowOn(dispatcher)
}