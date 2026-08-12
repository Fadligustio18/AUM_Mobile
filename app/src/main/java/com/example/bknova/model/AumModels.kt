package com.example.bknova.model

import com.google.gson.annotations.SerializedName

// Bidang Masalah
data class BidangMasalah(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_bidang") val namaBidang: String,
    @SerializedName("deskripsi") val deskripsi: String?
)

// Soal Masalah
data class SoalMasalah(
    @SerializedName("id") val id: Int,
    @SerializedName("kode") val kode: String,
    @SerializedName("bidangMasalah") val bidangMasalah: String,
    @SerializedName("pertanyaan") val pertanyaan: String
)

// Hasil AUM
data class HasilAum(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("siswa_id") val siswaId: Int,
    @SerializedName("soal_id") val soalId: Int,
    @SerializedName("jawaban") val jawaban: String, // Misal "Ya" / "Tidak" atau kode 1-10
    @SerializedName("tanggal_isi") val tanggalIsi: String? = null
)

// Response Wrappers (Opsional, tergantung format JSON backend)
data class AumResponse<T>(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T
)

// Request for AUM Submission
data class AumSubmitRequest(
    @SerializedName("Id_User") val idUser: Int,
    @SerializedName("Id_Tahun_Ajaran") val idTahunAjaran: Int,
    @SerializedName("Id_Soal_Masalah_Terpilih") val idSoalMasalahTerpilih: List<Int>
)
