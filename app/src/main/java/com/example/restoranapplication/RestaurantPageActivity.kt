package com.example.restoranapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
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
import com.example.restoranapplication.data.RestaurantData
import com.example.restoranapplication.data.RestaurantInfo
import com.example.restoranapplication.data.ReviewData
import com.example.restoranapplication.data.findFavouritesByRestaurantID
import com.example.restoranapplication.data.findReviewByRestaurantID
import com.example.restoranapplication.data.getAllRestaurants
import com.example.restoranapplication.data.updateRestaurant
import com.example.restoranapplication.data.updateUsers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class RestaurantPageActivity : BaseActivity() {
    override fun onSwipeLeft() {
        val callingActivityName = intent.getStringExtra("calling_activity")
        if (callingActivityName != null) {
            val callingActivityClass =
                Class.forName(callingActivityName)
            navigateToActivity(callingActivityClass)
        }
    }

    override fun onDoubleTapAction() {
        val restaurant = getRestaurant() // Получение текущего ресторана
        handleFavouriteAction(restaurant) // Обработка добавления/удаления из избранного
    }

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
                showMenuDialog(restaurant)
            }
            val editButton = findViewById<Button>(R.id.editButtonRestaurant)
            editButton.setOnClickListener {
                showEditRestaurantDialog(restaurant)

            }
            editButton.visibility = View.VISIBLE
            if (!loggedUser.isAdmin)
                editButton.visibility = View.GONE
            val ratingBar = findViewById<RatingBar>(R.id.ratingBarPage)
            ratingBar.rating = restaurant.rating
            ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
                if (fromUser)
                    if (flagIsUserLogged) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val isConfirmed = showConfirmationDialog(rating)
                            if (isConfirmed) {
                                Toast.makeText(
                                    this@RestaurantPageActivity,
                                    "Оценка $rating сохранена",
                                    Toast.LENGTH_SHORT
                                ).show()


                                val mutTemp = loggedUser.reviews.toMutableList()
                                val review = ReviewData(
                                    rating = rating,
                                    rest = restaurant.id,
                                    name = restaurant.name
                                )
                                val userReview = findReviewByRestaurantID(mutTemp, restaurant.id)
                                if (userReview != null) {
                                    ratingBar.rating =
                                        (restaurant.rating * restaurant.ratesAmount + rating - userReview.rating) /
                                                (restaurant.ratesAmount)
                                    restaurant.rating = ratingBar.rating
                                    mutTemp.replaceAll { reviews ->
                                        if (reviews.rest == restaurant.id && reviews.text == "") review else reviews
                                    }
                                } else {
                                    ratingBar.rating =
                                        (restaurant.rating * restaurant.ratesAmount + rating) /
                                                (restaurant.ratesAmount + 1)
                                    restaurant.rating = ratingBar.rating
                                    restaurant.ratesAmount++
                                    mutTemp.add(review)
                                }
                                showReviewDialog(restaurant.id, restaurant.name, rating = rating)
                                loggedUser.reviews = mutTemp.toList()
                                updateUsers(loggedUser.id, loggedUser)
                                saveRestaurant(restaurant)
                                updateRestaurant(restaurantId = restaurant.id, restaurant)
                                saveRestList(getAllRestaurants())
                            }
                        }
                    } else {
                        showDialog("Warning", "Сначала зайдите в аккаунт")
                        ratingBar.rating = restaurant.rating
                    }
            }

            val returnButton = findViewById<Button>(R.id.returnButtonRestoranPage)
            returnButton.setOnClickListener {
                val callingActivityName = intent.getStringExtra("calling_activity")
                if (callingActivityName != null) {
                    val callingActivityClass =
                        Class.forName(callingActivityName) // Получаем класс из имени
                    val intent = Intent(this, callingActivityClass)
                    startActivity(intent)
                    finish()
                }
            }

            val favourite = findViewById<Button>(R.id.favButton)
            favourite.setOnClickListener {
                handleFavouriteAction(restaurant)
            }
        } else {
            val intent = Intent(this, RestaurantListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private var currentMenuDialog: AlertDialog? = null
    private fun showMenuDialog(restaurant: RestaurantData) {
        currentMenuDialog?.dismiss()
        val menuItems = restaurant.menu
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        for (menuItem in menuItems) {
            val menuItemView = LayoutInflater.from(this).inflate(R.layout.menu_item, null)
            val dishName = menuItemView.findViewById<TextView>(R.id.dishName)
            val price = menuItemView.findViewById<TextView>(R.id.price)
            val deleteButton = menuItemView.findViewById<ImageView>(R.id.deleteButton)

            dishName.text = menuItem.dishName
            price.text = menuItem.price.toString()


            if (flagIsUserLogged) {
                if (loggedUser.isAdmin) {
                    deleteButton.visibility = View.VISIBLE
                    deleteButton.setOnClickListener {
                        restaurant.menu = restaurant.menu.filter { it != menuItem }
                        updateRestaurant(restaurant.id, restaurant)
                        showMenuDialog(restaurant)
                    }
                } else
                    deleteButton.visibility = View.GONE
            } else
                deleteButton.visibility = View.GONE
            layout.addView(menuItemView)
        }
        val addButton = Button(this).apply {
            text = "Добавить блюдо"
            setOnClickListener {
                showAddMenuItemDialog(restaurant)
            }
        }
        if (flagIsUserLogged) {
            if (loggedUser.isAdmin) {
                addButton.visibility = View.VISIBLE
                layout.addView(addButton)
            } else
                addButton.visibility = View.GONE
        } else
            addButton.visibility = View.GONE

        currentMenuDialog = AlertDialog.Builder(this)
            .setTitle("Меню")
            .setView(layout)
            .setPositiveButton("Закрыть", null)
            .create()

        currentMenuDialog?.show()
    }

    private fun showAddMenuItemDialog(restaurant: RestaurantData) {
        currentMenuDialog?.dismiss()
        val dishNameInput = EditText(this).apply {
            hint = "Введите название блюда"
        }

        val priceInput = EditText(this).apply {
            hint = "Введите цену блюда"
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(dishNameInput)
            addView(priceInput)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Добавить блюдо")
            .setView(layout)
            .setPositiveButton("Сохранить") { _, _ ->
                val newDishName = dishNameInput.text.toString()
                val newPrice = priceInput.text.toString().toDoubleOrNull()

                if (!newDishName.isBlank() && newPrice != null) {
                    val newMenuItem = MenuItem(dishName = newDishName, price = newPrice)
                    restaurant.menu += newMenuItem
                    updateRestaurant(restaurant.id, restaurant)
                    showMenuDialog(restaurant)
                    Toast.makeText(this, "Блюдо добавлено", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
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

    private fun showReviewDialog(restaurantId: String, name: String, rating: Float) {
        val reviewEditText = EditText(this).apply {
            hint = "Введите текст рецензии"
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(reviewEditText)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Оставить рецензию")
            .setView(layout)
            .setPositiveButton("Сохранить") { _, _ ->
                val reviewText = reviewEditText.text.toString()
                if (reviewText.isNotBlank()) {
                    val review = ReviewData(
                        text = reviewText,
                        rest = restaurantId,
                        name = name,
                        rating = rating
                    )
                    val tempList = loggedUser.reviews.toMutableList()
                    tempList.add(review)
                    loggedUser.reviews = tempList.toList()
                    updateUsers(loggedUser.id, loggedUser)
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }

    private fun showEditRestaurantDialog(restaurant: RestaurantData) {
        val nameInput = EditText(this).apply {
            hint = "Введите название ресторана"
            setText(restaurant.name)
        }

        val addressInput = EditText(this).apply {
            hint = "Введите адрес ресторана"
            setText(restaurant.address)
        }

        val imageUrlInput = EditText(this).apply {
            hint = "Введите URL изображения"
            setText(restaurant.imageURL)
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(nameInput)
            addView(addressInput)
            addView(imageUrlInput)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Редактировать информацию о ресторане")
            .setView(layout)
            .setPositiveButton("Сохранить") { _, _ ->
                val updatedName = nameInput.text.toString()
                val updatedAddress = addressInput.text.toString()
                val updatedImageUrl = imageUrlInput.text.toString()

                restaurant.name = updatedName
                restaurant.address = updatedAddress
                restaurant.imageURL = updatedImageUrl

                updateRestaurant(restaurant.id, restaurant)
                lifecycleScope.launch {
                    saveRestList(getAllRestaurants())
                }
                val callingActivityName = intent.getStringExtra("calling_activity")
                if (callingActivityName != null) {
                    val intent = Intent(this, RestaurantPageActivity::class.java)
                        .putExtra("calling_activity", callingActivityName)
                    startActivity(intent)
                    finish()
                }
                Toast.makeText(this, "Информация о ресторане обновлена", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }

    private fun handleFavouriteAction(restaurant: RestaurantData?) {
        restaurant?.let {
            if (flagIsUserLogged) {
                val favourites = loggedUser.favouriteRest.toMutableList()
                if (findFavouritesByRestaurantID(
                        loggedUser.favouriteRest,
                        restaurant.id
                    ) != null
                ) {
                    Toast.makeText(
                        this@RestaurantPageActivity,
                        "Убрано из любимого",
                        Toast.LENGTH_SHORT
                    ).show()
                    favourites.removeIf { it.rest == restaurant.id }
                    loggedUser.favouriteRest = favourites.toList()
                    updateUsers(loggedUser.id, loggedUser)
                } else {

                    Toast.makeText(
                        this@RestaurantPageActivity,
                        "Добавлено в любимое",
                        Toast.LENGTH_SHORT
                    ).show()
                    favourites.add(RestaurantInfo(rest = restaurant.id, name = restaurant.name))
                    loggedUser.favouriteRest = favourites.toList()
                    updateUsers(loggedUser.id, loggedUser)
                }

            } else {
                showDialog("Warning", "Сначала зайдите в аккаунт")
            }
        }
    }
}