package com.smarttoolfactory.tutorial1_1coroutinesbasics.retrofitexample


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UsersResponse(
    @SerializedName("data") val users: List<User>,
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("total_pages") val totalPages: Int
) {
    @Keep
    data class User(
        @SerializedName("avatar") val avatar: String,
        @SerializedName("email") val email: String,
        @SerializedName("first_name") val firstName: String,
        @SerializedName("id") val id: Int,
        @SerializedName("last_name") val lastName: String
    )
}