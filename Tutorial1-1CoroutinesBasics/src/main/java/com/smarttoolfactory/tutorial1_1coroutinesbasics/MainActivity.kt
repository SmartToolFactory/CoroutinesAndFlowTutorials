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
import com.smarttoolfactory.tutorial1_1coroutinesbasics.adapter.MyAdapter
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter1_basics.Activity1Basics
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter2_scopes.Activity2CoroutineScope
import com.smarttoolfactory.tutorial1_1coroutinesbasics.model.ActivityClassModel
import java.util.*

class MainActivity : AppCompatActivity(), BaseAdapter.OnRecyclerViewItemClickListener {


    private val activityClassModels = ArrayList<ActivityClassModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "MainActivity"

        val activityMainBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // Add Activities to list on RecyclerView
        activityClassModels.add(ActivityClassModel(Activity1Basics::class.java))
        activityClassModels.add(ActivityClassModel(Activity2CoroutineScope::class.java))

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
        val adapter = MyAdapter(activityClassModels)
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
