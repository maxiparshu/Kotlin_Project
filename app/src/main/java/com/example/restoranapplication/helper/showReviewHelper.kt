package com.example.restoranapplication.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.restoranapplication.RestaurantPageActivity
import com.example.restoranapplication.data.RestaurantData
import com.example.restoranapplication.data.ReviewData
import com.example.restoranapplication.data.findRestaurantByName
import com.example.restoranapplication.data.`interface`.OnRestaurantSelectedListener

@SuppressLint("SetTextI18n")
fun Context.showReviewsDialog(
    reviews: List<ReviewData>,
    rests: List<RestaurantData>,
    listener: OnRestaurantSelectedListener,
) {
    val dialogBuilder = AlertDialog.Builder(this)
        .setTitle("Отзывы пользователя")

    val container = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(20, 20, 20, 20)
    }

    reviews.forEach { review ->
        val reviewTextView = TextView(this).apply {
            text = "Ресторан: ${review.restName}\nОтзыв: ${review.text}"
            setPadding(20, 20, 20, 20)

            // Делаем название ресторана кликабельным, если он есть в списке
            val restaurant = findRestaurantByName(rests, review.restName)
            if (restaurant != null) {
                setOnClickListener { openRestaurantPage(restaurant, listener) }
                setTextColor(Color.BLUE)
            }
        }

        // Устанавливаем границу для TextView отзыва
        val border = GradientDrawable().apply {
            setColor(Color.WHITE)
            setStroke(2, Color.LTGRAY)
            cornerRadius = 8f
        }
        reviewTextView.background = border

        // Добавляем отзыв в контейнер
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

    dialogBuilder
        .setView(container)
        .setPositiveButton("OK", null)
        .show()
}

// Переход на страницу ресторана
fun Context.openRestaurantPage(rest: RestaurantData, listener: OnRestaurantSelectedListener) {
    listener.saveRestaurant(rest)
    val intent = Intent(this, RestaurantPageActivity::class.java)
    startActivity(intent)
}
