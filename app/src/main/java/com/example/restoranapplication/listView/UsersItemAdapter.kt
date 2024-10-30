package com.example.restoranapplication.listView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.restoranapplication.R
import com.example.restoranapplication.data.RestaurantData
import com.example.restoranapplication.data.UserData
import com.example.restoranapplication.data.`interface`.OnRestaurantSelectedListener
import com.example.restoranapplication.helpers.showReviewsDialog

class UsersItemAdapter(
    context: Context, users: List<UserData>,
    private val rests: List<RestaurantData>, private val listener: OnRestaurantSelectedListener,
) :
    ArrayAdapter<UserData>(context, 0, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val user = getItem(position)

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.users_list_item, parent, false
        )

        // Найдите TextView для логина и статуса администратора
        val loginTextView = view.findViewById<TextView>(R.id.loginTextView)
        val statusTextView = view.findViewById<TextView>(R.id.statusTextView)

        // Установите логин пользователя
        loginTextView.text = user?.login

        // Установите статус администратора
        statusTextView.text = if (user?.isAdmin == true) {
            "Администатор"
        } else {
            "Пользователь"
        }
        view.setOnClickListener {
            user?.let {
                context.showReviewsDialog(it.reviews, rests, listener)
            }
        }
        return view
    }
}