package com.smarttoolfactory.tutorial2_1flowbasics

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.smarttoolfactory.tutorial2_1flowbasics.adapter.BaseAdapter
import com.smarttoolfactory.tutorial2_1flowbasics.adapter.ChapterSelectionAdapter
import com.smarttoolfactory.tutorial2_1flowbasics.chapter2_network.Activity2Network
import com.smarttoolfactory.tutorial2_1flowbasics.chapter3_database.Activity3Database
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.Activity4SingleSourceOfTruth
import com.smarttoolfactory.tutorial2_1flowbasics.databinding.ActivityMainBinding
import com.smarttoolfactory.tutorial2_1flowbasics.model.ActivityClassModel
import java.util.*

class MainActivity : AppCompatActivity(), BaseAdapter.OnRecyclerViewItemClickListener {


    private val activityClassModels = ArrayList<ActivityClassModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "MainActivity"

        val activityMainBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // Add Activities to list to be displayed on RecyclerView
        activityClassModels.add(ActivityClassModel(Activity2Network::class.java))
        activityClassModels.add(ActivityClassModel(Activity3Database::class.java))
        activityClassModels.add(ActivityClassModel(Activity4SingleSourceOfTruth::class.java))

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