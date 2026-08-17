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
import com.example.bknova.adapter.DaftarSiswaBkAdapter
import com.example.bknova.controller.AuthController
import com.example.bknova.model.Siswa
import com.example.bknova.service.Aktor
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarSiswaBkFragment : Fragment() {
    private lateinit var authController: AuthController
    private lateinit var rvSiswa: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvEmpty: TextView
    
    private var idKelas: Int = -1
    private var namaKelas: String = ""
    private var isAumMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController(requireContext())
        arguments?.let {
            idKelas = it.getInt("id_kelas", -1)
            namaKelas = it.getString("nama_kelas", "")
            isAumMode = it.getBoolean("is_aum_mode", false)
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
        return inflater.inflate(R.layout.fragment_daftar_siswa_bk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar_siswa)
        rvSiswa = view.findViewById(R.id.rv_daftar_siswa)
        progressBar = view.findViewById(R.id.pb_loading_siswa)
        tvEmpty = view.findViewById(R.id.tv_empty_siswa)

        if (namaKelas.isNotEmpty()) {
            toolbar.title = "Siswa $namaKelas"
        }

        rvSiswa.layoutManager = LinearLayoutManager(context)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        if (idKelas != -1) {
            fetchStudents()
        }
    }

    private fun fetchStudents() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        
        val token = authController.getToken() ?: return
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        Aktor.dynamics.getSiswaByKelas(bearerToken, idKelas).enqueue(object : Callback<List<Siswa>> {
            override fun onResponse(call: Call<List<Siswa>>, response: Response<List<Siswa>>) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val listSiswa = response.body()
                        if (!listSiswa.isNullOrEmpty()) {
                            setupRecyclerView(listSiswa)
                        } else {
                            tvEmpty.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(context, "Gagal memuat data: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Siswa>>, t: Throwable) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, "Kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupRecyclerView(listSiswa: List<Siswa>) {
        val adapter = DaftarSiswaBkAdapter(listSiswa) { siswa ->
            if (isAumMode) {
                siswa.id?.let { id ->
                    val fragment = DetailAumSiswaFragment.newInstance(id, siswa.nama)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_bk, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                // Navigate to student detail if needed
                Toast.makeText(context, "Melihat profil ${siswa.nama}", Toast.LENGTH_SHORT).show()
            }
        }
        rvSiswa.adapter = adapter
    }

    companion object {
        fun newInstance(idKelas: Int, namaKelas: String, isAumMode: Boolean = false) =
            DaftarSiswaBkFragment().apply {
                arguments = Bundle().apply {
                    putInt("id_kelas", idKelas)
                    putString("nama_kelas", namaKelas)
                    putBoolean("is_aum_mode", isAumMode)
                }
            }
    }
}
