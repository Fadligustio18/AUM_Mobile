package com.example.bknova.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Tiket(
    @SerializedName("id") val id: Int,
    @SerializedName("idSiswa", alternate = ["id_siswa", "userId", "siswa_id", "Id_Siswa", "Id_User", "siswaId"]) val idSiswa: Int? = null,
    @SerializedName("siswa") val siswa: String? = null,
    @SerializedName("bk") val bk: String? = null,
    @SerializedName("tingkat", alternate = ["Tingkat"]) val tingkat: String? = null,
    @SerializedName("kelas", alternate = ["Kelas"]) val kelas: String? = null,
    @SerializedName("jurusan", alternate = ["Jurusan"]) val jurusan: String? = null,
    @SerializedName("judul") val judul: String,
    @SerializedName("isi") val isi: String,
    @SerializedName("status") val status: String,
    @SerializedName("tempat") val tempat: String?,
    @SerializedName("tanggal_Perjanjian") val tanggalPerjanjian: String?,
    @SerializedName("tanggal_Pembuatan") val tanggalPembuatan: String
) : Serializable

data class StatusTiket(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String
)

data class TiketApproveRequest(
    @SerializedName("tempat") val tempat: String,
    @SerializedName("tanggalPerjanjian") val tanggalPerjanjian: String
)

data class TiketUpdateLokasiRequest(
    @SerializedName("tempat") val tempat: String
)

data class TiketTundaRequest(
    @SerializedName("tempat") val tempat: String,
    @SerializedName("tanggalPerjanjian") val tanggalPerjanjian: String
)

data class TiketPengajuanRequest(
    @SerializedName("Judul") val judul: String,
    @SerializedName("Isi") val isi: String
)
