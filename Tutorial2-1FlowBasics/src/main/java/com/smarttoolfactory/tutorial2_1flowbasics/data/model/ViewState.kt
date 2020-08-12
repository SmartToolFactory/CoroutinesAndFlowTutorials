package com.smarttoolfactory.tutorial2_1flowbasics.data.model


class ViewState<T>(
    val status: Status,
    val data: T? = null,
    val error: Throwable? = null
) {
    fun isLoading() = status == Status.LOADING

    fun getErrorMessage() = error?.message

    fun shouldShowErrorMessage() = error != null
}

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}