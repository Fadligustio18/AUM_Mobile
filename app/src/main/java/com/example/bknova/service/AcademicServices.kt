package com.example.bknova.service

import com.example.bknova.model.Jurusan
import com.example.bknova.model.Kelas
import com.example.bknova.model.Siswa
import com.example.bknova.model.TahunAjaran
import retrofit2.Call
import retrofit2.http.*

interface AcademicServices {
    // Jurusan
    @POST("/api/v1/jurusan")
    fun createJurusan(@Header("Authorization") token: String, @Body jurusan: Jurusan): Call<Jurusan>

    @GET("/api/v1/jurusan")
    fun getJurusan(): Call<List<Jurusan>>

    @GET("/api/v1/jurusan/{id}")
    fun getJurusanById(@Path("id") id: Int): Call<Jurusan>

    @PATCH("/api/v1/jurusan/{id}")
    fun updateJurusan(@Header("Authorization") token: String, @Path("id") id: Int, @Body jurusan: Jurusan): Call<Jurusan>

    @DELETE("/api/v1/jurusan/{id}")
    fun deleteJurusan(@Header("Authorization") token: String, @Path("id") id: Int): Call<Void>

    // Kelas
    @POST("/api/v1/kelas")
    fun createKelas(@Header("Authorization") token: String, @Body kelas: Kelas): Call<Kelas>

    @GET("/api/v1/kelas")
    fun getKelas(): Call<List<Kelas>>

    @GET("/api/v1/kelas/{id}")
    fun getKelasById(@Path("id") id: Int): Call<Kelas>

    @PATCH("/api/v1/kelas/{id}")
    fun updateKelas(@Header("Authorization") token: String, @Path("id") id: Int, @Body kelas: Kelas): Call<Kelas>

    @DELETE("/api/v1/kelas/{id}")
    fun deleteKelas(@Header("Authorization") token: String, @Path("id") id: Int): Call<Void>

    // Siswa per Kelas (Sesuai Swagger: /api/v1/siswa/kelas/{id})
    @GET("/api/v1/siswa/kelas/{id}")
    fun getSiswaKelas(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<List<Siswa>> // Diubah ke List<Siswa> agar lebih umum

    // Tahun Ajaran
    @POST("/api/v1/tahun-ajaran")
    fun createTahunAjaran(@Header("Authorization") token: String, @Body tahunAjaran: TahunAjaran): Call<TahunAjaran>

    @GET("/api/v1/tahun-ajaran")
    fun getTahunAjaran(): Call<List<TahunAjaran>>

    @GET("/api/v1/tahun-ajaran/{id}")
    fun getTahunAjaranById(@Path("id") id: Int): Call<TahunAjaran>

    @PATCH("/api/v1/tahun-ajaran/{id}")
    fun updateTahunAjaran(@Header("Authorization") token: String, @Path("id") id: Int, @Body tahunAjaran: TahunAjaran): Call<TahunAjaran>

    @DELETE("/api/v1/tahun-ajaran/{id}")
    fun deleteTahunAjaran(@Header("Authorization") token: String, @Path("id") id: Int): Call<Void>
}
