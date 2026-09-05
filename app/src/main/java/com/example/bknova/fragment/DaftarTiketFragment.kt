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
import com.example.bknova.adapter.TiketAdapter
import com.example.bknova.controller.AuthController
import com.example.bknova.model.Tiket
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarTiketFragment : Fragment() {
    private lateinit var rvTiket: RecyclerView
    private lateinit var adapter: TiketAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var btnBack: ImageView

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daftar_tiket, container, false)
        
        sessionManager = SessionManager(requireContext())
        rvTiket = view.findViewById(R.id.rv_tiket)
        btnBack = view.findViewById(R.id.btn_back_daftar_tiket)
        rvTiket.layoutManager = LinearLayoutManager(context)
        
        adapter = TiketAdapter(emptyList()) { tiket ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_bk, DetailTiketFragment.newInstance(tiket))
                .addToBackStack(null)
                .commit()
        }
        rvTiket.adapter = adapter
        
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        loadTiket()
        
        return view
    }

    private fun loadTiket() {
        val token = "Bearer ${sessionManager.getToken()}"
        val idUser = sessionManager.getUserId()
        
        Aktor.tiket.getTiketBk(token, idUser).enqueue(object : Callback<List<Tiket>> {
            override fun onResponse(call: Call<List<Tiket>>, response: Response<List<Tiket>>) {
                if (response.isSuccessful) {
                    response.body()?.let { adapter.updateData(it) }
                } else {
                    Toast.makeText(context, "Gagal memuat tiket", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Tiket>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
