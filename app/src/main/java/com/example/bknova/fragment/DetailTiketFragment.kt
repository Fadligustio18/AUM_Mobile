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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        binding.tilTempat.editText?.setOnClickListener {
            val commonPlaces = listOf("Ruang BK", "Perpustakaan", "Laboratorium", "Kelas", "Custom...")
            showSelectionBottomSheet("Pilih Tempat Konseling", commonPlaces) { index ->
                val selected = commonPlaces[index]
                if (selected == "Custom...") {
                    showCustomPlaceDialog()
                } else {
                    binding.tilTempat.editText?.setText(selected)
                }
            }
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
                // Info kelas sudah ada di objek Tiket (tingkat & kelas)
                val level = it.tingkat ?: ""
                val className = it.kelas ?: ""
                val fullKelas = if (level.isNotEmpty()) "$level $className" else className
                binding.tvDetailKelas.text = "Kelas: ${if (fullKelas.isEmpty()) "-" else fullKelas}"
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

    private fun showCustomPlaceDialog() {
        val input = android.widget.EditText(requireContext())
        val padding = (24 * resources.displayMetrics.density).toInt()
        val container = android.widget.FrameLayout(requireContext())
        val params = android.widget.FrameLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(padding, 8, padding, 8)
        input.layoutParams = params
        input.hint = "Tulis tempat spesifik..."
        container.addView(input)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tempat Konseling Custom")
            .setMessage("Masukkan nama tempat pertemuan:")
            .setView(container)
            .setPositiveButton("Simpan") { _, _ ->
                val customPlace = input.text.toString().trim()
                if (customPlace.isNotEmpty()) {
                    binding.tilTempat.editText?.setText(customPlace)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
            
        input.requestFocus()
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

    private fun showSelectionBottomSheet(title: String, items: List<String>, onSelected: (Int) -> Unit) {
        val bottomSheet = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.layout_popup_selection, null)
        
        view.findViewById<android.widget.TextView>(R.id.tv_selection_title).text = title
        val listView = view.findViewById<android.widget.ListView>(R.id.lv_selection)
        
        val adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter
        
        listView.setOnItemClickListener { _, _, position, _ ->
            onSelected(position)
            bottomSheet.dismiss()
        }
        
        bottomSheet.setContentView(view)
        bottomSheet.show()
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
        val status = tiket?.status ?: ""
        val (title, message) = when (status) {
            "Selesai", "Dibatalkan" -> "Hapus Riwayat" to "Apakah Anda yakin ingin menghapus riwayat tiket ini?"
            "Dikirim" -> "Hapus Tiket" to "Apakah Anda yakin ingin menghapus pengajuan tiket ini?"
            else -> "Batalkan Tiket" to "Apakah Anda yakin ingin membatalkan jadwal konseling ini?"
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ya, Lanjutkan") { _, _ ->
                performDeleteTiket()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun performDeleteTiket() {
        val token = "Bearer ${sessionManager.getToken()}"
        val id = tiket?.id ?: return
        
        // Langsung gunakan endpoint DELETE untuk kedua role (Siswa & Guru BK)
        // Jika backend memperbolehkan, tiket akan terhapus permanen dari database dan hilang di kedua sisi.
        Aktor.tiket.deleteTiket(token, id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful || response.code() == 200 || response.code() == 204) {
                    Toast.makeText(context, "Tiket berhasil dihapus permanen", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    val errorMsg = when(response.code()) {
                        403 -> "Anda tidak memiliki izin untuk menghapus tiket ini"
                        404 -> "Tiket tidak ditemukan"
                        else -> "Gagal menghapus tiket: ${response.code()}"
                    }
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Kesalahan jaringan: ${t.message}", Toast.LENGTH_SHORT).show()
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
