package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.Navigation
import androidx.navigation.dynamicfeatures.fragment.DynamicNavHostFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.smarttoolfactory.tutorial2_1flowbasics.R

class Activity4SingleSourceOfTruth : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity4_single_source_of_truth)

        title = "Single Source of Truth Coroutines Flow"

        val navHostFragment: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navHostFragment.findNavController().setGraph(R.navigation.nav_graph_post_list)
    }
}