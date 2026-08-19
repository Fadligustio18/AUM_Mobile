package com.example.bknova.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Aktor {
    private const val BASE_URL = "http://192.168.69.50:3000/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val http: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val auth: AuthServices by lazy {
        http.create(AuthServices::class.java)
    }

    val aum: AumServices by lazy {
        http.create(AumServices::class.java)
    }

    val dynamics: DynamicsServices by lazy {
        http.create(DynamicsServices::class.java)
    }

    val academic: AcademicServices by lazy {
        http.create(AcademicServices::class.java)
    }
}