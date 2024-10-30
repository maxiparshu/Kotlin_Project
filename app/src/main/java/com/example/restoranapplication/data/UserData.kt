package com.example.restoranapplication.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class ReviewData (
    val text: String = "",
    val restName: String = ""
)
data class UserData (
    @Exclude var id: String = "",
    @PropertyName("admin")val isAdmin : Boolean = false,
    val login : String = "",
    val password: String = "",
    var reviews: List<ReviewData> = emptyList(),
    val favouriteRest: List<String> = emptyList()
)
fun findUserByLogin(users: List<UserData>, login: String): UserData? {
    return users.find { it.login == login }
}