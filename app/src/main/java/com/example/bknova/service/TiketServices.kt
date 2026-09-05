package com.example.bknova.service

import com.example.bknova.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TiketServices {
    // Guru BK Endpoints
    @GET("/api/v1/tiket/bk/{idUser}")
    fun getTiketBk(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int
    ): Call<List<Tiket>>

    @PATCH("/api/v1/tiket/bk/setujui/{idTiket}")
    fun setujuiTiket(
        @Header("Authorization") token: String,
        @Path("idTiket") idTiket: Int,
        @Body request: TiketApproveRequest
    ): Call<ResponseBody>

    @PATCH("/api/v1/tiket/bk/lokasi/{idTiket}")
    fun updateLokasiTiket(
        @Header("Authorization") token: String,
        @Path("idTiket") idTiket: Int,
        @Body request: TiketUpdateLokasiRequest
    ): Call<ResponseBody>

    @PATCH("/api/v1/tiket/bk/tunda/{idTiket}")
    fun tundaTiket(
        @Header("Authorization") token: String,
        @Path("idTiket") idTiket: Int,
        @Body request: TiketTundaRequest
    ): Call<ResponseBody>

    @PATCH("/api/v1/tiket/bk/batalkan/{idTiket}")
    fun batalkanTiket(
        @Header("Authorization") token: String,
        @Path("idTiket") idTiket: Int
    ): Call<ResponseBody>

    @PATCH("/api/v1/tiket/bk/selesai/{idTiket}")
    fun selesaiTiket(
        @Header("Authorization") token: String,
        @Path("idTiket") idTiket: Int
    ): Call<ResponseBody>

    // Siswa Endpoints
    @POST("/api/v1/tiket/request/{idUser}")
    fun ajukanTiket(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int,
        @Body request: TiketPengajuanRequest
    ): Call<ResponseBody>

    @PATCH("/api/v1/tiket/{idTiket}")
    fun editTiket(
        @Header("Authorization") token: String,
        @Path("idTiket") idTiket: Int,
        @Body request: TiketPengajuanRequest
    ): Call<ResponseBody>

    @DELETE("/api/v1/tiket/{idTiket}")
    fun deleteTiket(
        @Header("Authorization") token: String,
        @Path("idTiket") idTiket: Int
    ): Call<Void>

    @GET("/api/v1/tiket/{idUser}")
    fun getTiketSiswa(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int
    ): Call<List<Tiket>>

    @GET("/api/v1/status-tiket")
    fun getStatusTiket(
        @Header("Authorization") token: String
    ): Call<List<StatusTiket>>
}
