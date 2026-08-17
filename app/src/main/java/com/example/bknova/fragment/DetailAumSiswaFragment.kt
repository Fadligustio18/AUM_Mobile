package com.example.bknova.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.adapter.AumHasilBidangAdapter
import com.example.bknova.adapter.AumStatistikAdapter
import com.example.bknova.controller.AuthController
import com.example.bknova.model.AumHasilSiswa
import com.example.bknova.service.Aktor
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailAumSiswaFragment : Fragment() {
    private lateinit var authController: AuthController
    private lateinit var rvBidang: RecyclerView
    private lateinit var rvStatistik: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvEmpty: TextView
    
    private lateinit var tvNama: TextView
    private lateinit var tvKelas: TextView
    private lateinit var tvNis: TextView
    private lateinit var tvWaktu: TextView

    private var idSiswa: Int = -1
    private var namaSiswa: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController(requireContext())
        arguments?.let {
            idSiswa = it.getInt("id_siswa", -1)
            namaSiswa = it.getString("nama_siswa")
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_aum_siswa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar_detail_aum)
        rvBidang = view.findViewById(R.id.rv_bidang_aum)
        rvStatistik = view.findViewById(R.id.rv_statistik_aum)
        progressBar = view.findViewById(R.id.pb_detail_aum)
        tvEmpty = view.findViewById(R.id.tv_empty_detail_aum)
        
        tvNama = view.findViewById(R.id.tv_detail_nama)
        tvKelas = view.findViewById(R.id.tv_detail_kelas)
        tvNis = view.findViewById(R.id.tv_detail_nis)
        tvWaktu = view.findViewById(R.id.tv_detail_waktu)

        rvBidang.layoutManager = LinearLayoutManager(context)
        rvStatistik.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        if (idSiswa != -1) {
            fetchDetailAum()
        }
    }

    private fun fetchDetailAum() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        
        val token = authController.getToken() ?: return
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        val idGuru = authController.getUserId()

        Aktor.aum.getHasilAumByGuru(bearerToken, idGuru).enqueue(object : Callback<List<AumHasilSiswa>> {
            override fun onResponse(call: Call<List<AumHasilSiswa>>, response: Response<List<AumHasilSiswa>>) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val listHasil = response.body()
                        
                        // Cari berdasarkan ID dulu, jika gagal coba berdasarkan Nama (karena ID di DB mungkin tidak sinkron)
                        var result = listHasil?.find { it.idSiswa == idSiswa }
                        
                        if (result == null && !namaSiswa.isNullOrEmpty()) {
                            result = listHasil?.find { it.nama.equals(namaSiswa, ignoreCase = true) }
                        }
                        
                        if (result != null) {
                            displayData(result)
                        } else {
                            tvEmpty.visibility = View.VISIBLE
                            tvEmpty.text = "Data AUM tidak ditemukan untuk siswa ini"
                        }
                    } else {
                        Toast.makeText(context, "Gagal memuat data AUM: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<AumHasilSiswa>>, t: Throwable) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, "Kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun displayData(data: AumHasilSiswa) {
        tvNama.text = data.nama
        tvKelas.text = "Kelas: ${data.tingkat} ${data.kelas}"
        tvNis.text = "NIS/NISN: ${data.nis} / ${data.nisn}"
        tvWaktu.text = "Waktu Mengisi: ${data.waktuMengisi}"
        
        // Setup Detail Bidang
        val adapterBidang = AumHasilBidangAdapter(data.bidang)
        rvBidang.adapter = adapterBidang

        // Setup Statistik
        val adapterStatistik = AumStatistikAdapter(data.bidang)
        rvStatistik.adapter = adapterStatistik
    }

    companion object {
        fun newInstance(idSiswa: Int, namaSiswa: String) =
            DetailAumSiswaFragment().apply {
                arguments = Bundle().apply {
                    putInt("id_siswa", idSiswa)
                    putString("nama_siswa", namaSiswa)
                }
            }
    }
}