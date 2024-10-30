package com.example.restoranapplication.data.`interface`

import com.example.restoranapplication.data.RestaurantData

interface OnRestaurantSelectedListener {
    fun saveRestaurant(rest: RestaurantData)
    fun saveRestList(restList: List<RestaurantData>)
}
