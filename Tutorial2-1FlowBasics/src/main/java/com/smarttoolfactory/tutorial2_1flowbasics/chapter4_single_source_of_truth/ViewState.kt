package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth


class ViewState<T>(
    private val status: Status,
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