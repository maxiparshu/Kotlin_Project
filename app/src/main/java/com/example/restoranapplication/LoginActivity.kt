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
    companion object {
        init {
            System.loadLibrary("login") // замените на имя вашей библиотеки без префикса "lib"
        }
    }
    private external fun checkLogin(
        login: String,
        password: String,
        isAdminChecked: Boolean,
        users: List<UserData>,
    ): String

    override fun onSwipeLeft() {
        navigateToActivity(MainActivity::class.java)
    }

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
            val result = checkLogin(
                loginEdit.text.toString(), passwordEdit.text.toString(),
                checkBox.isChecked, userList
            )
            if (result[0] != 'S') {
                showDialog("Error", result)
                return@setOnClickListener
            }
            val id = result.drop(1)
            loggedUser = UserData(
                id = id,
                login= loginEdit.text.toString(),
                password= passwordEdit.text.toString(),
                isAdmin= checkBox.isChecked,
                reviews = emptyList(),
                favouriteRest = emptyList()
            )
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