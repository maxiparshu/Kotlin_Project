package com.example.restoranapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.restoranapplication.helpers.showReviewsDialog

class UserPageActivity : BaseActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page)
        val loginLabel = findViewById<TextView>(R.id.loginPage)
        val statusLabel = findViewById<TextView>(R.id.status)
        val idLabel = findViewById<TextView>(R.id.idText)
        val reviewButton = findViewById<Button>(R.id.reviewUserPage)


        loginLabel.clearComposingText()
        idLabel.clearComposingText()
        idLabel.text ="id: ${loggedUser.id}"
        loginLabel.text = loggedUser.login
        reviewButton.setOnClickListener{
            this.showReviewsDialog(loggedUser.reviews, getRestList(), this, UserPageActivity::class.java.name)
        }
        if (loggedUser.isAdmin)
            statusLabel.text = "Администратор"
        else
            statusLabel.text = "Пользователь"
        val returnButton = findViewById<Button>(R.id.returnFromPage)
        returnButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val exitAcc = findViewById<Button>(R.id.exitAcc)
        exitAcc.setOnClickListener{
            flagIsUserLogged = false
            loggedUser.isAdmin = false
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}