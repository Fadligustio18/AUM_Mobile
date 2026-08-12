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
    @SerializedName("tahun") val tahun: String,
    @SerializedName("semester") val semester: String
)
