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
import com.example.bknova.R
import com.example.bknova.activity.LoginActivity
import com.example.bknova.controller.AuthController
import com.example.bknova.model.UserResponse
import com.google.android.material.button.MaterialButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [profilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class profilFragment : Fragment() {
    private lateinit var authController: AuthController

    // Views
    private lateinit var tvName: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvBadgeKelas: TextView
    private lateinit var tvBadgeSchool: TextView
    private lateinit var tvNisn: TextView
    private lateinit var tvNis: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvTtl: TextView
    private lateinit var tvJurusan: TextView
    private lateinit var tvTahunAjaran: TextView

    private lateinit var layoutNisn: LinearLayout
    private lateinit var layoutNis: LinearLayout
    private lateinit var layoutGender: LinearLayout
    private lateinit var layoutTtl: LinearLayout
    private lateinit var layoutJurusan: LinearLayout
    private lateinit var layoutTahunAjaran: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profil_siswa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Views
        tvName = view.findViewById(R.id.tv_name_profil)
        tvRole = view.findViewById(R.id.tv_role_profil)
        tvBadgeKelas = view.findViewById(R.id.tv_badge_kelas)
        tvBadgeSchool = view.findViewById(R.id.tv_badge_school)
        tvNisn = view.findViewById(R.id.tv_nisn)
        tvNis = view.findViewById(R.id.tv_nis)
        tvGender = view.findViewById(R.id.tv_gender)
        tvTtl = view.findViewById(R.id.tv_ttl)
        tvJurusan = view.findViewById(R.id.tv_jurusan)
        tvTahunAjaran = view.findViewById(R.id.tv_tahun_ajaran)

        layoutNisn = view.findViewById(R.id.layout_nisn)
        layoutNis = view.findViewById(R.id.layout_nis)
        layoutGender = view.findViewById(R.id.layout_gender)
        layoutTtl = view.findViewById(R.id.layout_ttl)
        layoutJurusan = view.findViewById(R.id.layout_jurusan)
        layoutTahunAjaran = view.findViewById(R.id.layout_tahun_ajaran)

        val btnLogout = view.findViewById<MaterialButton>(R.id.btn_logout)

        fetchUserProfile()

        btnLogout.setOnClickListener {
            logout()
        }
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
        tvName.text = user.nama
        tvRole.text = user.role
        
        val tingkat = user.profile.tingkat ?: ""
        val kelas = user.profile.kelas ?: ""
        val fullKelas = if (tingkat.isNotEmpty() && kelas.isNotEmpty()) "$tingkat $kelas" else (tingkat + kelas).ifEmpty { "-" }
        
        tvBadgeKelas.text = fullKelas
        
        if (user.role == "Siswa") {
            layoutNisn.visibility = View.VISIBLE
            layoutNis.visibility = View.VISIBLE
            layoutGender.visibility = View.VISIBLE
            layoutTtl.visibility = View.VISIBLE
            layoutJurusan.visibility = View.VISIBLE
            layoutTahunAjaran.visibility = View.GONE

            tvNisn.text = user.profile.nisn ?: "-"
            tvNis.text = user.profile.nis ?: "-"
            tvGender.text = user.profile.jenisKelamin ?: "-"
            tvTtl.text = user.profile.tempatTanggalLahir ?: "-"
            tvJurusan.text = user.profile.jurusan ?: "-"
        } else {
            layoutNisn.visibility = View.GONE
            layoutNis.visibility = View.GONE
            layoutGender.visibility = View.GONE
            layoutTtl.visibility = View.GONE
            layoutJurusan.visibility = View.GONE
            layoutTahunAjaran.visibility = View.VISIBLE

            tvTahunAjaran.text = user.profile.tahunAjaran ?: "-"
        }
    }

    private fun logout() {
        authController.logout()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment profilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            profilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}