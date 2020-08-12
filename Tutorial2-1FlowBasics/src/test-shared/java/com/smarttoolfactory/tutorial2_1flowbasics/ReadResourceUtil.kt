package com.smarttoolfactory.tutorial2_1flowbasics

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity


/**
 * Use this method to get json files as string from resources folder to use in tests.
 */
fun getResourceAsText(path: String): String {
    return object {}.javaClass.classLoader!!.getResource(path)!!.readText()
}

inline fun <reified T> Gson.fromJsonWithType(json: String): T? =
    fromJson<T>(json, object : TypeToken<T>() {}.type)

/**
 *
 * Convert from json to item with type T
 *
 * * This function returns for some items as [LinkedTreeMap]
 */
inline fun <reified T> convertToObjectFromJson(json: String): T? {
    return Gson().fromJsonWithType<T>(json)
}

/**
 *
 * Convert from json to [List] of items with type T
 *
 * * This function returns for some items as [LinkedTreeMap]
 */
inline fun <reified T> convertToObjectListFromJson(json: String): List<T> {
    return GsonBuilder().create().fromJson(json, Array<T>::class.java).asList()
}

fun <T> Gson.mapFromLinkedTreeMap(map: Map<String, Any?>?, type: Class<T>): T? {
    if (map == null) return null

    val json = toJson(map)
    return fromJson(json, type)
}

inline fun <reified T : Any> convertFromJsonToObjectList(json: String): List<T>? {

    val gson = GsonBuilder().create()

    val itemList = convertToObjectListFromJson<T>(json)

    if (itemList.first() !is LinkedTreeMap<*, *>)
        return itemList

    //Must use map here because the result is a list of LinkedTreeMaps
    val list: ArrayList<Map<String, Any?>>? = gson.fromJsonWithType(json)
    //handle type erasure

    return list?.mapNotNull {
        gson.mapFromLinkedTreeMap(it, T::class.java)
    }
}

// Non generic Post functions

fun convertToPostEntityListFromJson(json: String): List<PostEntity> {
    return GsonBuilder().create().fromJson(json, Array<PostEntity>::class.java).asList()
}