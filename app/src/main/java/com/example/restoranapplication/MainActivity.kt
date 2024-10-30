package com.example.restoranapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.restoranapplication.data.RestaurantData
import com.example.restoranapplication.data.ReviewData
import com.example.restoranapplication.data.UserData
import com.example.restoranapplication.data.addUser
import com.example.restoranapplication.data.getAllRestaurants
import com.example.restoranapplication.data.getAllUsers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .into(imageView)
    }

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
            }
            saveRestList(getAllRestaurants())
            saveUserList(getAllUsers())
            button.setOnClickListener {
                if (!flagIsUserLogged) {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@MainActivity, UserPageActivity::class.java)
                    startActivity(intent)
                }
            }

            val favButton = findViewById<ImageView>(R.id.favourite)
            favButton.setOnClickListener {
                if (!flagIsUserLogged) {
                    showDialog("Error", "Сначала зайдите в аккаунт")
                }
            }
            val usersButton = findViewById<Button>(R.id.otherUser)
            usersButton.setOnClickListener {
                val intent = Intent(this@MainActivity, UsersPageActivity::class.java)
                startActivity(intent)
            }
        }

    }
}
