package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth

import kotlinx.coroutines.CoroutineDispatcher

data class DispatcherProvider(
    val ioDispatcher: CoroutineDispatcher,
    val defaultDispatcher: CoroutineDispatcher
)