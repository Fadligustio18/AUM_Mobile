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

data class UserResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String,
    @SerializedName("is_Active") val isActive: Boolean,
    @SerializedName("role") val role: String,
    @SerializedName("profile") val profile: UserProfile?
)

data class UserProfile(
    @SerializedName("id") val id: Int,
    @SerializedName("id_User") val idUser: Int,
    @SerializedName("kelas") val kelas: String? = null,
    @SerializedName("tahunAjaran") val tahunAjaran: String? = null,
    @SerializedName("tingkat") val tingkat: String? = null,
    @SerializedName("nisn") val nisn: String? = null,
    @SerializedName("nis") val nis: String? = null,
    @SerializedName("jenis_Kelamin") val jenisKelamin: String? = null,
    @SerializedName("tempat_Tanggal_Lahir") val tempatTanggalLahir: String? = null,
    @SerializedName("jurusan") val jurusan: String? = null
)

data class ChangePasswordRequest(
    @SerializedName("old_Password") val oldPassword: String,
    @SerializedName("new_Password") val newPassword: String
)

data class ChangePasswordResponse(
    @SerializedName("message") val message: String
)
