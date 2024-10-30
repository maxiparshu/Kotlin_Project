package com.example.restoranapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import com.example.restoranapplication.listView.UsersItemAdapter

class UsersPageActivity : BaseActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.users_page)

        val returnButton = findViewById<Button>(R.id.returnFromUsers)
        returnButton.setOnClickListener {
            val intent = Intent(this@UsersPageActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        var users = getUserList()
        if (flagIsUserLogged)
            users = getUserList().filter { it.login != loggedUser.login }
        val usersList = findViewById<ListView>(R.id.usersList)
        usersList.adapter = UsersItemAdapter(this, users, getRestList(), this)
    }
}