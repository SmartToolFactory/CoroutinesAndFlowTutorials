package com.smarttoolfactory.tutorial1_1coroutinesbasics

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.databinding.ActivityMainBinding
import com.smarttoolfactory.tutorial1_1coroutinesbasics.adapter.BaseAdapter
import com.smarttoolfactory.tutorial1_1coroutinesbasics.adapter.ChapterSelectionAdapter
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter1_basics.Activity1Basics
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter2_scopes.Activity2CoroutineScope1
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter2_scopes.Activity2CoroutineScope2
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter2_scopes.Activity2CoroutineScope3
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter3_lifecycle.Activity3CoroutineLifecycle
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter4_supervisorjob.Activity4SupervisorJob
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter5_viewmodel.Activity5ViewModelRxJava
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter5_viewmodel.Activity5ViewModelScope
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.Activity6Network
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.Activity7Database
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter8_single_source_of_truth.Activity8SingleSourceOfTruth
import com.smarttoolfactory.tutorial1_1coroutinesbasics.model.ActivityClassModel
import java.util.*

class MainActivity : AppCompatActivity(), BaseAdapter.OnRecyclerViewItemClickListener {


    private val activityClassModels = ArrayList<ActivityClassModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "MainActivity"

        val activityMainBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // Add Activities to list to be displayed on RecyclerView
        activityClassModels.add(ActivityClassModel(Activity1Basics::class.java))
        activityClassModels.add(ActivityClassModel(Activity2CoroutineScope1::class.java))
        activityClassModels.add(ActivityClassModel(Activity2CoroutineScope2::class.java))
        activityClassModels.add(ActivityClassModel(Activity2CoroutineScope3::class.java))
        activityClassModels.add(ActivityClassModel(Activity3CoroutineLifecycle::class.java))
        activityClassModels.add(ActivityClassModel(Activity4SupervisorJob::class.java))
        activityClassModels.add(ActivityClassModel(Activity5ViewModelScope::class.java))
        activityClassModels.add(ActivityClassModel(Activity5ViewModelRxJava::class.java))
        activityClassModels.add(ActivityClassModel(Activity6Network::class.java))
        activityClassModels.add(ActivityClassModel(Activity7Database::class.java))
        activityClassModels.add(ActivityClassModel(Activity8SingleSourceOfTruth::class.java))

        val recyclerView = activityMainBinding.recyclerView

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true)

        // use a linear layout manager
        LinearLayoutManager(this).also {
            recyclerView.layoutManager = it
        }


        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        // define an adapter
        val adapter = ChapterSelectionAdapter(activityClassModels)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(this)


    }

    @Override
    override fun onItemClicked(view: View, position: Int) {

        Intent(this@MainActivity, activityClassModels[position].clazz).also {
            startActivity(it)
        }

    }
}
