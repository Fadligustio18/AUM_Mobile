package com.example.bknova.service

import com.example.bknova.model.RiwayatKelas
import com.example.bknova.model.Siswa
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface DynamicsServices {
    // Siswa
    @GET("/api/v1/siswa")
    fun getSiswa(): Call<List<Siswa>>

    @POST("/api/v1/siswa")
    fun createSiswa(@Header("Authorization") token: String, @Body siswa: Siswa): Call<Siswa>

    @GET("/api/v1/siswa/{id}")
    fun getSiswaById(@Header("Authorization") token: String, @Path("id") id: Int): Call<Siswa>

    @PATCH("/api/v1/siswa/{id}")
    fun updateSiswa(@Header("Authorization") token: String, @Path("id") id: Int, @Body siswa: Siswa): Call<Siswa>

    @DELETE("/api/v1/siswa/{id}")
    fun deleteSiswa(@Header("Authorization") token: String, @Path("id") id: Int): Call<Void>

    @POST("/api/v1/siswa/import")
    fun importSiswa(@Header("Authorization") token: String, @Body siswaList: List<Siswa>): Call<ResponseBody>

    // Get students by Class ID (Duplicate from AcademicServices, but keeping for compatibility)
    @GET("/api/v1/siswa/kelas/{id_kelas}")
    fun getSiswaByKelas(
        @Header("Authorization") token: String,
        @Path("id_kelas") idKelas: Int
    ): Call<List<Siswa>>

    // Riwayat Kelas Siswa (Aligned with Swagger: /api/v1/riwayat-kelas-siswa)
    @POST("/api/v1/riwayat-kelas-siswa")
    fun createRiwayat(@Header("Authorization") token: String, @Body riwayat: RiwayatKelas): Call<RiwayatKelas>

    @GET("/api/v1/riwayat-kelas-siswa")
    fun getRiwayatKelas(): Call<List<RiwayatKelas>>

    @GET("/api/v1/riwayat-kelas-siswa/{id}")
    fun getRiwayatById(@Path("id") id: Int): Call<RiwayatKelas>

    @PATCH("/api/v1/riwayat-kelas-siswa/{id}")
    fun updateRiwayat(@Header("Authorization") token: String, @Path("id") id: Int, @Body riwayat: RiwayatKelas): Call<RiwayatKelas>

    @DELETE("/api/v1/riwayat-kelas-siswa/{id}")
    fun deleteRiwayat(@Header("Authorization") token: String, @Path("id") id: Int): Call<Void>

    @POST("/api/v1/riwayat-kelas-siswa/promote-kelas")
    fun promoteKelas(@Header("Authorization") token: String, @Body request: Any): Call<ResponseBody>
}
