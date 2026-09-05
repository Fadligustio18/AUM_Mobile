package com.example.bknova.service

import com.example.bknova.model.BidangMasalah
import com.example.bknova.model.HasilAum
import com.example.bknova.model.SoalMasalah
import com.example.bknova.model.AumSubmitRequest
import com.example.bknova.model.AumResponse
import com.example.bknova.model.AumStatusResponse
import com.example.bknova.model.AumHasilSiswa
import retrofit2.Call
import retrofit2.http.*

interface AumServices {
    // Bidang Masalah
    @GET("/api/v1/bidang-masalah")
    fun getBidangMasalah(): Call<List<BidangMasalah>>

    // Soal Masalah (Sesuaikan dengan screenshot: /api/v1/soal-masalah)
    @GET("/api/v1/soal-masalah")
    fun getSoalMasalah(): Call<List<SoalMasalah>>

    @GET("/api/v1/soal-masalah/{id}")
    fun getSoalByBidang(@Path("id") id: Int): Call<List<SoalMasalah>>

    // Hasil AUM
    @POST("/api/v1/hasil")
    fun createHasilAum(@Body hasil: HasilAum): Call<HasilAum>

    @GET("/api/v1/hasil")
    fun getHasilAum(): Call<List<HasilAum>>

    @POST("/api/v1/aum/submit")
    fun submitAum(
        @Header("Authorization") token: String,
        @Body request: AumSubmitRequest
    ): Call<AumResponse<String>>

    @GET("/api/v1/aum/status/{idUser}")
    fun getAumStatus(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int
    ): Call<AumStatusResponse>

    @GET("/api/v1/aum/hasil/{idGuru}")
    fun getHasilAumByGuru(
        @Header("Authorization") token: String,
        @Path("idGuru") idGuru: Int
    ): Call<List<AumHasilSiswa>>

    @GET("/api/v1/aum/hasil-siswa/{nisn}")
    fun getHasilAumByNisn(
        @Header("Authorization") token: String,
        @Path("nisn") nisn: String
    ): Call<AumHasilSiswa>

    @GET("/api/v1/aum/hasil-siswa-id/{idSiswa}")
    fun getHasilAumBySiswaId(
        @Header("Authorization") token: String,
        @Path("idSiswa") idSiswa: Int
    ): Call<AumHasilSiswa>
}
