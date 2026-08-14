package com.example.bknova.controller

import com.example.bknova.model.BidangMasalah
import com.example.bknova.model.HasilAum
import com.example.bknova.model.SoalMasalah
import com.example.bknova.model.AumSubmitRequest
import com.example.bknova.model.AumResponse
import com.example.bknova.model.AumStatusResponse
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

    fun submitAum(token: String, request: AumSubmitRequest, callback: AumCallback<String>) {
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        
        Aktor.aum.submitAum(bearerToken, request).enqueue(object : Callback<AumResponse<String>> {
            override fun onResponse(
                call: Call<AumResponse<String>>,
                response: Response<AumResponse<String>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.status == "success") {
                        callback.onSuccess(body.message)
                    } else {
                        callback.onError(body?.message ?: "Gagal submit AUM")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    callback.onError("Error ${response.code()}: $errorBody")
                }
            }

            override fun onFailure(call: Call<AumResponse<String>>, t: Throwable) {
                callback.onError("Kesalahan jaringan: ${t.message}")
            }
        })
    }

    fun checkAumStatus(token: String, userId: Int, callback: AumCallback<Boolean>) {
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        
        Aktor.aum.getAumStatus(bearerToken, userId).enqueue(object : Callback<AumStatusResponse> {
            override fun onResponse(call: Call<AumStatusResponse>, response: Response<AumStatusResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()?.submitted ?: false)
                } else {
                    callback.onError("Gagal cek status: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AumStatusResponse>, t: Throwable) {
                callback.onError("Kesalahan jaringan: ${t.message}")
            }
        })
    }
}
