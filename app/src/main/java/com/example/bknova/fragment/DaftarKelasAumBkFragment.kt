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
import com.example.bknova.adapter.DaftarKelasBkAdapter
import com.example.bknova.controller.AuthController
import com.example.bknova.model.BkTask
import com.example.bknova.service.Aktor
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarKelasAumBkFragment : Fragment() {
    private lateinit var authController: AuthController
    private lateinit var rvKelas: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvEmpty: TextView

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
        return inflater.inflate(R.layout.fragment_daftar_kelas_aum_bk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar_kelas_aum)
        rvKelas = view.findViewById(R.id.rv_daftar_kelas_aum)
        progressBar = view.findViewById(R.id.pb_loading_aum)
        tvEmpty = view.findViewById(R.id.tv_empty_aum)

        rvKelas.layoutManager = LinearLayoutManager(context)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fetchClasses()
    }

    private fun fetchClasses() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        
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
                            tvEmpty.visibility = View.VISIBLE
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
        val adapter = DaftarKelasBkAdapter(tasks) { _, task ->
            showOptionsBottomSheet(task)
        }
        rvKelas.adapter = adapter
        rvKelas.scheduleLayoutAnimation()
    }

    private fun showOptionsBottomSheet(task: BkTask) {
        val bottomSheet = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.layout_popup_aum_options, null)

        view.findViewById<View>(R.id.btn_popup_statistik).setOnClickListener {
            bottomSheet.dismiss()
            navigateToStatistik(task)
        }

        view.findViewById<View>(R.id.btn_popup_siswa).setOnClickListener {
            bottomSheet.dismiss()
            navigateToDaftarSiswa(task)
        }

        bottomSheet.setContentView(view)
        bottomSheet.show()
    }

    private fun navigateToStatistik(task: BkTask) {
        val fragment = StatistikAumFragment.newInstance(task.namaKelas, task.idKelas)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_bk, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToDaftarSiswa(task: BkTask) {
        val fragment = DaftarSiswaBkFragment.newInstance(task.idKelas, task.namaKelas, true)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_bk, fragment)
            .addToBackStack(null)
            .commit()
    }
}
