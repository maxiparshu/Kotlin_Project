package com.example.restoranapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.restoranapplication.data.MenuItem
import com.example.restoranapplication.data.ReviewData
import com.example.restoranapplication.data.getAllRestaurants
import com.example.restoranapplication.data.updateRestaurant
import com.example.restoranapplication.data.updateUsers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class RestaurantPageActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restoran_window)
        val restaurant = getRestaurant()
        if (restaurant != null) {
            val image = findViewById<ImageView>(R.id.restaurantPageImage)
            Glide.with(this)
                .load(restaurant.imageURL)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(image)

            val name = findViewById<TextView>(R.id.restaurantPageName)
            name.text = restaurant.name

            val address = findViewById<TextView>(R.id.restAdress)

            address.text = restaurant.address

            val menuButton = findViewById<Button>(R.id.menuButton)

            menuButton.setOnClickListener {
                showMenuDialog(restaurant.menu)
            }

            val ratingBar = findViewById<RatingBar>(R.id.ratingBarPage)
            ratingBar.rating = restaurant.rating
            ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                if (flagIsUserLogged) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val isConfirmed = showConfirmationDialog(rating)
                        if (isConfirmed) {
                            // Логика для подтвержденного рейтинга
                            Toast.makeText(
                                this@RestaurantPageActivity,
                                "Оценка $rating сохранена",
                                Toast.LENGTH_SHORT
                            ).show()
                            ratingBar.rating =
                                (restaurant.rating * restaurant.ratesAmount + rating) / (restaurant.ratesAmount + 1)
                            restaurant.rating = ratingBar.rating
                            restaurant.ratesAmount++
                            saveRestaurant(restaurant)
                            updateRestaurant(restaurantId = restaurant.id, restaurant)
                            lifecycleScope.launch {
                                saveRestList(getAllRestaurants())
                            }
                        }
                    }
                }
                else {
                    showDialog("Warning", "Сначала зайдите в аккаунт")
                    ratingBar.rating = restaurant.rating
                }
            }

            val returnButton = findViewById<Button>(R.id.returnButtonRestoranPage)
            returnButton.setOnClickListener {
                val intent = Intent(this, RestaurantListActivity::class.java)
                startActivity(intent)
                finish()
            }

            val favourite = findViewById<Button>(R.id.favButton)
            favourite.setOnClickListener{
                if (flagIsUserLogged) {
                    showReviewDialog(restaurant.name)
                }
                else {
                    showDialog("Warning", "Сначала зайдите в аккаунт")
                }
            }
        } else {
            val intent = Intent(this, RestaurantListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showMenuDialog(menu: List<MenuItem>) {
        val menuItems = menu.joinToString(separator = "\n") { "${it.dishName} - $${it.price}" }

        AlertDialog.Builder(this)
            .setTitle("Menu")
            .setMessage(menuItems)
            .setPositiveButton("OK", null)
            .show()
    }

    private suspend fun showConfirmationDialog(rating: Float): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val dialog = AlertDialog.Builder(this)
                .setTitle("Подтверждение оценки")
                .setMessage("Вы уверены, что хотите поставить оценку $rating?")
                .setPositiveButton("Подтвердить") { _, _ ->
                    continuation.resume(true)
                }
                .setNegativeButton("Отмена") { dialog, _ ->
                    dialog.dismiss()
                    continuation.resume(false)
                }
                .create()

            dialog.show()
        }
    }
    private fun showReviewDialog(restaurantName : String) {
        // Создаем поля для ввода текста рецензии и названия ресторана
        val reviewEditText = EditText(this).apply {
            hint = "Введите текст рецензии"
        }
        // Размещаем поля в вертикальном контейнере
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(reviewEditText)
        }

        // Создаем диалог
        val dialog = AlertDialog.Builder(this)
            .setTitle("Оставить рецензию")
            .setView(layout)
            .setPositiveButton("Сохранить") { _, _ ->
                val reviewText = reviewEditText.text.toString()
                if (reviewText.isNotBlank()) {
                    val review = ReviewData(text = reviewText, restName = restaurantName)
                    val tempList = loggedUser.reviews.toMutableList()
                    tempList.add(review)
                    loggedUser.reviews =  tempList.toList()
                    updateUsers(loggedUser.id, loggedUser)
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }
}