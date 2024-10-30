package com.example.restoranapplication.data

import com.google.firebase.firestore.Exclude
import java.time.temporal.TemporalAmount

data class MenuItem(
    val dishName: String = "",
    val price: Double = 0.0
)

data class RestaurantData(
    @Exclude var id: String = "",
    var name: String = "",
    var menu: List<MenuItem> = emptyList(),
    var imageURL: String = "",
    var rating: Float = 0.0F,
    var ratesAmount: Int = 0
)

fun findRestaurantByName(rest: List<RestaurantData>, name: String): RestaurantData? {
    return rest.find { it.name == name }
}
