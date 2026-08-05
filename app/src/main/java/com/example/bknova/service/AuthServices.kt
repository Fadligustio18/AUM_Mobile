package com.example.bknova.service

import com.example.bknova.model.Login
import com.example.bknova.model.LoginFeedback
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthServices {
    @POST("/api/v1/auth/login")
    fun Login_Services(@Body login: Login): Call<LoginFeedback>
}