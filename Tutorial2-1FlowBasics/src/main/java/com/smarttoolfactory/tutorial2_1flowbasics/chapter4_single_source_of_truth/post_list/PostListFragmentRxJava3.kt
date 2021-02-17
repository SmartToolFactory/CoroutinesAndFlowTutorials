package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.post_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.smarttoolfactory.tutorial2_1flowbasics.R
import com.smarttoolfactory.tutorial2_1flowbasics.adapter.PostListAdapter
import com.smarttoolfactory.tutorial2_1flowbasics.databinding.FragmentPostListBinding

class PostListFragmentRxJava3 : Fragment() {

    lateinit var dataBinding: FragmentPostListBinding

    private val viewModel by activityViewModels<PostViewModelRxJava3> {
        PostViewModelFactoryRxJava3(application = requireActivity().application)
    }

    private lateinit var postListAdapter: PostListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_post_list, container, false)
        bindViews()
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPosts()
    }

    private fun bindViews() {

        val binding = dataBinding!!


        // 🔥 Set lifecycle for data binding
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        binding.recyclerView.apply {

            // Set Layout manager
            this.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

            postListAdapter = PostListAdapter(
                R.layout.row_post,
                viewModel::onClick
            )

            // Set RecyclerViewAdapter
            this.adapter = postListAdapter
        }

        val swipeRefreshLayout = binding.swipeRefreshLayout

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            viewModel.refreshPosts()
        }

        subscribeGoToDetailScreen()
    }

    private fun subscribeGoToDetailScreen() {

        viewModel.goToDetailScreen.observe(viewLifecycleOwner, Observer {

            it.getContentIfNotHandled()?.let { post ->
                val bundle = bundleOf("post" to post)
                findNavController().navigate(
                    R.id.nav_graph_post_detail,
                    bundle
                )
            }
        })
    }
}