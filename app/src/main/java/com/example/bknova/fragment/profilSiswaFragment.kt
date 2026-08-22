package com.example.bknova.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import com.example.bknova.R
import com.example.bknova.activity.LoginActivity
import com.example.bknova.activity.halaman_siswa_Activity
import com.example.bknova.controller.AuthController
import com.example.bknova.model.ChangePasswordRequest
import com.example.bknova.model.UserResponse
import com.example.bknova.ui.WaveTransitionHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class profilFragment : Fragment() {
    private lateinit var authController: AuthController

    // Views
    private lateinit var tvName: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvBadgeKelas: TextView
    private lateinit var tvBadgeSchool: TextView
    private lateinit var tvNisnNis: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvTtl: TextView
    private lateinit var tvJurusan: TextView
    private lateinit var tvTahunAjaran: TextView

    private lateinit var layoutNisnNis: LinearLayout
    private lateinit var layoutGender: LinearLayout
    private lateinit var layoutTtl: LinearLayout
    private lateinit var layoutJurusan: LinearLayout
    private lateinit var layoutTahunAjaran: LinearLayout

    override fun onResume() {
        super.onResume()
        // Pastikan Bottom Navigation muncul di halaman profil
        (activity as? halaman_siswa_Activity)?.setBottomNavigationVisibility(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profil_siswa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            // Initialize Views
            tvName = view.findViewById(R.id.tv_name_profil)
            tvRole = view.findViewById(R.id.tv_role_profil)
            tvBadgeKelas = view.findViewById(R.id.tv_badge_kelas)
            tvBadgeSchool = view.findViewById(R.id.tv_badge_school)
            tvNisnNis = view.findViewById(R.id.tv_nisn_nis)
            tvGender = view.findViewById(R.id.tv_gender)
            tvTtl = view.findViewById(R.id.tv_ttl)
            tvJurusan = view.findViewById(R.id.tv_jurusan)
            tvTahunAjaran = view.findViewById(R.id.tv_tahun_ajaran)

            layoutNisnNis = view.findViewById(R.id.layout_nisn_nis)
            layoutGender = view.findViewById(R.id.layout_gender)
            layoutTtl = view.findViewById(R.id.layout_ttl)
            layoutJurusan = view.findViewById(R.id.layout_jurusan)
            layoutTahunAjaran = view.findViewById(R.id.layout_tahun_ajaran)

            val btnLogout = view.findViewById<MaterialButton>(R.id.btn_logout)
            val btnChangePassword = view.findViewById<LinearLayout>(R.id.btn_change_password)
            
            // Handle Window Insets for bottom padding
            val scrollView = view.findViewById<NestedScrollView>(R.id.scroll_view_profil_siswa)
            val initialPaddingBottom = scrollView.paddingBottom
            ViewCompat.setOnApplyWindowInsetsListener(scrollView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, initialPaddingBottom + systemBars.bottom)
                insets
            }

            fetchUserProfile()

            btnLogout?.setOnClickListener {
                showLogoutConfirmation()
            }

            btnChangePassword?.setOnClickListener {
                showChangePasswordDialog()
            }
        } catch (e: Exception) {
            android.util.Log.e("PROFIL_ERROR", "Error in onViewCreated: ${e.message}")
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null)
        val etOldPassword = dialogView.findViewById<TextInputEditText>(R.id.et_old_password)
        val etNewPassword = dialogView.findViewById<TextInputEditText>(R.id.et_new_password)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ganti Kata Sandi")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                val oldPassword = etOldPassword.text.toString()
                val newPassword = etNewPassword.text.toString()

                if (oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                    performChangePassword(oldPassword, newPassword)
                } else {
                    Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun performChangePassword(old: String, new: String) {
        val request = ChangePasswordRequest(old, new)
        authController.changePassword(request, object : AuthController.ChangePasswordCallback {
            override fun onSuccess(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            override fun onError(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUserProfile() {
        authController.getUserProfile(object : AuthController.UserCallback {
            override fun onSuccess(user: UserResponse) {
                updateUI(user)
            }

            override fun onError(message: String) {
                Toast.makeText(context, "Gagal memuat profil: $message", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(user: UserResponse) {
        if (!isAdded) return
        
        tvName.text = user.nama
        tvRole.text = if (user.role.lowercase() == "siswa") "Siswa Aktif" else user.role
        
        val profile = user.profile
        val tingkat = profile?.tingkat ?: ""
        val kelas = profile?.kelas ?: ""
        val fullKelas = if (tingkat.isNotEmpty() && kelas.isNotEmpty()) "$tingkat $kelas" else (tingkat + kelas).ifEmpty { "-" }
        
        tvBadgeKelas.text = fullKelas
        
        if (user.role.lowercase() == "siswa") {
            layoutNisnNis.visibility = View.VISIBLE
            layoutGender.visibility = View.VISIBLE
            layoutTtl.visibility = View.VISIBLE
            layoutJurusan.visibility = View.VISIBLE
            layoutTahunAjaran.visibility = View.GONE

            val nisn = profile?.nisn ?: "-"
            val nis = profile?.nis ?: "-"
            tvNisnNis.text = "$nisn / $nis"
            
            tvGender.text = profile?.jenisKelamin ?: "-"
            tvTtl.text = profile?.tempatTanggalLahir ?: "-"
            tvJurusan.text = profile?.jurusan ?: "-"
        } else {
            layoutNisnNis.visibility = View.GONE
            layoutGender.visibility = View.GONE
            layoutTtl.visibility = View.GONE
            layoutJurusan.visibility = View.GONE
            layoutTahunAjaran.visibility = View.VISIBLE

            tvTahunAjaran.text = profile?.tahunAjaran ?: "-"
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

    private fun logout() {
        authController.logout()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        
        WaveTransitionHelper.startTransition(requireActivity(), intent)
    }
}
