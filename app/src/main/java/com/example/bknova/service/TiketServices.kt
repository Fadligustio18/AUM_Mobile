package com.example.bknova.service

import com.example.bknova.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TiketServices {
    // Guru BK Endpoints
    @GET("/api/v1/tiket/bk/{IdUser}")
    fun getTiketBk(
        @Header("Authorization") token: String,
        @Path("IdUser") idUser: Int
    ): Call<List<Tiket>>

    @PATCH("/api/v1/tiket/bk/setujui/{IdTiket}")
    fun setujuiTiket(
        @Header("Authorization") token: String,
        @Path("IdTiket") idTiket: Int,
        @Body request: TiketApproveRequest
    ): Call<ResponseBody>

    @PATCH("/api/v1/tiket/bk/lokasi/{IdTiket}")
    fun updateLokasiTiket(
        @Header("Authorization") token: String,
        @Path("IdTiket") idTiket: Int,
        @Body request: TiketUpdateLokasiRequest
    ): Call<ResponseBody>

    @PATCH("/api/v1/tiket/bk/tunda/{IdTiket}")
    fun tundaTiket(
        @Header("Authorization") token: String,
        @Path("IdTiket") idTiket: Int,
        @Body request: TiketTundaRequest
    ): Call<ResponseBody>

    @PATCH("/api/v1/tiket/bk/batalkan/{IdTiket}")
    fun batalkanTiket(
        @Header("Authorization") token: String,
        @Path("IdTiket") idTiket: Int
    ): Call<ResponseBody>

    @PATCH("/api/v1/tiket/bk/selesai/{IdTiket}")
    fun selesaiTiket(
        @Header("Authorization") token: String,
        @Path("IdTiket") idTiket: Int
    ): Call<ResponseBody>

    // Siswa Endpoints
    @POST("/api/v1/tiket/request/{IdSiswa}")
    fun ajukanTiket(
        @Header("Authorization") token: String,
        @Path("IdSiswa") idSiswa: Int,
        @Body request: TiketPengajuanRequest
    ): Call<ResponseBody>

    @PATCH("/api/v1/tiket/{IdTiket}")
    fun editTiket(
        @Header("Authorization") token: String,
        @Path("IdTiket") idTiket: Int,
        @Body request: TiketPengajuanRequest
    ): Call<ResponseBody>

    @DELETE("/api/v1/tiket/{IdTiket}")
    fun deleteTiket(
        @Header("Authorization") token: String,
        @Path("IdTiket") idTiket: Int
    ): Call<Void>

    @GET("/api/v1/tiket/{IdUser}")
    fun getTiketSiswa(
        @Header("Authorization") token: String,
        @Path("IdUser") idUser: Int
    ): Call<List<Tiket>>

    @GET("/api/v1/status-tiket")
    fun getStatusTiket(
        @Header("Authorization") token: String
    ): Call<List<StatusTiket>>
}
