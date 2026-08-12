package com.example.bknova.controller

import android.content.Context
import android.content.SharedPreferences
import com.example.bknova.model.Login
import com.example.bknova.model.LoginFeedback
import com.example.bknova.service.Aktor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthController(private val context: Context) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    interface LoginCallback {
        fun onSuccess(user: LoginFeedback)
        fun onError(message: String)
    }

    fun login(loginRequest: Login, callback: LoginCallback) {
        Aktor.auth.Login_Services(loginRequest).enqueue(object : Callback<LoginFeedback> {
            override fun onResponse(call: Call<LoginFeedback>, response: Response<LoginFeedback>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        saveSession(user)
                        callback.onSuccess(user)
                    } else {
                        callback.onError("Response body is null")
                    }
                } else {
                    callback.onError("Login gagal: Periksa kembali akun Anda")
                }
            }

            override fun onFailure(call: Call<LoginFeedback>, t: Throwable) {
                callback.onError("Terjadi kesalahan: ${t.message}")
            }
        })
    }

    private fun saveSession(user: LoginFeedback) {
        val editor = sharedPref.edit()
        editor.putString("token", user.token)
        editor.putString("role", user.role)
        editor.putString("name", user.nama)
        editor.putBoolean("is_logged_in", true)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean("is_logged_in", false)
    }

    fun logout() {
        sharedPref.edit().clear().apply()
    }
}
