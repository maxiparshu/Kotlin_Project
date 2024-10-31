package com.example.restoranapplication.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

fun addUser(user: UserData) {
    val db = Firebase.firestore
    db.collection("users").add(user)
        .addOnSuccessListener { documentReference ->
            user.id = documentReference.id
            Log.d("Firestore", "User added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error adding user", e)
        }
}

suspend fun getAllUsers(): List<UserData> {
    val db = Firebase.firestore
    val users = mutableListOf<UserData>()
    return try {
        val result = db.collection("users").get().await()
        result.forEach { document ->
            val user = document.toObject(UserData::class.java).apply {
                id = document.id
            }
            Log.w("Firestore", "With id ${user.id}")
            users.add(user)
        }
        users
    } catch (e: Exception) {
        emptyList()
    }
}
fun updateUsers(userId: String, updatedData: UserData) {
    val db = Firebase.firestore
    db.collection("users")
        .document(userId)
        .set(updatedData, SetOptions.merge())
        .addOnSuccessListener {
            Log.w("Firestore", "Users updated successfully.")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error updating users.", e)
        }
}
fun addRestaurant(restaurant: RestaurantData): RestaurantData {
    val db = Firebase.firestore
    db.collection("restaurants").add(restaurant)
        .addOnSuccessListener { documentReference ->
            restaurant.id = documentReference.id
            Log.w("Firestore", "Restaurant added with ID: ${restaurant.id}")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error adding restaurant", e)
        }
    return restaurant
}


suspend fun getAllRestaurants(): List<RestaurantData> {
    val db = Firebase.firestore
    val restaurants = mutableListOf<RestaurantData>()
    return try {
        val result = db.collection("restaurants").get().await()
        result.forEach { document ->
            val restaurant = document.toObject(RestaurantData::class.java).apply {
                id = document.id
            }
            Log.w("Firestore", "Restaurant with id ${restaurant.id}")
            restaurants.add(restaurant)
        }
        restaurants
    } catch (e: Exception) {
        emptyList()
    }
}

fun updateRestaurant(restaurantId: String, updatedData: RestaurantData) {
    val db = Firebase.firestore
    db.collection("restaurants")
        .document(restaurantId)
        .set(updatedData, SetOptions.merge())
        .addOnSuccessListener {
            Log.d("Firestore", "Restaurant updated successfully.")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error updating restaurant.", e)
        }
}

// Удаление ресторана
fun deleteRestaurant(restaurantId: String) {
    val db = Firebase.firestore
    db.collection("restaurants").document(restaurantId).delete()
        .addOnSuccessListener {
            Log.d("Firestore", "Restaurant successfully deleted!")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error deleting restaurant", e)
        }
}