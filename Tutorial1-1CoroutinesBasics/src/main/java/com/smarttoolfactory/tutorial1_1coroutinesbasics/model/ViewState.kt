package com.smarttoolfactory.tutorial1_1coroutinesbasics.model

import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.Status


class ViewState<T>(
    private val status: Status,
    val data: T? = null,
    val error: Throwable? = null
) {
    fun isLoading() = status == Status.LOADING

    fun getErrorMessage() = error?.message

    fun shouldShowErrorMessage() = error != null
}