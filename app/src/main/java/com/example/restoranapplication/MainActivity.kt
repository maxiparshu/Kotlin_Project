package com.example.restoranapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.example.restoranapplication.data.getAllRestaurants
import com.example.restoranapplication.data.getAllUsers
import com.example.restoranapplication.helpers.showFavouriteDialog
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycleScope.launch {
            val button = findViewById<ImageView>(R.id.login)
            if (!flagIsUserLogged)
                button.setImageResource(R.drawable.door)
            else
                button.setImageResource(R.drawable.user)
            val listButton = findViewById<Button>(R.id.findButton)
            listButton.setOnClickListener {
                val intent = Intent(this@MainActivity, RestaurantListActivity::class.java)
                startActivity(intent)
                finish()
            }
            saveRestList(getAllRestaurants())
            saveUserList(getAllUsers())
            button.setOnClickListener {
                if (!flagIsUserLogged) {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@MainActivity, UserPageActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }


        }
        val usersButton = findViewById<Button>(R.id.otherUser)
        usersButton.setOnClickListener {
            val intent = Intent(this@MainActivity, UsersPageActivity::class.java)
            startActivity(intent)
            finish()
        }
        val favButton = findViewById<ImageView>(R.id.favourite)
        favButton.setOnClickListener {
            if (!flagIsUserLogged) {
                showDialog("Error", "Сначала зайдите в аккаунт")
            } else {
                showFavouriteDialog(
                    loggedUser.favouriteRest,
                    getRestList(),
                    this,
                    MainActivity::class.java.name,
                    loggedUser
                )
            }
        }
    }
}
