package com.example.restoranapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.restoranapplication.data.RestaurantData
import com.example.restoranapplication.data.ReviewData
import com.example.restoranapplication.data.UserData
import com.example.restoranapplication.data.`interface`.OnRestaurantSelectedListener
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

open class BaseActivity : AppCompatActivity(), OnRestaurantSelectedListener  {
    companion object {
        var flagIsUserLogged: Boolean = false
        var loggedUser: UserData = UserData(
            isAdmin = false,
            login = "",
            password = "",
            reviews = emptyList(),
            favouriteRest = emptyList()
        )
    }
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val loggedUserKey = "LoggedUser"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    }

    fun showDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss() // Закрыть диалог
        }
        builder.show()
    }

    fun saveUserData(user: UserData) {
        val editor = sharedPreferences.edit()
        val jsonString = gson.toJson(user)
        editor.putString(loggedUserKey, jsonString)
        editor.apply()
    }
    fun saveUserList(userList: List<UserData>) {
        val jsonString = gson.toJson(userList)
        sharedPreferences.edit()
            .putString("user_list", jsonString)
            .apply() // Применение изменений
    }
    // Метод для чтения UserData из SharedPreferences
    fun getUserData(): UserData? {
        val jsonString = sharedPreferences.getString(loggedUserKey, null)
        return if (jsonString != null) {
            gson.fromJson(jsonString, UserData::class.java)
        } else {
            null
        }
    }
    fun getUserList(): List<UserData> {
        val jsonString = sharedPreferences.getString("user_list", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<UserData>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList() // Если данных нет, возвращается пустой список
        }
    }
    override fun saveRestList(restList: List<RestaurantData>) {
        val jsonString = gson.toJson(restList)
        sharedPreferences.edit()
            .putString("restaurant_list", jsonString)
            .apply() // Применение изменений
    }
    fun getRestList(): List<RestaurantData> {
        val jsonString = sharedPreferences.getString("restaurant_list", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<RestaurantData>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList() // Если данных нет, возвращается пустой список
        }
    }
    // Метод для сохранения флага состояния логина
    fun saveLoginStatus(isLoggedIn: Boolean) {
        sharedPreferences.edit()
            .putBoolean("is_logged_in", isLoggedIn)
            .apply() // Применение изменений
    }
    override fun saveRestaurant(rest: RestaurantData) {
        val jsonString = gson.toJson(rest)
        sharedPreferences.edit()
            .putString("restaurant", jsonString)
            .apply() // Применение изменений
    }
    fun getRestaurant() : RestaurantData? {
        val jsonString = sharedPreferences.getString("restaurant", null)
        return if (jsonString != null) {
            gson.fromJson(jsonString, RestaurantData::class.java)
        } else {
            null
        }
    }
    fun getLoginStatus(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }
    // Метод для очистки данных пользователя из SharedPreferences (например, при выходе)
    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }
}