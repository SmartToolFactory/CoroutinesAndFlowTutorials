package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api

import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.Status.*


sealed class DataResult<T>(
    val status: Status,
    val data: T? = null,
    val error: Throwable? = null
) {

    class Loading<T> : DataResult<T>(LOADING)
    class Success<T>(data: T) : DataResult<T>(SUCCESS, data = data)
    class Error<T>(error: Throwable) : DataResult<T>(ERROR, error = error)

}


enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}