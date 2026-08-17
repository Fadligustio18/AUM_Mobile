package com.example.bknova.service

import com.example.bknova.model.ChangePasswordRequest
import com.example.bknova.model.ChangePasswordResponse
import com.example.bknova.model.Login
import com.example.bknova.model.LoginFeedback
import com.example.bknova.model.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthServices {
    @POST("/api/v1/auth/login")
    fun Login_Services(@Body login: Login): Call<LoginFeedback>

    @POST("/api/v1/auth/refresh")
    fun refreshToken(@Header("Authorization") refreshToken: String): Call<LoginFeedback>

    @GET("/api/v1/auth/me")
    fun getMe(@Header("Authorization") token: String): Call<UserResponse>

    @PATCH("/api/v1/auth/change-password")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Call<ChangePasswordResponse>
}
