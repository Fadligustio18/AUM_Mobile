package com.example.bknova.service

import com.example.bknova.model.*
import retrofit2.Call
import retrofit2.http.*

interface SociografikServices {
    @GET("/api/v1/sociografik/questions")
    fun getQuestions(
        @Header("Authorization") token: String
    ): Call<List<SociografikQuestion>>

    @POST("/api/v1/sociografik/submit")
    fun submitAnswers(
        @Header("Authorization") token: String,
        @Body submission: SociografikSubmission
    ): Call<AumResponse<String>>

    // Guru BK CRUD Questions
    @POST("/api/v1/sociografik/questions")
    fun createQuestion(
        @Header("Authorization") token: String,
        @Body question: SociografikQuestion
    ): Call<SociografikQuestion>

    @PUT("/api/v1/sociografik/questions/{id}")
    fun updateQuestion(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body question: SociografikQuestion
    ): Call<SociografikQuestion>

    @DELETE("/api/v1/sociografik/questions/{id}")
    fun deleteQuestion(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Void>
}
