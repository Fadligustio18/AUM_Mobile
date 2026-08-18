package com.example.bknova.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.adapter.DaftarKelasBkAdapter
import com.example.bknova.controller.AuthController
import com.example.bknova.model.BkTask
import com.example.bknova.service.Aktor
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarKelasBkFragment : Fragment() {
    private lateinit var authController: AuthController
    private lateinit var rvKelas: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController(requireContext())
    }

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daftar_kelas_bk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar_kelas)
        rvKelas = view.findViewById(R.id.rv_daftar_kelas)
        progressBar = view.findViewById(R.id.pb_loading)

        rvKelas.layoutManager = LinearLayoutManager(context)

        // Handle Window Insets for bottom padding
        val initialPaddingBottom = rvKelas.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(rvKelas) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, initialPaddingBottom + systemBars.bottom)
            insets
        }

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fetchClasses()
    }

    private fun fetchClasses() {
        progressBar.visibility = View.VISIBLE
        val token = authController.getToken() ?: return
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        Aktor.academic.getMyTasks(bearerToken).enqueue(object : Callback<List<BkTask>> {
            override fun onResponse(call: Call<List<BkTask>>, response: Response<List<BkTask>>) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val tasks = response.body()
                        if (!tasks.isNullOrEmpty()) {
                            setupRecyclerView(tasks)
                        } else {
                            Toast.makeText(context, "Tidak ada data kelas", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Gagal memuat data: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<BkTask>>, t: Throwable) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, "Kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupRecyclerView(tasks: List<BkTask>) {
        val adapter = DaftarKelasBkAdapter(tasks) { task ->
            // Navigate to student list for this class
            val fragment = DaftarSiswaBkFragment.newInstance(task.idKelas, task.namaKelas)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_bk, fragment)
                .addToBackStack(null)
                .commit()
        }
        rvKelas.adapter = adapter
    }
}
