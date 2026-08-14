package com.example.bknova.controller

import android.content.Context
import android.content.SharedPreferences
import com.example.bknova.model.Login
import com.example.bknova.model.LoginFeedback
import com.example.bknova.model.UserResponse
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

    interface UserCallback {
        fun onSuccess(user: UserResponse)
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

    fun getUserProfile(callback: UserCallback) {
        val token = sharedPref.getString("token", null)
        if (token == null) {
            callback.onError("Token tidak ditemukan")
            return
        }

        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        Aktor.auth.getMe(bearerToken).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        // Optionally update session with more details
                        callback.onSuccess(user)
                    } else {
                        callback.onError("Data user kosong")
                    }
                } else {
                    callback.onError("Gagal mengambil data user: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                callback.onError("Terjadi kesalahan: ${t.message}")
            }
        })
    }

    fun refreshToken(callback: LoginCallback) {
        val refreshToken = getRefreshToken()
        if (refreshToken == null) {
            callback.onError("Refresh token tidak ditemukan")
            return
        }

        val bearerToken = if (refreshToken.startsWith("Bearer ")) refreshToken else "Bearer $refreshToken"

        Aktor.auth.refreshToken(bearerToken).enqueue(object : Callback<LoginFeedback> {
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
                    callback.onError("Gagal memperbarui sesi: ${response.code()}")
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
        editor.putString("refresh_token", user.refreshToken)
        editor.putBoolean("is_logged_in", true)
        editor.apply()
    }

    fun saveUserProfile(user: UserResponse) {
        val editor = sharedPref.edit()
        editor.putInt("user_id", user.id)
        editor.putString("name", user.nama)
        editor.putString("role", user.role)
        // Refresh token is already saved during login, but we can update other fields here
        editor.apply()
    }

    fun getUserId(): Int = sharedPref.getInt("user_id", -1)

    fun getName(): String? = sharedPref.getString("name", "User")

    fun getRole(): String? = sharedPref.getString("role", null)

    fun getToken(): String? = sharedPref.getString("token", null)

    fun getRefreshToken(): String? = sharedPref.getString("refresh_token", null)

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean("is_logged_in", false)
    }

    fun logout() {
        sharedPref.edit().clear().apply()
    }
}
