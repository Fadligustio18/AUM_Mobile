package com.example.bknova.model

data class QuestionReport(
    val pertanyaan: String,
    val tipe: String, // "Pilihan Ganda" or "Esai"
    val pgStats: List<PieEntryData>? = null,
    val esaiAnswers: List<String>? = null,
    val totalResponden: Int
)

data class PieEntryData(
    val label: String,
    val count: Int,
    val percentage: Float
)

data class KuesionerReportSummary(
    val judul: String,
    val kelas: String,
    val totalSiswa: Int,
    val totalMengerjakan: Int,
    val reports: List<QuestionReport>
)
