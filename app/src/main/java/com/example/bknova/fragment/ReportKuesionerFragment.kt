package com.example.bknova.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.adapter.ReportKuesionerAdapter
import com.example.bknova.model.*
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import com.example.bknova.util.KuesionerAggregator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportKuesionerFragment : Fragment() {
    private lateinit var rv: RecyclerView
    private lateinit var pb: ProgressBar
    private lateinit var tvJudul: TextView
    private lateinit var tvKelas: TextView
    private lateinit var tvResponden: TextView
    private lateinit var tvTotalSiswa: TextView
    private lateinit var scrollReport: View
    private lateinit var tvError: TextView
    
    private lateinit var adapter: ReportKuesionerAdapter
    private lateinit var sessionManager: SessionManager
    
    private var kuesionerId: Int = -1
    private var idKelas: Int = -1
    private var namaKelas: String = "-"
    
    private var allJawaban = mutableListOf<JawabanSiswaDetail>()
    private var kuesionerDetail: KuesionerDetail? = null

    companion object {
        fun newInstance(kuesionerId: Int, idKelas: Int, namaKelas: String) = ReportKuesionerFragment().apply {
            arguments = Bundle().apply {
                putInt("kuesioner_id", kuesionerId)
                putInt("id_kelas", idKelas)
                putString("nama_kelas", namaKelas)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kuesionerId = arguments?.getInt("kuesioner_id") ?: -1
        idKelas = arguments?.getInt("id_kelas") ?: -1
        namaKelas = arguments?.getString("nama_kelas") ?: "-"
    }

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report_kuesioner, container, false)
        
        sessionManager = SessionManager(requireContext())
        rv = view.findViewById(R.id.rv_report_items)
        pb = view.findViewById(R.id.pb_report)
        tvJudul = view.findViewById(R.id.tv_report_judul)
        tvKelas = view.findViewById(R.id.tv_report_kelas)
        tvResponden = view.findViewById(R.id.tv_total_responden)
        tvTotalSiswa = view.findViewById(R.id.tv_total_siswa)
        scrollReport = view.findViewById(R.id.scroll_report)
        tvError = view.findViewById(R.id.tv_error_report)
        
        tvKelas.text = "Kelas: $namaKelas"
        
        val btnBack = view.findViewById<ImageView>(R.id.btn_back_report)
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        
        rv.layoutManager = LinearLayoutManager(context)
        adapter = ReportKuesionerAdapter(emptyList())
        rv.adapter = adapter
        
        loadData()
        
        return view
    }

    private fun loadData() {
        if (kuesionerId == -1 || idKelas == -1) {
            showError("Parameter tidak valid")
            return
        }

        pb.visibility = View.VISIBLE
        scrollReport.visibility = View.GONE
        tvError.visibility = View.GONE
        
        val token = "Bearer ${sessionManager.getToken()}"
        
        // 1. Ambil detail kuesioner untuk mengetahui struktur pertanyaan
        Aktor.kuesioner.getKuesionerDetailBk(token, kuesionerId).enqueue(object : Callback<KuesionerDetail> {
            override fun onResponse(call: Call<KuesionerDetail>, response: Response<KuesionerDetail>) {
                if (response.isSuccessful) {
                    kuesionerDetail = response.body()
                    tvJudul.text = kuesionerDetail?.judul ?: "Laporan Kuesioner"
                    
                    // 2. Langsung ambil data siswa kelas untuk diproses jawabannya
                    loadStudentsOfClass()
                } else {
                    showError("Gagal mengambil detail kuesioner: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<KuesionerDetail>, t: Throwable) {
                showError("Gagal mengambil detail kuesioner: ${t.message}")
            }
        })
    }

    private fun loadStudentsOfClass() {
        val token = "Bearer ${sessionManager.getToken()}"
        Aktor.dynamics.getSiswaByKelas(token, idKelas).enqueue(object : Callback<List<Siswa>> {
            override fun onResponse(call: Call<List<Siswa>>, response: Response<List<Siswa>>) {
                if (response.isSuccessful) {
                    val students = response.body() ?: emptyList()
                    tvTotalSiswa.text = students.size.toString()
                    
                    if (students.isEmpty()) {
                        finalizeReport()
                        return
                    }
                    
                    // 3. Ambil jawaban dari setiap siswa di kelas ini
                    fetchAllAnswers(students)
                } else {
                    showError("Gagal mengambil data siswa kelas: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<Siswa>>, t: Throwable) {
                showError("Gagal mengambil data siswa kelas: ${t.message}")
            }
        })
    }

    private fun fetchAllAnswers(students: List<Siswa>) {
        val token = "Bearer ${sessionManager.getToken()}"
        var completedCount = 0
        var respondenCount = 0
        allJawaban.clear()
        
        students.forEach { siswa ->
            val idSiswa = siswa.idSiswa ?: -1
            if (idSiswa != -1) {
                Aktor.kuesioner.getJawabanSiswa(token, kuesionerId, idSiswa).enqueue(object : Callback<List<JawabanSiswaDetail>> {
                    override fun onResponse(call: Call<List<JawabanSiswaDetail>>, response: Response<List<JawabanSiswaDetail>>) {
                        if (response.isSuccessful) {
                            val jawaban = response.body()
                            if (!jawaban.isNullOrEmpty()) {
                                allJawaban.addAll(jawaban)
                                respondenCount++
                            }
                        }
                        checkCompletion()
                    }
                    override fun onFailure(call: Call<List<JawabanSiswaDetail>>, t: Throwable) {
                        checkCompletion()
                    }
                    
                    private fun checkCompletion() {
                        completedCount++
                        if (completedCount == students.size) {
                            tvResponden.text = respondenCount.toString()
                            finalizeReport()
                        }
                    }
                })
            } else {
                completedCount++
                if (completedCount == students.size) {
                    tvResponden.text = respondenCount.toString()
                    finalizeReport()
                }
            }
        }
    }

    private fun finalizeReport() {
        if (!isAdded) return
        
        val detail = kuesionerDetail
        if (detail != null) {
            val reportItems = KuesionerAggregator.aggregate(detail, allJawaban)
            adapter.updateData(reportItems)
        }
        
        pb.visibility = View.GONE
        scrollReport.visibility = View.VISIBLE
    }

    private fun showError(msg: String) {
        if (!isAdded) return
        pb.visibility = View.GONE
        tvError.visibility = View.VISIBLE
        tvError.text = msg
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
