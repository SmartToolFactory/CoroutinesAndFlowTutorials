package com.smarttoolfactory.tutorial1_1coroutinesbasics.util

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.smarttoolfactory.tutorial1_1coroutinesbasics.model.ViewState

/**
 * Display or hide a view based on a condition
 */
@BindingAdapter("visibilityBasedOn")
fun View.visibleWhen(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE

}


/**
 * Set text of [TextView] depending on success or error from network or database
 */
@BindingAdapter("postState")
fun TextView.postState(viewState: ViewState<String>?) {
    viewState?.let {
        text = if (viewState.shouldShowErrorMessage()) {
            viewState.getErrorMessage()
        } else {
            viewState.data
        }
    }
}