package com.example.restoranapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.restoranapplication.data.RestaurantData
import com.example.restoranapplication.data.UserData
import com.example.restoranapplication.data.`interface`.OnRestaurantSelectedListener
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.abs

open class BaseActivity : AppCompatActivity(), OnRestaurantSelectedListener,
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private lateinit var gestureDetector: GestureDetector

    companion object {
        private const val SWIPE_THRESHOLD = 100 // Пороговое значение для свайпа

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        gestureDetector = GestureDetector(this, this)

    }

    fun showDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    fun saveUserList(userList: List<UserData>) {
        val jsonString = gson.toJson(userList)
        sharedPreferences.edit()
            .putString("user_list", jsonString)
            .apply()
    }


    fun getUserList(): List<UserData> {
        val jsonString = sharedPreferences.getString("user_list", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<UserData>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }

    override fun saveRestList(restList: List<RestaurantData>) {
        val jsonString = gson.toJson(restList)
        sharedPreferences.edit()
            .putString("restaurant_list", jsonString)
            .apply()
    }

    fun getRestList(): List<RestaurantData> {
        val jsonString = sharedPreferences.getString("restaurant_list", null)
        return if (jsonString != null) {
            val type = object : TypeToken<List<RestaurantData>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }

    override fun saveRestaurant(rest: RestaurantData) {
        val jsonString = gson.toJson(rest)
        sharedPreferences.edit()
            .putString("restaurant", jsonString)
            .apply() // Применение изменений
    }

    fun getRestaurant(): RestaurantData? {
        val jsonString = sharedPreferences.getString("restaurant", null)
        return if (jsonString != null) {
            gson.fromJson(jsonString, RestaurantData::class.java)
        } else {
            null
        }
    }

    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null)
            return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
        return false
    }

    override fun onDown(p0: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(p0: MotionEvent) {
    }

    override fun onSingleTapUp(p0: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun onLongPress(p0: MotionEvent) {
    }

    override fun onFling(
        e1: MotionEvent?,
        p1: MotionEvent,
        velocityX: Float,
        velocityY: Float,
    ): Boolean {
        if (e1 != null) {
            val diffX = p1.x - e1.x
            val diffY = p1.y - e1.y

            if (abs(diffX) > abs(diffY)) {
                if (diffX < -SWIPE_THRESHOLD) { // Свайп влево
                    onSwipeLeft()
                    return true
                }
            }
        }
        return false
    }


    protected fun navigateToActivity(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        startActivity(intent)
        finish()
    }

    override fun onSingleTapConfirmed(p0: MotionEvent): Boolean {
        return false
    }

    override fun onDoubleTap(p0: MotionEvent): Boolean {
        onDoubleTapAction()
        return true
    }

    override fun onDoubleTapEvent(p0: MotionEvent): Boolean {
        return false
    }

    open fun onDoubleTapAction() {
    }

    open fun onSwipeLeft() {
    }

}