package com.example.bknova.service

import com.example.bknova.model.RiwayatKelas
import com.example.bknova.model.Siswa
import com.example.bknova.model.WaliKelas
import retrofit2.Call
import retrofit2.http.*

interface DynamicsServices {
    // Siswa
    @GET("/api/v1/siswa")
    fun getSiswa(): Call<List<Siswa>>

    @POST("/api/v1/siswa")
    fun createSiswa(@Body siswa: Siswa): Call<Siswa>

    // Riwayat Kelas
    @POST("/api/v1/riwayat-kelas")
    fun createRiwayat(@Body riwayat: RiwayatKelas): Call<RiwayatKelas>

    // Wali Kelas
    @GET("/api/v1/wali-kelas")
    fun getWaliKelas(): Call<List<WaliKelas>>

    // Get students by Class ID
    @GET("/api/v1/siswa/kelas/{id_kelas}")
    fun getSiswaByKelas(
        @Header("Authorization") token: String,
        @Path("id_kelas") idKelas: Int
    ): Call<List<Siswa>>
}
