package com.example.bknova.service

import com.example.bknova.model.BkTask
import com.example.bknova.model.Jurusan
import com.example.bknova.model.Kelas
import com.example.bknova.model.TahunAjaran
import retrofit2.Call
import retrofit2.http.*

interface AcademicServices {
    // Jurusan
    @GET("/api/v1/jurusan")
    fun getJurusan(): Call<List<Jurusan>>

    // Kelas
    @GET("/api/v1/kelas")
    fun getKelas(): Call<List<Kelas>>

    // Tahun Ajaran
    @GET("/api/v1/tahun-ajaran")
    fun getTahunAjaran(): Call<List<TahunAjaran>>

    // BK Tasks
    @GET("/api/v1/bk/tugas/me")
    fun getMyTasks(@Header("Authorization") token: String): Call<List<BkTask>>
}
