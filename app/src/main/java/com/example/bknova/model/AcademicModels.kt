package com.example.bknova.model

import com.google.gson.annotations.SerializedName

// Jurusan
data class Jurusan(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_jurusan") val namaJurusan: String
)

// Kelas
data class Kelas(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_kelas") val namaKelas: String,
    @SerializedName("jurusan_id") val jurusanId: Int
)

// Tahun Ajaran
data class TahunAjaran(
    @SerializedName("id") val id: Int,
    @SerializedName("tahun", alternate = ["tahun_ajaran", "tahunAjaran", "tahun_Ajaran", "Tahun_Ajaran", "TahunAjaran"]) val tahun: String?,
    @SerializedName("semester") val semester: String?
)

data class BkTask(
    @SerializedName("id") val id: Int,
    @SerializedName("id_User_BK") val idUserBk: Int,
    @SerializedName("nama_BK") val namaBk: String,
    @SerializedName("id_Kelas") val idKelas: Int,
    @SerializedName("nama_Kelas") val namaKelas: String,
    @SerializedName("tingkat") val tingkat: String,
    @SerializedName("id_Tahun_Ajaran") val idTahunAjaran: Int,
    @SerializedName("tahunAjaran") val tahunAjaran: String,
    @SerializedName("is_Active") val isActive: Boolean,
    @SerializedName("assigned_At") val assignedAt: String
)

data class SiswaKelas(
    @SerializedName("tingkat") val tingkat: String,
    @SerializedName("nama_kelas") val namaKelas: String
)
