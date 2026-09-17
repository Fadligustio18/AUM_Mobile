package com.example.bknova.service

import com.example.bknova.model.ChangePasswordRequest
import com.example.bknova.model.ChangePasswordResponse
import com.example.bknova.model.Login
import com.example.bknova.model.LoginFeedback
import com.example.bknova.model.UserResponse
import com.example.bknova.model.FcmTokenRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthServices {
    @POST("/api/v1/auth/register-admin")
    fun registerAdmin(@Body request: Any): Call<ResponseBody>

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

    @PUT("/api/v1/auth/fcm-token")
    fun updateFcmToken(
        @Header("Authorization") token: String,
        @Body request: FcmTokenRequest
    ): Call<ResponseBody>
}
