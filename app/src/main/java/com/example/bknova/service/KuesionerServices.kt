package com.example.bknova.service

import com.example.bknova.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface KuesionerServices {
    // Guru BK Endpoints
    @POST("/api/v1/kuesioner/{idUser}")
    fun createKuesioner(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int,
        @Body request: KuesionerCreateRequest
    ): Call<ResponseBody>

    @GET("/api/v1/kuesioner/bk/{idUser}")
    fun getKuesionerBk(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int
    ): Call<List<KuesionerSummary>>

    @GET("/api/v1/kuesioner/bk/detail/{idKuesioner}")
    fun getKuesionerDetailBk(
        @Header("Authorization") token: String,
        @Path("idKuesioner") idKuesioner: Int
    ): Call<KuesionerDetail>

    @GET("/api/v1/kuesioner/bk/responden/{idKuesioner}")
    fun getRespondenKuesioner(
        @Header("Authorization") token: String,
        @Path("idKuesioner") idKuesioner: Int
    ): Call<List<RespondenKuesioner>>

    @GET("/api/v1/kuesioner/bk/jawaban/{idKuesioner}/{idSiswa}")
    fun getJawabanSiswa(
        @Header("Authorization") token: String,
        @Path("idKuesioner") idKuesioner: Int,
        @Path("idSiswa") idSiswa: Int
    ): Call<List<JawabanSiswaDetail>>

    // Siswa Endpoints
    @GET("/api/v1/kuesioner/siswa/{idUser}")
    fun getKuesionerSiswa(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int
    ): Call<List<KuesionerSummary>>

    @GET("/api/v1/kuesioner/siswa/detail/{idKuesioner}")
    fun getKuesionerDetailSiswa(
        @Header("Authorization") token: String,
        @Path("idKuesioner") idKuesioner: Int
    ): Call<KuesionerDetail>

    @POST("/api/v1/kuesioner/siswa/submit/{idUser}/{idKuesioner}")
    fun submitKuesioner(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int,
        @Path("idKuesioner") idKuesioner: Int,
        @Body jawaban: List<JawabanSubmitRequest>
    ): Call<ResponseBody>
}
