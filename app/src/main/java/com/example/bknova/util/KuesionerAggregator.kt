package com.example.bknova.util

import com.example.bknova.model.*

object KuesionerAggregator {
    fun aggregate(
        kuesionerDetail: KuesionerDetail,
        allJawaban: List<JawabanSiswaDetail>
    ): List<QuestionReport> {
        return kuesionerDetail.soal.map { soal ->
            // Filter jawaban yang pertanyaannya sama dengan soal ini
            val matchingAnswers = allJawaban.filter { it.pertanyaan == soal.pertanyaan }
            
            if (soal.tipe == "Pilihan Ganda" || soal.tipe == "Sosiometrik") {
                // Untuk pilihan ganda, kita hitung frekuensi per opsi yang ada di master soal
                val stats = soal.opsi.map { opsi ->
                    val count = matchingAnswers.count { it.jawabanPG == opsi.teks }
                    val percentage = if (matchingAnswers.isNotEmpty()) {
                        (count.toFloat() / matchingAnswers.size) * 100f
                    } else 0f
                    PieEntryData(opsi.teks, count, percentage)
                }
                QuestionReport(
                    pertanyaan = soal.pertanyaan,
                    tipe = soal.tipe,
                    pgStats = stats,
                    totalResponden = matchingAnswers.size
                )
            } else {
                // Untuk esai, kita kumpulkan semua jawaban teksnya
                val esaiList = matchingAnswers.mapNotNull { it.jawabanEsai }
                QuestionReport(
                    pertanyaan = soal.pertanyaan,
                    tipe = soal.tipe,
                    esaiAnswers = esaiList,
                    totalResponden = matchingAnswers.size
                )
            }
        }
    }
}
