package com.example.bknova.model

import com.google.gson.annotations.SerializedName

data class Login (
    @SerializedName("Nama") val Nama: String,
    @SerializedName("Password") val Password: String
)

data class LoginFeedback(
    @SerializedName("token") val token: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("role") val role: String,
    @SerializedName("refreshToken") val refreshToken: String
)
