package com.example.restoranapplication.data

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

fun addUser(user: UserData) {
    val db = Firebase.firestore
    db.collection("users").add(user)
        .addOnSuccessListener { documentReference ->
            Log.d("Firestore", "User added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error adding user", e)
        }
}

suspend fun getAllUsers(): List<UserData> {
    val db = Firebase.firestore
    return try {
        val result = db.collection("users").get().await()
        result.map { document ->
            document.toObject(UserData::class.java).apply {
                id = document.id // Устанавливаем id документа
            }
        }
    } catch (e: Exception) {
        emptyList() // Возвращаем пустой список в случае ошибки
    }
}
fun updateUsers(userId: String, updatedData: UserData) {
    val db = Firebase.firestore
    db.collection("users")
        .document(userId)
        .set(updatedData, SetOptions.merge())
        .addOnSuccessListener {
            Log.d(TAG, "Users updated successfully.")
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error updating users.", e)
        }
}
fun addRestaurant(restaurant: RestaurantData) {
    val db = Firebase.firestore
    db.collection("restaurants").add(restaurant)
        .addOnSuccessListener { documentReference ->
            restaurant.id = documentReference.id
            Log.d("Firestore", "Restaurant added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error adding restaurant", e)
        }
}


suspend fun getAllRestaurants(): List<RestaurantData> {
    val db = Firebase.firestore
    return try {
        val result = db.collection("restaurants").get().await()
        result.map { document ->
            document.toObject(RestaurantData::class.java).apply {
                id = document.id // Устанавливаем id документа
            }
        }
    } catch (e: Exception) {
        emptyList() // Возвращаем пустой список в случае ошибки
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