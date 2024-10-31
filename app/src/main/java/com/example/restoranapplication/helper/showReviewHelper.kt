package com.example.restoranapplication.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.restoranapplication.RestaurantPageActivity
import com.example.restoranapplication.data.RestaurantData
import com.example.restoranapplication.data.RestaurantInfo
import com.example.restoranapplication.data.ReviewData
import com.example.restoranapplication.data.UserData
import com.example.restoranapplication.data.findRestaurantById
import com.example.restoranapplication.data.`interface`.OnRestaurantSelectedListener
import com.example.restoranapplication.data.updateUsers

@SuppressLint("SetTextI18n")
fun Context.showFavouriteDialog(
    favourites: List<RestaurantInfo>,
    rests: List<RestaurantData>,
    listener: OnRestaurantSelectedListener,
    callingActivity: String,
    user: UserData,
) {
    val dialogBuilder = AlertDialog.Builder(this)
        .setTitle("Ваши любимые места")

    val container = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(20, 20, 20, 20)
    }
    val mutableFav = favourites.toMutableList()
    favourites.forEach { favourite ->
        val reviewTextView = TextView(this).apply {
            setPadding(20, 20, 20, 20)
            val restaurant = findRestaurantById(rests, favourite.rest)
            if (restaurant != null) {
                if (restaurant.name != favourite.name)
                    mutableFav[favourites.indexOf(favourite)].name = favourite.name
                text = "Ресторан: ${restaurant.name}\n"
                this.setOnClickListener {
                    openRestaurantPage(
                        restaurant,
                        listener,
                        callingActivity
                    )
                }
                setTextColor(Color.BLUE)
            } else {
                mutableFav.remove(favourite)
            }
        }

        val border = GradientDrawable().apply {
            setColor(Color.WHITE)
            setStroke(2, Color.LTGRAY)
            cornerRadius = 8f
        }
        reviewTextView.background = border


        if (mutableFav.size == favourites.size)
            container.addView(reviewTextView)


        val divider = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                20
            )
        }
        container.addView(divider)
    }

    user.favouriteRest = mutableFav.toList()
    updateUsers(user.id, user)
    dialogBuilder
        .setView(container)
        .setPositiveButton("OK", null)
        .show()
}

@SuppressLint("SetTextI18n")
fun Context.showReviewsDialog(
    reviews: List<ReviewData>,
    rests: List<RestaurantData>,
    listener: OnRestaurantSelectedListener,
    callingActivity: String,
    user: UserData,
) {
    val dialogBuilder = AlertDialog.Builder(this)
        .setTitle("Отзывы пользователя")

    val container = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(20, 20, 20, 20)
    }
    val mutableRev = reviews.toMutableList()
    reviews.forEach { review ->
        if (review.text != "") {
            val reviewTextView = TextView(this).apply {
                text = "Ресторан: ${review.name} Оценка: ${review.rating}/5.0\nОтзыв: ${review.text}"
                setPadding(20, 20, 20, 20)
                if (user.id != "") {
                    this.setOnLongClickListener {
                        AlertDialog.Builder(this@showReviewsDialog)
                            .setMessage("Удалить этот ресторан из избранного?")
                            .setPositiveButton("Удалить") { _, _ ->
                                mutableRev.remove(review)
                                user.reviews = mutableRev.toList()
                                updateUsers(user.id, user)
                                container.removeView(this) // Удаляем TextView
                            }
                            .setNegativeButton("Отмена", null)
                            .show()
                        true
                    }
                }
                val restaurant = findRestaurantById(rests, review.rest)
                if (restaurant != null) {
                    setOnClickListener { openRestaurantPage(restaurant, listener, callingActivity) }
                    setTextColor(Color.BLUE)
                }
            }


            val border = GradientDrawable().apply {
                setColor(Color.WHITE)
                setStroke(2, Color.LTGRAY)
                cornerRadius = 8f
            }
            reviewTextView.background = border

            container.addView(reviewTextView)

            // Добавляем разделитель между отзывами
            val divider = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    20
                )
            }
            container.addView(divider)
        }
    }

    dialogBuilder
        .setView(container)
        .setPositiveButton("OK", null)
        .show()
}

// Переход на страницу ресторана
fun Context.openRestaurantPage(
    rest: RestaurantData,
    listener: OnRestaurantSelectedListener,
    callingActivity: String,
) {
    listener.saveRestaurant(rest)
    val intent = Intent(this, RestaurantPageActivity::class.java)
        .putExtra("calling_activity", callingActivity)
    (this as? Activity)?.finish()
    startActivity(intent)
}
