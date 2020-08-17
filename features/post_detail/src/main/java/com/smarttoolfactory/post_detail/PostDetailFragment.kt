package com.smarttoolfactory.post_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.smarttoolfactory.post_detail.databinding.FragmentPostDetailBinding
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post


class PostDetailFragment : Fragment() {


    private lateinit var dataBinding: FragmentPostDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_post_detail, container, false)
        return dataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get RepoListItem from navigation component arguments
        val post = arguments?.get("post") as? Post?

        dataBinding?.item = post
    }
}