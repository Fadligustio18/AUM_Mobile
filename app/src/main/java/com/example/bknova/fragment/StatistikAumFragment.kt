package com.example.bknova.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bknova.adapter.AumStatistikAdapter
import com.example.bknova.controller.AuthController
import com.example.bknova.databinding.FragmentStatistikAumBinding
import com.example.bknova.model.AumBidangHasil
import com.example.bknova.model.AumHasilSiswa
import com.example.bknova.service.Aktor
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatistikAumFragment : Fragment() {

    private var _binding: FragmentStatistikAumBinding? = null
    private val binding get() = _binding!!
    private lateinit var authController: AuthController

    private var filterKelas: String? = null

    private val bidangList = listOf(
        "JDK" to "Jasmani dan Kesehatan",
        "DPI" to "Diri Pribadi",
        "KHK" to "Hubungan Sosial",
        "HSO" to "Ekonomi dan Keuangan",
        "KDP" to "Karier dan Pekerjaan",
        "EDK" to "Pendidikan dan Belajar",
        "WSG" to "Agama, Nilai, dan Moral",
        "ANM" to "Hubungan Muda-Mudi",
        "HMP" to "Keadaan dan Hubungan Keluarga",
        "PDP" to "Waktu Senggang"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            filterKelas = it.getString(ARG_CLASS_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatistikAumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authController = AuthController(requireContext())
        
        if (filterKelas != null) {
            binding.tvTitleStatistik.text = "Statistik AUM - $filterKelas"
            binding.tvSubtitleStatistik.text = "Distribusi Bidang Masalah Kelas $filterKelas"
        }

        setupRecyclerView()
        fetchData()
    }

    private fun setupRecyclerView() {
        binding.rvSummaryStats.layoutManager = 
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun fetchData() {
        val token = authController.getToken()
        val idGuru = authController.getUserId()

        if (token == null || idGuru == -1) {
            Toast.makeText(context, "Sesi berakhir, silakan login kembali", Toast.LENGTH_SHORT).show()
            return
        }

        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        Aktor.aum.getHasilAumByGuru(bearerToken, idGuru).enqueue(object : Callback<List<AumHasilSiswa>> {
            override fun onResponse(call: Call<List<AumHasilSiswa>>, response: Response<List<AumHasilSiswa>>) {
                if (response.isSuccessful) {
                    val data = response.body() ?: emptyList()
                    processData(data)
                } else {
                    Toast.makeText(context, "Gagal mengambil data statistik", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AumHasilSiswa>>, t: Throwable) {
                Toast.makeText(context, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun processData(data: List<AumHasilSiswa>) {
        val filteredData = if (filterKelas != null) {
            data.filter { it.kelas.contains(filterKelas!!, ignoreCase = true) }
        } else {
            data
        }

        val counts = mutableMapOf<String, Int>()
        bidangList.forEach { counts[it.first] = 0 }

        // Agregasi jumlah masalah per bidang dari siswa yang difilter
        filteredData.forEach { siswa ->
            siswa.bidang.forEach { bidang ->
                val currentCount = counts[bidang.kodeBidang] ?: 0
                counts[bidang.kodeBidang] = currentCount + bidang.pilihan.size
            }
        }

        val resultBidang = bidangList.map { (kode, nama) ->
            val totalPilihan = counts[kode] ?: 0
            val dummyList = List(totalPilihan) { "" }
            AumBidangHasil(kode, nama, dummyList)
        }

        updateUI(resultBidang)
    }

    private fun updateUI(data: List<AumBidangHasil>) {
        // 1. RecyclerView Summary
        binding.rvSummaryStats.adapter = AumStatistikAdapter(data)

        // 2. Donut Chart
        setupDonutChart(data)

        // 3. Bar Chart
        setupBarChart(data)
    }

    private fun setupDonutChart(data: List<AumBidangHasil>) {
        val entries = data.filter { it.pilihan.isNotEmpty() }.map {
            PieEntry(it.pilihan.size.toFloat(), it.kodeBidang)
        }

        if (entries.isEmpty()) {
            binding.donutChart.setNoDataText("Tidak ada data masalah")
            binding.donutChart.data = null
            binding.donutChart.invalidate()
            return
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.WHITE

        val pieData = PieData(dataSet)
        
        binding.donutChart.apply {
            this.data = pieData
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setCenterText("Total\nMasalah")
            setCenterTextSize(16f)
            animateY(1000)
            legend.isEnabled = true
            invalidate()
        }
    }

    private fun setupBarChart(data: List<AumBidangHasil>) {
        val entries = data.mapIndexed { index, bidang ->
            BarEntry(index.toFloat(), bidang.pilihan.size.toFloat())
        }

        val dataSet = BarDataSet(entries, "Jumlah Masalah")
        dataSet.color = Color.parseColor("#5561F4") // brand_primary
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f

        binding.barChart.apply {
            this.data = barData
            description.isEnabled = false
            setPinchZoom(false)
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(data.map { it.kodeBidang })
                setDrawGridLines(false)
            }

            axisLeft.apply {
                granularity = 1f
                axisMinimum = 0f
            }
            axisRight.isEnabled = false
            
            animateY(1000)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CLASS_NAME = "class_name"
        private const val ARG_CLASS_ID = "class_id"

        @JvmStatic
        fun newInstance(className: String? = null, classId: Int? = null) =
            StatistikAumFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CLASS_NAME, className)
                    if (classId != null) putInt(ARG_CLASS_ID, classId)
                }
            }
    }
}
