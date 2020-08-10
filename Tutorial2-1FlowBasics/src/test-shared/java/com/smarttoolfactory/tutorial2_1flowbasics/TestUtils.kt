package com.smarttoolfactory.tutorial2_1flowbasics

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity


/**
 * Use this method to get json files as string from resources folder to use in tests.
 */
fun getResourceAsText(path: String): String {
    return object {}.javaClass.classLoader!!.getResource(path)!!.readText()
}

inline fun <reified T> convertToObjectFromJson(json: String): T? {
    return Gson().fromJsonWithType<T>(json)
}

inline fun <reified T> Gson.fromJsonWithType(json: String): T? =
    fromJson<T>(json, object : TypeToken<T>() {}.type)

inline fun <reified T> convertToObjectListFromJson(json: String): List<T> {
    return GsonBuilder().create().fromJson(json, Array<T>::class.java).asList()
}


 fun convertToPostListFromJson(json: String): List<Post> {
    return GsonBuilder().create().fromJson(json, Array<Post>::class.java).asList()
}

fun convertToPostEntityListFromJson(json: String): List<PostEntity> {
    return GsonBuilder().create().fromJson(json, Array<PostEntity>::class.java).asList()
}