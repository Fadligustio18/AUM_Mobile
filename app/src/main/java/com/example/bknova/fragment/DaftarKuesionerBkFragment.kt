package com.example.bknova.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.adapter.KuesionerAdapter
import com.example.bknova.model.BkTask
import com.example.bknova.model.KuesionerSummary
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarKuesionerBkFragment : Fragment() {
    private lateinit var rv: RecyclerView
    private lateinit var adapter: KuesionerAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var btnBack: ImageView
    private lateinit var fabAdd: FloatingActionButton
    private var listKelasBk = listOf<BkTask>()

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daftar_kuesioner_bk, container, false)
        
        sessionManager = SessionManager(requireContext())
        rv = view.findViewById(R.id.rv_kuesioner)
        btnBack = view.findViewById(R.id.btn_back_kuesioner)
        fabAdd = view.findViewById(R.id.fab_add_kuesioner)
        
        rv.layoutManager = LinearLayoutManager(context)
        adapter = KuesionerAdapter(
            list = emptyList(),
            onItemClick = { kuesioner ->
                handleKuesionerClick(kuesioner)
            }
        )
        rv.adapter = adapter
        
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        
        fabAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_bk, CreateKuesionerFragment())
                .addToBackStack(null)
                .commit()
        }
        
        loadKelasAndData()
        
        return view
    }

    private fun loadKelasAndData() {
        val token = "Bearer ${sessionManager.getToken()}"
        
        // Pertama, ambil daftar kelas Guru BK untuk referensi ID
        Aktor.academic.getMyTasks(token).enqueue(object : Callback<List<BkTask>> {
            override fun onResponse(call: Call<List<BkTask>>, response: Response<List<BkTask>>) {
                if (response.isSuccessful) {
                    listKelasBk = response.body() ?: emptyList()
                }
                // Setelah daftar kelas siap, baru ambil daftar kuesioner
                loadKuesionerList()
            }
            override fun onFailure(call: Call<List<BkTask>>, t: Throwable) {
                loadKuesionerList()
            }
        })
    }

    private fun loadKuesionerList() {
        val token = "Bearer ${sessionManager.getToken()}"
        val idUser = sessionManager.getUserId()
        
        Aktor.kuesioner.getKuesionerBk(token, idUser).enqueue(object : Callback<List<KuesionerSummary>> {
            override fun onResponse(call: Call<List<KuesionerSummary>>, response: Response<List<KuesionerSummary>>) {
                if (response.isSuccessful) {
                    response.body()?.let { adapter.updateData(it) }
                }
            }
            override fun onFailure(call: Call<List<KuesionerSummary>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleKuesionerClick(kuesioner: KuesionerSummary) {
        var classId = kuesioner.idKelas ?: -1
        
        // Fallback: Jika ID Kelas kosong, cari berdasarkan nama kelas (misal: "12 PPLG 1")
        if (classId == -1) {
            val match = listKelasBk.find { "${it.tingkat} ${it.namaKelas}".equals(kuesioner.kelas, ignoreCase = true) }
            if (match != null) {
                classId = match.idKelas
            }
        }

        if (classId != -1) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_bk, RespondenKuesionerFragment.newInstance(kuesioner.id, classId))
                .addToBackStack(null)
                .commit()
        } else {
            Toast.makeText(context, "ID Kelas untuk '${kuesioner.kelas}' tidak ditemukan di data tugas Anda", Toast.LENGTH_LONG).show()
        }
    }
}
