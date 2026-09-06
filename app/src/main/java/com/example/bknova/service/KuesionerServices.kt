package com.example.bknova.service

import com.example.bknova.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface KuesionerServices {
    // Guru BK Endpoints
    @POST("/api/v1/kuesioner/{IdUser}")
    fun createKuesioner(
        @Header("Authorization") token: String,
        @Path("IdUser") idUser: Int,
        @Body request: KuesionerCreateRequest
    ): Call<ResponseBody>

    @GET("/api/v1/kuesioner/bk/{IdUser}")
    fun getKuesionerBk(
        @Header("Authorization") token: String,
        @Path("IdUser") idUser: Int
    ): Call<List<KuesionerSummary>>

    @GET("/api/v1/kuesioner/bk/detail/{IdKuesioner}")
    fun getKuesionerDetailBk(
        @Header("Authorization") token: String,
        @Path("IdKuesioner") idKuesioner: Int
    ): Call<KuesionerDetail>

    @GET("/api/v1/kuesioner/bk/responden/{IdKuesioner}")
    fun getRespondenKuesioner(
        @Header("Authorization") token: String,
        @Path("IdKuesioner") idKuesioner: Int
    ): Call<List<RespondenKuesioner>>

    @GET("/api/v1/kuesioner/bk/jawaban/{IdKuesioner}/{IdSiswa}")
    fun getJawabanSiswa(
        @Header("Authorization") token: String,
        @Path("IdKuesioner") idKuesioner: Int,
        @Path("IdSiswa") idSiswa: Int
    ): Call<List<JawabanSiswaDetail>>

    // Siswa Endpoints
    @GET("/api/v1/kuesioner/siswa/{IdUser}")
    fun getKuesionerSiswa(
        @Header("Authorization") token: String,
        @Path("IdUser") idUser: Int
    ): Call<List<KuesionerSummary>>

    @GET("/api/v1/kuesioner/siswa/detail/{IdKuesioner}")
    fun getKuesionerDetailSiswa(
        @Header("Authorization") token: String,
        @Path("IdKuesioner") idKuesioner: Int
    ): Call<KuesionerDetail>

    @POST("/api/v1/kuesioner/siswa/submit/{IdUser}/{IdKuesioner}")
    fun submitKuesioner(
        @Header("Authorization") token: String,
        @Path("IdUser") idUser: Int,
        @Path("IdKuesioner") idKuesioner: Int,
        @Body jawaban: List<JawabanSubmitRequest>
    ): Call<ResponseBody>
}
