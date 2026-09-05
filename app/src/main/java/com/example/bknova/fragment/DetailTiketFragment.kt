package com.example.bknova.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bknova.R
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.activity.halaman_siswa_Activity
import com.example.bknova.databinding.FragmentDetailTiketBinding
import com.example.bknova.model.*
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DetailTiketFragment : Fragment() {
    private var _binding: FragmentDetailTiketBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private var tiket: Tiket? = null

    companion object {
        fun newInstance(tiket: Tiket) = DetailTiketFragment().apply {
            arguments = Bundle().apply { putSerializable("tiket_data", tiket) }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(false)
        (activity as? halaman_siswa_Activity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tiket = arguments?.getSerializable("tiket_data") as? Tiket
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailTiketBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())

        setupUI()

        binding.btnBackDetailTiket.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.tilTanggal.editText?.setOnClickListener {
            showDateTimePicker()
        }

        // BK Actions
        binding.btnSetujui.setOnClickListener { approveTiket() }
        binding.btnUpdateLokasi.setOnClickListener { updateLokasi() }
        binding.btnTunda.setOnClickListener { tundaTiket() }
        binding.btnSelesaiBk.setOnClickListener { selesaikanTiketBk() }
        binding.btnHapusRiwayatBk.setOnClickListener { hapusTiketSiswa() } // BK can reuse the same delete logic

        // Siswa Actions
        binding.btnEditSiswa.setOnClickListener { editTiketSiswa() }
        binding.btnHapusSiswa.setOnClickListener { hapusTiketSiswa() }

        return binding.root
    }

    private fun setupUI() {
        val role = sessionManager.getRole()
        val status = tiket?.status ?: ""

        if (role == "Siswa") {
            binding.layoutActionsBk.visibility = View.GONE
            binding.layoutActionsSiswa.visibility = View.VISIBLE
            
            // Hide Student info, Show BK info for Student
            binding.tvDetailSiswa.visibility = View.GONE
            binding.tvDetailKelas.visibility = View.GONE
            binding.tvDetailBk.visibility = View.VISIBLE
            
            // Siswa can edit if status is Dikirim, Disetujui, or Ditunda
            if (status == "Dikirim" || status == "Disetujui" || status == "Ditunda") {
                binding.btnEditSiswa.visibility = View.VISIBLE
            } else {
                binding.btnEditSiswa.visibility = View.GONE
            }

            // Siswa can delete/cancel anytime (Hapus for sent/finished, Batalkan for scheduled)
            binding.btnHapusSiswa.visibility = View.VISIBLE
            binding.btnHapusSiswa.text = when (status) {
                "Dikirim" -> "Hapus Tiket"
                "Selesai", "Dibatalkan" -> "Hapus Riwayat Tiket"
                else -> "Batalkan Tiket"
            }
        } else {
            binding.layoutActionsBk.visibility = View.VISIBLE
            binding.layoutActionsSiswa.visibility = View.GONE

            // Show Student info, Hide BK info for BK
            binding.tvDetailSiswa.visibility = View.VISIBLE
            binding.tvDetailKelas.visibility = View.VISIBLE
            binding.tvDetailBk.visibility = View.GONE

            // Logic Guru BK based on status
            when (status) {
                "Dikirim" -> {
                    binding.layoutActionsBk.visibility = View.VISIBLE
                    binding.btnSetujui.visibility = View.VISIBLE
                    binding.btnUpdateLokasi.visibility = View.GONE
                    binding.btnTunda.visibility = View.GONE
                    binding.btnSelesaiBk.visibility = View.GONE
                    binding.btnHapusRiwayatBk.visibility = View.GONE
                    binding.tilTempat.visibility = View.VISIBLE
                    binding.tilTanggal.visibility = View.VISIBLE
                }
                "Disetujui", "Ditunda" -> {
                    binding.layoutActionsBk.visibility = View.VISIBLE
                    binding.btnSetujui.visibility = View.GONE
                    binding.btnUpdateLokasi.visibility = View.VISIBLE
                    binding.btnTunda.visibility = View.VISIBLE
                    binding.btnSelesaiBk.visibility = View.VISIBLE
                    binding.btnHapusRiwayatBk.visibility = View.GONE
                    binding.tilTempat.visibility = View.VISIBLE
                    binding.tilTanggal.visibility = View.VISIBLE
                }
                else -> {
                    // Selesai or Dibatalkan
                    binding.layoutActionsBk.visibility = View.VISIBLE
                    binding.btnSetujui.visibility = View.GONE
                    binding.btnUpdateLokasi.visibility = View.GONE
                    binding.btnTunda.visibility = View.GONE
                    binding.btnSelesaiBk.visibility = View.GONE
                    binding.btnHapusRiwayatBk.visibility = View.VISIBLE
                    binding.tilTempat.visibility = View.GONE
                    binding.tilTanggal.visibility = View.GONE
                }
            }
        }

        tiket?.let {
            binding.tvDetailSiswa.text = "Siswa: ${it.siswa ?: "-"}"
            binding.tvDetailStatus.text = it.status

            // Apply Status Colors
            val (bgRes, textColor) = when (it.status) {
                "Dikirim" -> R.drawable.bg_status_dikirim to R.color.brand_primary
                "Disetujui" -> R.drawable.bg_status_disetujui to R.color.green_text
                "Ditunda" -> R.drawable.bg_status_ditunda to R.color.accent_peach_text
                "Dibatalkan" -> R.drawable.bg_status_dibatalkan to R.color.red_text
                "Selesai" -> R.drawable.bg_status_selesai to R.color.brand_dark
                else -> R.drawable.bg_status_dikirim to R.color.text_color_secondary
            }
            binding.tvDetailStatus.setBackgroundResource(bgRes)
            binding.tvDetailStatus.setTextColor(ContextCompat.getColor(requireContext(), textColor))
            
            // Cek apakah tingkat ada data, jika tidak tampilkan hanya kelas
            val level = it.tingkat ?: ""
            val className = it.kelas ?: ""
            val fullKelas = if (level.isNotEmpty()) "$level $className" else className
            
            binding.tvDetailKelas.text = "Kelas: ${if (fullKelas.isEmpty()) "-" else fullKelas}"
            
            binding.tvDetailBk.text = "Guru BK: ${it.bk ?: "-"}"
            binding.tvDetailJudul.text = it.judul
            binding.tvDetailIsi.text = it.isi
            
            // Fetch detailed class info if it's a Guru BK view
            if (sessionManager.getRole() != "Siswa") {
                val studentId = it.idSiswa ?: -1
                if (studentId != -1) {
                    fetchDetailedClass(studentId)
                } else {
                    // Jika studentId tidak ada di JSON, coba tampilkan tingkat dari JSON tiket jika ada
                    val level = it.tingkat ?: ""
                    val className = it.kelas ?: ""
                    val fullKelas = if (level.isNotEmpty()) "$level $className" else className
                    binding.tvDetailKelas.text = "Kelas: ${if (fullKelas.isEmpty()) "-" else fullKelas}"
                }
            }
            
            // Show meeting info if exists
            if (!it.tempat.isNullOrEmpty() || !it.tanggalPerjanjian.isNullOrEmpty()) {
                binding.layoutInfoPerjanjian.visibility = View.VISIBLE
                binding.tvDetailTempat.text = "Tempat: ${it.tempat ?: "-"}"
                binding.tvDetailTanggal.text = "Waktu: ${it.tanggalPerjanjian ?: "-"}"
            } else {
                binding.layoutInfoPerjanjian.visibility = View.GONE
            }
            
            binding.tilTempat.editText?.setText(it.tempat ?: "")
            binding.tilTanggal.editText?.setText(it.tanggalPerjanjian ?: "")
        }
    }

    private fun fetchDetailedClass(id: Int) {
        val token = "Bearer ${sessionManager.getToken()}"
        Aktor.academic.getSiswaKelas(token, id).enqueue(object : Callback<SiswaKelas> {
            override fun onResponse(call: Call<SiswaKelas>, response: Response<SiswaKelas>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.let {
                        val level = it.tingkat
                        val className = it.namaKelas
                        binding.tvDetailKelas.text = "Kelas: $level $className"
                    }
                } else {
                    // Jika gagal, tampilkan pesan singkat untuk debug
                    // Toast.makeText(context, "API Kelas Gagal: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SiswaKelas>, t: Throwable) {
                // Toast.makeText(context, "Error API Kelas: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                // Format ISO 8601: YYYY-MM-DDTHH:mm:ss
                val formattedDate = String.format(
                    Locale.US,
                    "%04d-%02d-%02dT%02d:%02d:00",
                    year, month + 1, day, hour, minute
                )
                binding.tilTanggal.editText?.setText(formattedDate)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun approveTiket() {
        val currentTempat = binding.tilTempat.editText?.text.toString()
        val currentTanggal = binding.tilTanggal.editText?.text.toString()

        if (currentTempat.isEmpty() || currentTanggal.isEmpty()) {
            Toast.makeText(context, "Tempat dan Tanggal harus diisi terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val token = "Bearer ${sessionManager.getToken()}"
        val id = tiket?.id ?: return
        val request = TiketApproveRequest(currentTempat, currentTanggal)

        Aktor.tiket.setujuiTiket(token, id, request).enqueue(createCallback("Tiket disetujui"))
    }

    private fun updateLokasi() {
        val currentTempat = binding.tilTempat.editText?.text.toString()

        // Validasi: Cek apakah ada perubahan lokasi
        if (currentTempat == tiket?.tempat) {
            Toast.makeText(context, "Tolong ubah tempat terlebih dahulu sebelum update lokasi", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentTempat.isEmpty()) {
            Toast.makeText(context, "Tempat tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val token = "Bearer ${sessionManager.getToken()}"
        val id = tiket?.id ?: return
        val request = TiketUpdateLokasiRequest(currentTempat)

        Aktor.tiket.updateLokasiTiket(token, id, request).enqueue(createCallback("Lokasi diperbarui"))
    }

    private fun tundaTiket() {
        val currentTempat = binding.tilTempat.editText?.text.toString()
        val currentTanggal = binding.tilTanggal.editText?.text.toString()

        // Validasi: Cek apakah ada perubahan data
        if (currentTempat == tiket?.tempat && currentTanggal == tiket?.tanggalPerjanjian) {
            Toast.makeText(context, "Tolong ubah tanggal/tempat sebelum menunda", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentTempat.isEmpty() || currentTanggal.isEmpty()) {
            Toast.makeText(context, "Tempat dan Tanggal harus diisi untuk menunda", Toast.LENGTH_SHORT).show()
            return
        }

        val token = "Bearer ${sessionManager.getToken()}"
        val id = tiket?.id ?: return
        val request = TiketTundaRequest(currentTempat, currentTanggal)

        Aktor.tiket.tundaTiket(token, id, request).enqueue(createCallback("Tiket ditunda"))
    }

    private fun selesaikanTiketBk() {
        val token = "Bearer ${sessionManager.getToken()}"
        val id = tiket?.id ?: return
        Aktor.tiket.selesaiTiket(token, id).enqueue(createCallback("Tiket selesai"))
    }

    private fun editTiketSiswa() {
        tiket?.let {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PengajuanTiketFragment.newInstance(it))
                .addToBackStack(null)
                .commit()
        }
    }

    private fun hapusTiketSiswa() {
        val token = "Bearer ${sessionManager.getToken()}"
        val id = tiket?.id ?: return
        Aktor.tiket.deleteTiket(token, id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Tiket dihapus", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createCallback(message: String) = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(context, "Gagal: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
