package com.example.bknova.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.bknova.R
import com.example.bknova.activity.LoginActivity
import com.example.bknova.controller.AuthController
import com.example.bknova.model.UserResponse
import com.google.android.material.button.MaterialButton

class profilBkFragment : Fragment() {
    private lateinit var authController: AuthController

    // Views
    private lateinit var tvName: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvBadgeKelas: TextView
    private lateinit var tvTahunAjaran: TextView
    private lateinit var tvTingkat: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController(requireContext())
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
        tvBadgeKelas = view.findViewById(R.id.tv_badge_kelas)
        tvTahunAjaran = view.findViewById(R.id.tv_tahun_ajaran)
        tvTingkat = view.findViewById(R.id.tv_tingkat)
        
        val btnLogout = view.findViewById<MaterialButton>(R.id.btn_logout)

        fetchUserProfile()

        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun fetchUserProfile() {
        authController.getUserProfile(object : AuthController.UserCallback {
            override fun onSuccess(user: UserResponse) {
                if (isAdded) {
                    updateUI(user)
                }
            }

            override fun onError(message: String) {
                if (isAdded) {
                    Toast.makeText(context, "Gagal memuat profil: $message", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateUI(user: UserResponse) {
        tvName.text = user.nama
        tvRole.text = user.role
        
        val profile = user.profile
        val tingkat = profile?.tingkat ?: ""
        val kelas = profile?.kelas ?: ""
        val fullKelas = if (tingkat.isNotEmpty() && kelas.isNotEmpty()) "$tingkat $kelas" else (tingkat + kelas).ifEmpty { "-" }
        
        tvBadgeKelas.text = fullKelas
        tvTahunAjaran.text = profile?.tahunAjaran ?: "-"
        tvTingkat.text = profile?.tingkat ?: "-"
    }

    private fun logout() {
        authController.logout()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}








































//Radot was here