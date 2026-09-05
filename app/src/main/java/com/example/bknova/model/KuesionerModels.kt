package com.example.bknova.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class KuesionerSummary(
    @SerializedName("id") val id: Int,
    @SerializedName("judul") val judul: String,
    @SerializedName("deskripsi") val deskripsi: String,
    @SerializedName("id_Kelas", alternate = ["idKelas", "id_kelas", "Id_Kelas", "kelas_id", "id_target_kelas"]) val idKelas: Int? = null,
    @SerializedName("kelas") val kelas: String,
    @SerializedName("tahun_Ajaran", alternate = ["tahunAjaran", "tahun_ajaran", "Tahun_Ajaran"]) val tahunAjaran: String,
    @SerializedName("created_At", alternate = ["createdAt", "created_at"]) val createdAt: String,
    @SerializedName("sudah_Submit") val sudahSubmit: Boolean = false
) : Serializable

data class KuesionerDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("judul") val judul: String,
    @SerializedName("deskripsi") val deskripsi: String,
    @SerializedName("soal") val soal: List<SoalKuesioner>
) : Serializable

data class SoalKuesioner(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("pertanyaan", alternate = ["Pertanyaan"]) val pertanyaan: String,
    @SerializedName("tipe", alternate = ["Tipe"]) val tipe: String, // "Pilihan Ganda" or "Esai"
    @SerializedName("urutan", alternate = ["Urutan"]) val urutan: Int,
    @SerializedName("opsi", alternate = ["Opsi"]) val opsi: List<OpsiKuesioner>
) : Serializable

data class OpsiKuesioner(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("teks", alternate = ["Teks"]) val teks: String,
    @SerializedName("urutan", alternate = ["Urutan"]) val urutan: Int
) : Serializable

data class KuesionerCreateRequest(
    @SerializedName("judul") val judul: String,
    @SerializedName("deskripsi") val deskripsi: String,
    @SerializedName("id_Kelas", alternate = ["Id_Kelas", "id_kelas"]) val idKelas: Int,
    @SerializedName("id_Tahun_Ajaran", alternate = ["Id_Tahun_Ajaran", "id_tahun_ajaran"]) val idTahunAjaran: Int,
    @SerializedName("soal") val soal: List<SoalKuesioner>
)

data class JawabanSubmitRequest(
    @SerializedName("id_Soal") val idSoal: Int,
    @SerializedName("id_Opsi") val idOpsi: Int? = null,
    @SerializedName("teks_Jawaban") val teksJawaban: String? = null
)

data class JawabanSiswaDetail(
    @SerializedName("pertanyaan") val pertanyaan: String,
    @SerializedName("tipe") val tipe: String,
    @SerializedName("jawaban_PG") val jawabanPG: String? = null,
    @SerializedName("jawaban_Esai") val jawabanEsai: String? = null
)

data class RespondenKuesioner(
    @SerializedName("id_siswa") val idSiswa: Int,
    @SerializedName("nama_siswa") val nama: String,
    @SerializedName("nisn") val nisn: String,
    @SerializedName("sudah_mengerjakan") val sudahMengerjakan: Boolean
)
