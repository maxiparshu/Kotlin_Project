package com.example.restoranapplication

import RestaurantItemAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.restoranapplication.data.MenuItem
import com.example.restoranapplication.data.RestaurantData
import com.example.restoranapplication.data.addRestaurant
import com.example.restoranapplication.data.getAllRestaurants
import kotlinx.coroutines.launch


class RestaurantListActivity : BaseActivity() {
    override fun onSwipeLeft() {
        navigateToActivity(MainActivity::class.java)
    }

    private fun showAddRestaurantDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_restaurant, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextRestaurantName)
        val editTextMenuItems = dialogView.findViewById<EditText>(R.id.editTextMenuItems)
        val editTextImageUrl = dialogView.findViewById<EditText>(R.id.editTextImageUrl)
        val editAddress = dialogView.findViewById<EditText>(R.id.editAddress)
        AlertDialog.Builder(this)
            .setTitle("Добавить ресторан")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val name = editTextName.text.toString()
                val menuItemsText = editTextMenuItems.text.toString()
                val imageUrl = editTextImageUrl.text.toString()
                val addressText = editAddress.text.toString()

                if (name.isNotEmpty() && menuItemsText.isNotEmpty() && imageUrl.isNotEmpty()) {
                    try {
                        val menuItems = menuItemsText.split(",").map {
                            val (dish, price) = it.trim().split("-")
                            MenuItem(dish.trim(), price.trim().toDouble())
                        }

                        val restaurant = RestaurantData(
                            name = name,
                            menu = menuItems,
                            imageURL = imageUrl,
                            rating = 0.0f,
                            address = addressText,
                            ratesAmount = 0
                        )
                        addRestaurant(restaurant)
                        lifecycleScope.launch {
                            saveRestList(getAllRestaurants())
                            val intent = intent
                            finish()
                            startActivity(intent)
                        }


                        Toast.makeText(
                            this,
                            "Ресторан добавлен: ${restaurant.name}",
                            Toast.LENGTH_SHORT
                        ).show()

                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            "Ошибка при вводе данных. Проверьте формат.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Пожалуйста, заполните все поля.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restoran_list)
        val returnButton = findViewById<Button>(R.id.returnButtonFromList)
        returnButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            showAddRestaurantDialog()
        }
        if (!loggedUser.isAdmin) {
            addButton.visibility = View.GONE
        } else {
            addButton.visibility = View.VISIBLE
        }

        val restaurantListView = findViewById<ListView>(R.id.restaurantList)
        val restaurantList = getRestList()
        if (restaurantList.isNotEmpty()) {
            restaurantListView.adapter =
                RestaurantItemAdapter(
                    this, R.layout.restaraunt_list_item,
                    loggedUser.isAdmin, restaurantList, this
                )
        }
    }
}