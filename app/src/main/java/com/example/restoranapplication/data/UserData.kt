package com.example.restoranapplication.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class ReviewData(
    val text: String = "",
    val rest: String = "",
    val name: String = "",
    val rating: Float = 0.0F,
)

data class RestaurantInfo(
    val rest: String = "",
    var name: String = "",
)

data class UserData(
    @Exclude var id: String = "",
    @PropertyName("admin") var isAdmin: Boolean = false,
    val login: String = "",
    val password: String = "",
    var reviews: List<ReviewData> = emptyList(),
    var favouriteRest: List<RestaurantInfo> = emptyList(),
)

fun findUserByLogin(users: List<UserData>, login: String): UserData? {
    return users.find { it.login == login }
}

fun findReviewByRestaurantID(reviews: List<ReviewData>, id: String): ReviewData? {
    return reviews.find { it.rest == id }
}

fun findFavouritesByRestaurantID(reviews: List<RestaurantInfo>, id: String): RestaurantInfo? {
    return reviews.find { it.rest == id }
}