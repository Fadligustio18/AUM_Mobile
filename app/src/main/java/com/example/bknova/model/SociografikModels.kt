package com.example.bknova.model

import com.google.gson.annotations.SerializedName

data class SociografikQuestion(
    @SerializedName("id") val id: Int,
    @SerializedName("pertanyaan") val pertanyaan: String,
    @SerializedName("pilihan") val pilihan: List<String>? = null
)

data class SociografikAnswer(
    @SerializedName("questionId") val questionId: Int,
    @SerializedName("jawaban") val jawaban: String
)

data class SociografikSubmission(
    @SerializedName("idUser") val idUser: Int,
    @SerializedName("answers") val answers: List<SociografikAnswer>
)
