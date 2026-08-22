package com.example.bknova.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import com.example.bknova.R
import com.example.bknova.activity.LoginActivity
import com.example.bknova.controller.AuthController
import com.example.bknova.model.BkTask
import com.example.bknova.model.UserResponse
import com.example.bknova.service.Aktor
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.ui.WaveTransitionHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class profilBkFragment : Fragment() {
    private lateinit var authController: AuthController

    // Views
    private lateinit var tvName: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvTahunAjaran: TextView
    private lateinit var tvKelasDiampu: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController(requireContext())
    }

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profil_bk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Views
        tvName = view.findViewById(R.id.tv_name_profil)
        tvRole = view.findViewById(R.id.tv_role_profil)
        tvTahunAjaran = view.findViewById(R.id.tv_tahun_ajaran)
        tvKelasDiampu = view.findViewById(R.id.tv_kelas_diampu)
        
        val btnLogout = view.findViewById<MaterialButton>(R.id.btn_logout)

        // Handle Window Insets for bottom padding
        val scrollView = view.findViewById<NestedScrollView>(R.id.scroll_view_profil_bk)
        val initialPaddingBottom = scrollView.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, initialPaddingBottom + systemBars.bottom)
            insets
        }

        fetchUserProfile()
        fetchBkTasks()

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
            .setPositiveButton("Keluar") { _, _ ->
                logout()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun fetchUserProfile() {
        authController.getUserProfile(object : AuthController.UserCallback {
            override fun onSuccess(user: UserResponse) {
                if (isAdded) {
                    tvName.text = user.nama
                    tvRole.text = user.role
                }
            }

            override fun onError(message: String) {
                if (isAdded) {
                    Toast.makeText(context, "Gagal memuat profil: $message", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun fetchBkTasks() {
        val token = authController.getToken() ?: return
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        Aktor.academic.getMyTasks(bearerToken).enqueue(object : Callback<List<BkTask>> {
            override fun onResponse(call: Call<List<BkTask>>, response: Response<List<BkTask>>) {
                if (isAdded && response.isSuccessful) {
                    val tasks = response.body()
                    if (!tasks.isNullOrEmpty()) {
                        updateTaskUI(tasks)
                    }
                }
            }

            override fun onFailure(call: Call<List<BkTask>>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(context, "Gagal memuat data tugas: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateTaskUI(tasks: List<BkTask>) {
        val firstTask = tasks[0]
        tvTahunAjaran.text = firstTask.tahunAjaran
        
        // Combine Tingkat and Nama Kelas (e.g., "X DKV 1, X DKV 2")
        val combinedClassList = tasks.joinToString(", ") { "${it.tingkat} ${it.namaKelas}" }
        tvKelasDiampu.text = combinedClassList
    }

    private fun logout() {
        authController.logout()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        
        WaveTransitionHelper.startTransition(requireActivity(), intent)
    }
}
