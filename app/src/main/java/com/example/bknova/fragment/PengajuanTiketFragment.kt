package com.example.bknova.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bknova.R
import com.example.bknova.activity.halaman_siswa_Activity
import com.example.bknova.databinding.FragmentPengajuanTiketBinding
import com.example.bknova.model.Tiket
import com.example.bknova.model.TiketPengajuanRequest
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengajuanTiketFragment : Fragment() {
    private var _binding: FragmentPengajuanTiketBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private var existingTiket: Tiket? = null

    companion object {
        fun newInstance(tiket: Tiket? = null) = PengajuanTiketFragment().apply {
            arguments = Bundle().apply {
                if (tiket != null) putSerializable("existing_tiket", tiket)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        existingTiket = arguments?.getSerializable("existing_tiket") as? Tiket
    }

    override fun onStart() {
        super.onStart()
        (activity as? halaman_siswa_Activity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPengajuanTiketBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())

        // Pre-fill fields if editing
        existingTiket?.let {
            binding.tilJudulPengajuan.editText?.setText(it.judul)
            binding.tilIsiPengajuan.editText?.setText(it.isi)
            binding.btnKirimTiket.text = "Simpan Perubahan"
        }

        binding.btnBackPengajuan.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnKirimTiket.setOnClickListener {
            if (existingTiket != null) {
                updateTiket()
            } else {
                submitTiket()
            }
        }

        return binding.root
    }

    private fun submitTiket() {
        val judul = binding.tilJudulPengajuan.editText?.text.toString()
        val isi = binding.tilIsiPengajuan.editText?.text.toString()

        if (judul.isEmpty() || isi.isEmpty()) {
            Toast.makeText(context, "Judul dan Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val token = "Bearer ${sessionManager.getToken()}"
        val idUser = sessionManager.getUserId()
        val request = TiketPengajuanRequest(judul, isi)

        Aktor.tiket.ajukanTiket(token, idUser, request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Tiket berhasil diajukan", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(context, "Gagal mengajukan tiket: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateTiket() {
        val judul = binding.tilJudulPengajuan.editText?.text.toString()
        val isi = binding.tilIsiPengajuan.editText?.text.toString()
        val idTiket = existingTiket?.id ?: return

        if (judul == existingTiket?.judul && isi == existingTiket?.isi) {
            Toast.makeText(context, "Tolong ubah data terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val token = "Bearer ${sessionManager.getToken()}"
        val request = TiketPengajuanRequest(judul, isi)

        Aktor.tiket.editTiket(token, idTiket, request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Tiket berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    // Pop back twice to return to List status (since we came from Detail)
                    parentFragmentManager.popBackStack()
                    parentFragmentManager.popBackStack()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
