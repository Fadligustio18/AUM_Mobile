package com.example.bknova.fragment

import android.graphics.Color
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.content.ContentValues
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bknova.R
import com.example.bknova.adapter.AumStatistikAdapter
import com.example.bknova.controller.AuthController
import com.example.bknova.databinding.FragmentStatistikAumBinding
import com.example.bknova.model.AumBidangHasil
import com.example.bknova.model.AumHasilSiswa
import com.example.bknova.service.Aktor
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.OutputStream

class StatistikAumFragment : Fragment() {

    private var _binding: FragmentStatistikAumBinding? = null
    private val binding get() = _binding!!
    private lateinit var authController: AuthController

    private var filterKelas: String? = null
    private val NOTIFICATION_ID = 101
    private val CHANNEL_ID = "download_channel"

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
        
        createNotificationChannel()

        if (filterKelas != null) {
            binding.tvTitleStatistik.text = "Statistik AUM $filterKelas"
            binding.tvSubtitleStatistik.text = "Distribusi Bidang Masalah Kelas $filterKelas"
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupRecyclerView()
        fetchData()

        binding.btnActionHeader.setOnClickListener {
            exportToPdf()
        }
    }

    private fun exportToPdf() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 102)
                return
            }
        }

        val nsv = binding.nsvContent
        val child = nsv.getChildAt(0) ?: return

        val height = child.height
        val width = child.width

        if (height <= 0 || width <= 0) {
            Toast.makeText(context, "Data belum siap", Toast.LENGTH_SHORT).show()
            return
        }

        showDownloadNotification("Persiapan ekspor...", 0)

        try {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            child.draw(canvas)

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            
            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDocument.finishPage(page)

            showDownloadNotification("Menyimpan PDF...", 50)
            savePdfToDownloads(pdfDocument)
            
            pdfDocument.close()
            bitmap.recycle()
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal ekspor: ${e.message}", Toast.LENGTH_SHORT).show()
            cancelNotification()
        }
    }

    private fun showDownloadNotification(title: String, progress: Int) {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(if (progress < 100) android.R.drawable.stat_sys_download else android.R.drawable.stat_sys_download_done)
            .setContentTitle(title)
            .setContentText("Statistik AUM $filterKelas")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(progress < 100)
            .setOnlyAlertOnce(true)

        if (progress in 0..99) {
            builder.setProgress(100, progress, false)
        } else {
            builder.setProgress(0, 0, false)
            builder.setOngoing(false)
            builder.setContentTitle("Ekspor Selesai")
            builder.setContentText("PDF berhasil disimpan ke folder Download")
            builder.setAutoCancel(true)
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun cancelNotification() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Download Notification"
            val descriptionText = "Channel untuk notifikasi ekspor PDF"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun savePdfToDownloads(pdfDocument: PdfDocument) {
        val sanitizedClassName = filterKelas?.replace(" ", "_") ?: "Semua_Kelas"
        val fileName = "Statistik_AUM_$sanitizedClassName.pdf"
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                
                val uri = requireContext().contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI, 
                    contentValues
                )
                
                uri?.let {
                    requireContext().contentResolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                        showDownloadNotification("Ekspor Selesai", 100)
                        Toast.makeText(context, "PDF tersimpan di folder Download", Toast.LENGTH_LONG).show()
                    }
                } ?: throw Exception("Gagal membuat URI MediaStore")
            } else {
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = java.io.File(downloadDir, fileName)
                java.io.FileOutputStream(file).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                    showDownloadNotification("Ekspor Selesai", 100)
                    Toast.makeText(context, "PDF tersimpan di: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        binding.rvSummaryStats.layoutManager = GridLayoutManager(requireContext(), 2)
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

        // 4. Line Chart
        setupLineChart(data)

        // 5. Data Table
        setupDataTable(data)
    }

    private fun setupLineChart(data: List<AumBidangHasil>) {
        val entries = data.mapIndexed { index, bidang ->
            Entry(index.toFloat(), bidang.pilihan.size.toFloat())
        }

        val dataSet = LineDataSet(entries, "Tren Masalah")
        dataSet.color = Color.parseColor("#5561F4")
        dataSet.setCircleColor(Color.parseColor("#5561F4"))
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(true)
        dataSet.valueTextSize = 10f
        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.parseColor("#5561F4")
        dataSet.fillAlpha = 30
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        val lineData = LineData(dataSet)

        binding.lineChart.apply {
            this.data = lineData
            description.isEnabled = false
            setPinchZoom(false)
            setDrawGridBackground(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(data.map { it.kodeBidang })
                setDrawGridLines(false)
                labelCount = data.size
                labelRotationAngle = -45f
            }

            axisLeft.apply {
                granularity = 1f
                axisMinimum = 0f
            }
            axisRight.isEnabled = false
            
            // Tambahkan jarak bawah agar label yang diputar tidak terpotong
            setExtraOffsets(0f, 0f, 0f, 16f)
            animateX(1000)
            invalidate()
        }
    }

    private fun setupDataTable(data: List<AumBidangHasil>) {
        val table = binding.tableDataAum
        // Remove all rows except header (index 0)
        val childCount = table.childCount
        if (childCount > 1) {
            table.removeViews(1, childCount - 1)
        }

        data.forEach { bidang ->
            val row = TableRow(requireContext())
            row.setPadding(8, 12, 8, 12)
            
            // Kode
            val tvKode = TextView(requireContext())
            tvKode.text = bidang.kodeBidang
            tvKode.setPadding(0, 0, 8, 0)
            
            // Nama
            val tvNama = TextView(requireContext())
            tvNama.text = bidang.namaBidang
            
            // Jumlah
            val tvJumlah = TextView(requireContext())
            tvJumlah.text = bidang.pilihan.size.toString()
            tvJumlah.gravity = Gravity.END

            row.addView(tvKode)
            row.addView(tvNama)
            row.addView(tvJumlah)
            
            table.addView(row)
        }
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
        
        // Pemetaan warna khusus untuk setiap kode bidang agar konsisten
        val colorMap = mapOf(
            "JDK" to Color.parseColor("#E63946"),
            "DPI" to Color.parseColor("#F4A261"),
            "KHK" to Color.parseColor("#2A9D8F"),
            "HSO" to Color.parseColor("#264653"),
            "KDP" to Color.parseColor("#457B9D"),
            "EDK" to Color.parseColor("#F97316"),
            "WSG" to Color.parseColor("#1D3557"),
            "ANM" to Color.parseColor("#10B981"),
            "HMP" to Color.parseColor("#9B5DE5"),
            "PDP" to Color.parseColor("#E76F51")
        )

        val chartColors = entries.map { entry ->
            colorMap[entry.label] ?: Color.GRAY
        }

        dataSet.colors = chartColors
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
            
            // Matikan legend bawaan karena kita pakai custom legend scrollable
            legend.isEnabled = false
            
            setExtraOffsets(20f, 0f, 20f, 10f)
            invalidate()
        }

        setupCustomLegend(entries, colorMap)
    }

    private fun setupCustomLegend(entries: List<PieEntry>, colorMap: Map<String, Int>) {
        val container = binding.legendContainer
        container.removeAllViews()

        entries.forEach { entry ->
            val color = colorMap[entry.label] ?: Color.GRAY
            
            val itemLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 24, 0)
                layoutParams = params
            }

            // Dot warna
            val colorView = View(requireContext()).apply {
                val size = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics
                ).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size)
                setBackgroundColor(color)
            }

            // Teks label
            val textView = TextView(requireContext()).apply {
                text = entry.label
                textSize = 12f
                setTextColor(Color.parseColor("#424242"))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 0, 0)
                layoutParams = params
            }

            itemLayout.addView(colorView)
            itemLayout.addView(textView)
            container.addView(itemLayout)
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
                labelCount = data.size
                labelRotationAngle = -45f
            }

            axisLeft.apply {
                granularity = 1f
                axisMinimum = 0f
            }
            axisRight.isEnabled = false
            
            // Tambahkan jarak bawah agar label yang diputar tidak terpotong
            setExtraOffsets(0f, 0f, 0f, 16f)
            animateY(1000)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 102 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            exportToPdf()
        } else if (requestCode == 102) {
            Toast.makeText(context, "Izin notifikasi ditolak, PDF akan diekspor tanpa notifikasi status", Toast.LENGTH_SHORT).show()
            // Kita bisa tetap lanjut tanpa notifikasi jika diinginkan, 
            // tapi di sini saya pilih biarkan user tahu kenapa tidak ada notifikasi.
        }
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
