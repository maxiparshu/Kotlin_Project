package com.example.restoranapplication.data

import com.google.firebase.firestore.Exclude

data class MenuItem(
    val dishName: String = "",
    val price: Double = 0.0
)

data class RestaurantData(
    @Exclude var id: String = "",
    var name: String = "",
    var menu: List<MenuItem> = emptyList(),
    var address: String = "",
    var imageURL: String = "",
    var rating: Float = 0.0F,
    var ratesAmount: Int = 0
)

fun findRestaurantById(rest: List<RestaurantData>, id: String): RestaurantData? {
    return rest.find { it.id == id }
}
