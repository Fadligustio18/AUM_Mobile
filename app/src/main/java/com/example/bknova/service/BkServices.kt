package com.example.bknova.service

import com.example.bknova.model.BkTask
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface BkServices {
    @POST("/api/v1/bk")
    fun createBk(@Header("Authorization") token: String, @Body request: Any): Call<ResponseBody>

    @GET("/api/v1/bk")
    fun getBkList(@Header("Authorization") token: String): Call<List<Any>>

    @POST("/api/v1/bk/tugas")
    fun createBkTask(@Header("Authorization") token: String, @Body task: BkTask): Call<BkTask>

    @GET("/api/v1/bk/tugas")
    fun getAllBkTasks(@Header("Authorization") token: String): Call<List<BkTask>>

    @GET("/api/v1/bk/tugas/me")
    fun getMyBkTasks(@Header("Authorization") token: String): Call<List<BkTask>>

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
