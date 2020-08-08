package com.smarttoolfactory.tutorial2_1flowbasics.chapter3_database.mock

import android.app.Application
import android.content.res.AssetManager
import com.google.gson.GsonBuilder
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDao
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post


class MockProductFactory constructor(
    private val postDao: PostDao,
    private val application: Application
) {


    fun generateMockList(): List<Post> {
        val response = application.assets.readAssetsFile("mock/response.json")
        return GsonBuilder().create().fromJson(response, Array<Post>::class.java).asList()
    }


    companion object {
        const val KEY_FIRST_START = "FIRST_START"
    }


    private fun AssetManager.readAssetsFile(fileName: String): String =
        open(fileName).bufferedReader().use { it.readText() }

}

