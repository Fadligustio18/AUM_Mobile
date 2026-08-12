package com.example.bknova.controller

import com.example.bknova.model.BidangMasalah
import com.example.bknova.model.HasilAum
import com.example.bknova.model.SoalMasalah
import com.example.bknova.service.Aktor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AumController {

    interface AumCallback<T> {
        fun onSuccess(data: T)
        fun onError(message: String)
    }

    fun fetchSoalByBidang(bidangId: Int, callback: AumCallback<List<SoalMasalah>>) {
        Aktor.aum.getSoalByBidang(bidangId).enqueue(object : Callback<List<SoalMasalah>> {
            override fun onResponse(
                call: Call<List<SoalMasalah>>,
                response: Response<List<SoalMasalah>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                        ?: callback.onError("Data kosong")
                } else {
                    callback.onError("Gagal mengambil soal bidang $bidangId")
                }
            }

            override fun onFailure(call: Call<List<SoalMasalah>>, t: Throwable) {
                callback.onError("Jaringan error: ${t.message}")
            }
        })
    }

    fun submitHasil(hasil: HasilAum, callback: AumCallback<HasilAum>) {
        Aktor.aum.createHasilAum(hasil).enqueue(object : Callback<HasilAum> {
            override fun onResponse(
                call: Call<HasilAum>,
                response: Response<HasilAum>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                        ?: callback.onError("Gagal menyimpan hasil")
                } else {
                    callback.onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<HasilAum>, t: Throwable) {
                callback.onError("Kesalahan jaringan: ${t.message}")
            }
        })
    }
}
