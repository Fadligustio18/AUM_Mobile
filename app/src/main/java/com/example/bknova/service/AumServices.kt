package com.example.bknova.service

import com.example.bknova.model.BidangMasalah
import com.example.bknova.model.HasilAum
import com.example.bknova.model.SoalMasalah
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
}
