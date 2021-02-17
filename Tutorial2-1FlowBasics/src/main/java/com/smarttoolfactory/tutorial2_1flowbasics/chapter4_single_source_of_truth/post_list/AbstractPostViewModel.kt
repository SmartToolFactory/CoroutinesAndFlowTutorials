package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.post_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.ViewState
import com.smarttoolfactory.tutorial2_1flowbasics.util.Event

abstract class AbstractPostViewModel : ViewModel() {

    internal val _goToDetailScreen = MutableLiveData<Event<Post>>()
    val goToDetailScreen: LiveData<Event<Post>>
        get() = _goToDetailScreen

    internal val _postViewState = MutableLiveData<ViewState<List<Post>>>()
    val postViewState: LiveData<ViewState<List<Post>>>
        get() = _postViewState

    abstract fun getPosts()

    abstract fun refreshPosts()

    abstract fun onClick(post: Post)
}