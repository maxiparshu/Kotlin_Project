package com.example.restoranapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.restoranapplication.data.UserData
import com.example.restoranapplication.data.addUser
import com.example.restoranapplication.data.findUserByLogin

class LoginActivity : BaseActivity() {
    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        val userList = getUserList()
        val button = findViewById<Button>(R.id.returnButton)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val loginEdit = findViewById<EditText>(R.id.buttonLogin)
        val passwordEdit = findViewById<EditText>(R.id.password)
        val checkBox = findViewById<CheckBox>(R.id.adminCheckBox)
        loginEdit.text.clear()
        val loginButton = findViewById<Button>(R.id.loginButton)
        val regButton = findViewById<Button>(R.id.regButton)
        regButton.setOnClickListener {
            if (loginEdit.text.isEmpty()) {
                showDialog("Warning", "Введите логин")
                return@setOnClickListener
            }
            if (passwordEdit.text.isEmpty()) {
                showDialog("Warning", "Введите пароль")
                return@setOnClickListener
            }
            val tempUser = findUserByLogin(userList, loginEdit.text.toString())
            if (tempUser == null) {
                loggedUser = UserData(
                    isAdmin = checkBox.isChecked,
                    login = loginEdit.text.toString(),
                    password = passwordEdit.text.toString(),
                    reviews = emptyList(),
                    favouriteRest = emptyList()
                )
                addUser(loggedUser)
                flagIsUserLogged = true
                Toast.makeText(
                    this,
                    "Вы успешно зарегистрировались ${loggedUser.login}",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }
            showDialog("Error", "Аккаунт с введеным логином уже существует")
        }
        loginButton.setOnClickListener {
            if (loginEdit.text.isEmpty()) {
                showDialog("Warning", "Введите логин")
                return@setOnClickListener
            }
            if (passwordEdit.text.isEmpty()) {
                showDialog("Warning", "Введите пароль")
                return@setOnClickListener
            }
            val tempUser = findUserByLogin(userList, loginEdit.text.toString())
            if (tempUser == null) {
                showDialog(
                    "Error", "Аккаунт с логином ${loginEdit.text} " +
                            "не существуют"
                )
                return@setOnClickListener
            }
            if (tempUser.password != passwordEdit.text.toString()) {
                showDialog("Error", "Введен неправильный пароль")
                return@setOnClickListener
            }
            if (tempUser.isAdmin != checkBox.isChecked) {
                if (!tempUser.isAdmin)
                    showDialog(
                        "Error", "Данный аккаунт " +
                                "зарегистрирован как обычный пользователь"
                    )
                else
                    showDialog("Error", "Данный аккаунт " +
                            "зарегистрирован как администатор")
                return@setOnClickListener
            }
            loggedUser = tempUser
            flagIsUserLogged = true
            Toast.makeText(
                this,
                "Вы успешно зашли ${loggedUser.login}",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}