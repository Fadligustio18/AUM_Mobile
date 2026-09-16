package com.example.bknova.service

import com.example.bknova.model.BkTask
import com.example.bknova.model.PaginatedResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface BkServices {
    @POST("/api/v1/bk")
    fun createBk(@Header("Authorization") token: String, @Body request: Any): Call<ResponseBody>

    @GET("/api/v1/bk")
    fun getBkList(@Header("Authorization") token: String): Call<List<Any>>

    @GET("/api/v1/bk/paged")
    fun getBkListPaged(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Call<PaginatedResponse<Any>>

    @POST("/api/v1/bk/tugas")
    fun createBkTask(@Header("Authorization") token: String, @Body task: BkTask): Call<BkTask>

    @GET("/api/v1/bk/tugas")
    fun getAllBkTasks(@Header("Authorization") token: String): Call<List<BkTask>>

    @GET("/api/v1/bk/tugas/paged")
    fun getAllBkTasksPaged(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Call<PaginatedResponse<BkTask>>

    @GET("/api/v1/bk/tugas/me")
    fun getMyBkTasks(@Header("Authorization") token: String): Call<List<BkTask>>

    @GET("/api/v1/bk/tugas/me/paged")
    fun getMyBkTasksPaged(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Call<PaginatedResponse<BkTask>>

    @PATCH("/api/v1/bk/tugas/{id}")
    fun updateBkTask(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body task: BkTask
    ): Call<BkTask>

    @DELETE("/api/v1/bk/tugas/{id}")
    fun deleteBkTask(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Void>
}
