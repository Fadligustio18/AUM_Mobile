package com.example.bknova.model

import com.google.gson.annotations.SerializedName

// Siswa
data class Siswa(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("id_user", alternate = ["Id_User", "userId", "IdUser", "id_siswa"]) val idUser: Int? = null,
    @SerializedName("nisn") val nisn: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String? = null,
    @SerializedName("role") val role: String? = "siswa"
)

// Riwayat Kelas Siswa
data class RiwayatKelas(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("siswa_id") val siswaId: Int,
    @SerializedName("kelas_id") val kelasId: Int,
    @SerializedName("tahun_ajaran_id") val tahunAjaranId: Int,
    @SerializedName("status") val status: String? = "aktif"
)

// Wali Kelas
data class WaliKelas(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("guru_id") val guruId: Int,
    @SerializedName("kelas_id") val kelasId: Int
)
