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
import com.example.bknova.activity.halaman_siswa_Activity
import com.example.bknova.adapter.TiketAdapter
import com.example.bknova.model.Tiket
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarTiketSiswaFragment : Fragment() {
    private lateinit var rvTiket: RecyclerView
    private lateinit var adapter: TiketAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var btnBack: ImageView

    override fun onResume() {
        super.onResume()
        // Sembunyikan Bottom Navigation saat masuk ke halaman ini
        (activity as? halaman_siswa_Activity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daftar_tiket_siswa, container, false)
        
        sessionManager = SessionManager(requireContext())
        rvTiket = view.findViewById(R.id.rv_tiket_siswa)
        fabAdd = view.findViewById(R.id.fab_add_tiket)
        btnBack = view.findViewById(R.id.btn_back_tiket_siswa)
        
        rvTiket.layoutManager = LinearLayoutManager(context)
        
        adapter = TiketAdapter(emptyList()) { tiket ->
            // Siswa can also see their own ticket detail
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DetailTiketFragment.newInstance(tiket))
                .addToBackStack(null)
                .commit()
        }
        rvTiket.adapter = adapter
        
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fabAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PengajuanTiketFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        
        loadTiket()
        
        return view
    }

    private fun loadTiket() {
        val token = "Bearer ${sessionManager.getToken()}"
        val idUser = sessionManager.getUserId()
        
        Aktor.tiket.getTiketSiswa(token, idUser).enqueue(object : Callback<List<Tiket>> {
            override fun onResponse(call: Call<List<Tiket>>, response: Response<List<Tiket>>) {
                if (response.isSuccessful) {
                    response.body()?.let { adapter.updateData(it) }
                } else {
                    Toast.makeText(context, "Gagal memuat status tiket", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Tiket>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
