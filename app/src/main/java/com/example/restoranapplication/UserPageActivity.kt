package com.example.restoranapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class UserPageActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page)
        val loginLabel = findViewById<TextView>(R.id.loginPage)
        val statusLabel = findViewById<TextView>(R.id.status)
        loginLabel.clearComposingText()
        loginLabel.text = loggedUser.login
        if (loggedUser.isAdmin)
            statusLabel.text = "Администратор"
        else
            statusLabel.text = "Пользователь"
        val returnButton = findViewById<Button>(R.id.returnFromPage)
        returnButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val exitAcc = findViewById<Button>(R.id.exitAcc)
        exitAcc.setOnClickListener{
            flagIsUserLogged = false
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}