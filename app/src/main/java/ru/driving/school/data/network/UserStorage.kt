package ru.driving.school.data.network

import android.content.Context

class UserStorage(
    context: Context
) {

    private val shared = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    fun getAccessToken(): String? {
        return shared.getString("access_token", null)
    }

    fun saveAccessToken(value: String?) {
        shared.edit()
            .putString("access_token", value)
            .apply()
    }
}