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
import com.example.bknova.adapter.DaftarSiswaBkAdapter
import com.example.bknova.model.Siswa
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RespondenKuesionerFragment : Fragment() {
    private lateinit var rv: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var sessionManager: SessionManager
    
    private var kuesionerId: Int = -1
    private var idKelas: Int = -1

    companion object {
        fun newInstance(kuesionerId: Int, idKelas: Int) = RespondenKuesionerFragment().apply {
            arguments = Bundle().apply {
                putInt("kuesioner_id", kuesionerId)
                putInt("id_kelas", idKelas)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kuesionerId = arguments?.getInt("kuesioner_id") ?: -1
        idKelas = arguments?.getInt("id_kelas") ?: -1
    }

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_responden_kuesioner, container, false)
        
        sessionManager = SessionManager(requireContext())
        rv = view.findViewById(R.id.rv_responden)
        progressBar = view.findViewById(R.id.pb_loading_responden)
        tvEmpty = view.findViewById(R.id.tv_empty_responden)
        val btnBack = view.findViewById<ImageView>(R.id.btn_back_responden)
        
        rv.layoutManager = LinearLayoutManager(context)
        
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        
        if (idKelas != -1) {
            loadStudents()
        } else {
            Toast.makeText(context, "ID Kelas tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
        
        return view
    }

    private fun loadStudents() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        
        val token = "Bearer ${sessionManager.getToken()}"
        Aktor.dynamics.getSiswaByKelas(token, idKelas).enqueue(object : Callback<List<Siswa>> {
            override fun onResponse(call: Call<List<Siswa>>, response: Response<List<Siswa>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val listSiswa = response.body()
                    if (!listSiswa.isNullOrEmpty()) {
                        setupRecyclerView(listSiswa)
                    } else {
                        tvEmpty.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<List<Siswa>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView(listSiswa: List<Siswa>) {
        val adapter = DaftarSiswaBkAdapter(listSiswa, false) { siswa ->
            // Gunakan 'id' (ID Tabel Siswa) sesuai spesifikasi API monitoring Guru BK
            val idTarget = siswa.id ?: -1 
            
            if (idTarget != -1) {
                // Notifikasi untuk verifikasi data (bisa dihapus nanti jika sudah fix)
                Toast.makeText(context, "Membuka jawaban: ${siswa.nama} (ID: $idTarget)", Toast.LENGTH_SHORT).show()
                
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_bk, JawabanKuesionerSiswaFragment.newInstance(kuesionerId, idTarget))
                    .addToBackStack(null)
                    .commit()
            } else {
                Toast.makeText(context, "Data ID untuk ${siswa.nama} tidak valid", Toast.LENGTH_SHORT).show()
            }
        }
        rv.adapter = adapter
    }
}
