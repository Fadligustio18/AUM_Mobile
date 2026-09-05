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
import com.example.bknova.adapter.KuesionerAdapter
import com.example.bknova.model.KuesionerSummary
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarKuesionerSiswaFragment : Fragment() {
    private lateinit var rv: RecyclerView
    private lateinit var adapter: KuesionerAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var btnBack: ImageView

    override fun onResume() {
        super.onResume()
        (activity as? halaman_siswa_Activity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daftar_kuesioner_bk, container, false) // Reuse layout
        
        sessionManager = SessionManager(requireContext())
        rv = view.findViewById(R.id.rv_kuesioner)
        btnBack = view.findViewById(R.id.btn_back_kuesioner)
        val fabAdd = view.findViewById<View>(R.id.fab_add_kuesioner)
        fabAdd.visibility = View.GONE // Siswa cannot create
        
        rv.layoutManager = LinearLayoutManager(context)
        adapter = KuesionerAdapter(
            list = emptyList(),
            onItemClick = { kuesioner ->
                if (kuesioner.sudahSubmit) {
                    Toast.makeText(context, "Anda sudah mengerjakan kuesioner ini", Toast.LENGTH_SHORT).show()
                } else {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, IsiKuesionerFragment.newInstance(kuesioner.id))
                        .addToBackStack(null)
                        .commit()
                }
            }
        )
        rv.adapter = adapter
        
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        
        loadData()
        
        return view
    }

    private fun loadData() {
        val token = "Bearer ${sessionManager.getToken()}"
        val idUser = sessionManager.getUserId()
        
        Aktor.kuesioner.getKuesionerSiswa(token, idUser).enqueue(object : Callback<List<KuesionerSummary>> {
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
}
