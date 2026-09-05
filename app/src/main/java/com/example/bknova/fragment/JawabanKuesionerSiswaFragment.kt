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
import com.example.bknova.adapter.JawabanKuesionerAdapter
import com.example.bknova.model.JawabanSiswaDetail
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JawabanKuesionerSiswaFragment : Fragment() {
    private lateinit var rv: RecyclerView
    private lateinit var adapter: JawabanKuesionerAdapter
    private lateinit var sessionManager: SessionManager
    private var kuesionerId: Int = -1
    private var siswaId: Int = -1

    companion object {
        fun newInstance(kuesionerId: Int, siswaId: Int) = JawabanKuesionerSiswaFragment().apply {
            arguments = Bundle().apply {
                putInt("kuesioner_id", kuesionerId)
                putInt("siswa_id", siswaId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kuesionerId = arguments?.getInt("kuesioner_id") ?: -1
        siswaId = arguments?.getInt("siswa_id") ?: -1
    }

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_jawaban_kuesioner_bk, container, false)
        
        sessionManager = SessionManager(requireContext())
        rv = view.findViewById(R.id.rv_jawaban)
        val btnBack = view.findViewById<ImageView>(R.id.btn_back_jawaban)
        
        rv.layoutManager = LinearLayoutManager(context)
        adapter = JawabanKuesionerAdapter(emptyList())
        rv.adapter = adapter
        
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        
        loadData()
        
        return view
    }

    private fun loadData() {
        val token = "Bearer ${sessionManager.getToken()}"
        Aktor.kuesioner.getJawabanSiswa(token, kuesionerId, siswaId).enqueue(object : Callback<List<JawabanSiswaDetail>> {
            override fun onResponse(call: Call<List<JawabanSiswaDetail>>, response: Response<List<JawabanSiswaDetail>>) {
                if (response.isSuccessful) {
                    response.body()?.let { adapter.updateData(it) }
                }
            }
            override fun onFailure(call: Call<List<JawabanSiswaDetail>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
