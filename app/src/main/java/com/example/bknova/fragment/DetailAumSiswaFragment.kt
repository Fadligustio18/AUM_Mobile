package com.example.bknova.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.adapter.AumHasilBidangAdapter
import com.example.bknova.adapter.AumStatistikAdapter
import com.example.bknova.controller.AuthController
import com.example.bknova.model.AumBidangHasil
import com.example.bknova.model.AumHasilSiswa
import com.example.bknova.service.Aktor
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class DetailAumSiswaFragment : Fragment() {
    private lateinit var authController: AuthController
    private lateinit var rvBidang: RecyclerView
    private lateinit var rvStatistik: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvEmpty: TextView
    
    private lateinit var tvNama: TextView
    private lateinit var tvKelas: TextView
    private lateinit var tvNis: TextView
    private lateinit var tvWaktu: TextView

    private lateinit var donutChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var lineChart: LineChart
    private lateinit var legendContainer: LinearLayout
    private lateinit var tableDataAum: TableLayout
    
    private lateinit var btnExport: MaterialButton
    private lateinit var nsvContent: NestedScrollView
    private lateinit var cardTable: View
    private lateinit var cardDonut: View
    private lateinit var cardBar: View
    private lateinit var cardLine: View
    private lateinit var sectionVisualTitle: View
    private lateinit var sectionStatsTitle: View
    private lateinit var sectionDetailTitle: View

    private var idSiswa: Int = -1
    private var namaSiswa: String? = null
    private var nisnSiswa: String? = null
    private var currentData: AumHasilSiswa? = null

    private val colorMap = mapOf(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController(requireContext())
        arguments?.let {
            idSiswa = it.getInt("id_siswa", -1)
            namaSiswa = it.getString("nama_siswa")
            nisnSiswa = it.getString("nisn_siswa")
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_aum_siswa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar_detail_aum)
        rvBidang = view.findViewById(R.id.rv_bidang_aum)
        rvStatistik = view.findViewById(R.id.rv_statistik_aum)
        progressBar = view.findViewById(R.id.pb_detail_aum)
        tvEmpty = view.findViewById(R.id.tv_empty_detail_aum)
        
        tvNama = view.findViewById(R.id.tv_detail_nama)
        tvKelas = view.findViewById(R.id.tv_detail_kelas)
        tvNis = view.findViewById(R.id.tv_detail_nis)
        tvWaktu = view.findViewById(R.id.tv_detail_waktu)

        donutChart = view.findViewById(R.id.donut_chart)
        barChart = view.findViewById(R.id.bar_chart)
        lineChart = view.findViewById(R.id.line_chart)
        legendContainer = view.findViewById(R.id.legend_container)
        tableDataAum = view.findViewById(R.id.table_data_aum)
        btnExport = view.findViewById(R.id.btn_export_pdf)
        nsvContent = view.findViewById(R.id.nsv_detail_aum)
        
        cardTable = view.findViewById(R.id.card_table)
        cardDonut = view.findViewById(R.id.card_donut)
        cardBar = view.findViewById(R.id.card_bar)
        cardLine = view.findViewById(R.id.card_line)
        
        sectionVisualTitle = view.findViewById(R.id.tv_title_visual)
        sectionStatsTitle = view.findViewById(R.id.tv_title_stats)
        sectionDetailTitle = view.findViewById(R.id.tv_title_detail)

        rvBidang.layoutManager = LinearLayoutManager(context)
        rvStatistik.layoutManager = GridLayoutManager(context, 2)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        if (idSiswa != -1) {
            // Tampilkan data profil dasar segera dari arguments agar tidak kosong saat loading
            tvNama.text = namaSiswa ?: "Siswa"
            tvNis.text = if (!nisnSiswa.isNullOrBlank()) "NISN: $nisnSiswa" else "-"
            toolbar.title = "AUM ${namaSiswa ?: "Siswa"}"
            
            fetchDetailAum()
        }

        btnExport.setOnClickListener {
            if (currentData != null) {
                exportToPdf()
            } else {
                Toast.makeText(context, "Data belum dimuat", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exportToPdf() {
        val child = nsvContent.getChildAt(0) ?: return
        val height = child.height
        val width = child.width

        if (height <= 0 || width <= 0) {
            Toast.makeText(context, "Data belum siap", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(context, "Menyiapkan PDF...", Toast.LENGTH_SHORT).show()

        try {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            
            // Tambahkan latar belakang putih eksplisit
            canvas.drawColor(Color.WHITE)
            
            // Simpan state alpha asli dan paksa alpha = 1 agar konten terlihat di PDF
            val animatedViews = listOf(
                sectionVisualTitle, cardTable, cardDonut, cardBar, cardLine,
                sectionStatsTitle, rvStatistik, sectionDetailTitle, rvBidang
            )
            val originalAlphas = animatedViews.map { it to it.alpha }
            animatedViews.forEach { it.alpha = 1f }

            child.draw(canvas)
            
            // Kembalikan alpha ke state semula
            originalAlphas.forEach { (view, alpha) -> view.alpha = alpha }

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            
            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDocument.finishPage(page)

            val pdfUri = savePdfToStorage(pdfDocument)
            
            if (pdfUri != null) {
                sharePdf(pdfUri)
            }
            
            pdfDocument.close()
            bitmap.recycle()
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal memproses PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun savePdfToStorage(pdfDocument: PdfDocument): Uri? {
        val sanitizedNama = currentData?.nama?.replace(" ", "_") ?: "Siswa"
        val fileName = "AUM_$sanitizedNama.pdf"
        var resultUri: Uri? = null
        
        try {
            // Simpan ke cache agar bisa dishare tanpa memenuhi folder Download secara otomatis
            // User bisa memilih "Simpan ke File" di menu Share Android jika ingin mendownload
            val cacheDir = File(requireContext().externalCacheDir, "pdf")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            
            val file = File(cacheDir, fileName)
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
                
                resultUri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    file
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal menyiapkan file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return resultUri
    }

    private fun sharePdf(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // Menambahkan ClipData agar pratinjau file muncul di atas Share Sheet (Android 10+)
            clipData = ClipData.newRawUri("", uri)
        }
        
        val chooser = Intent.createChooser(shareIntent, "Bagikan Hasil AUM")
        startActivity(chooser)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun fetchDetailAum() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        
        val token = authController.getToken() ?: return
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        
        // Prioritas 1: Fetch langsung berdasarkan ID Siswa (Identifier Paling Unik)
        if (idSiswa != -1) {
            Aktor.aum.getHasilAumBySiswaId(bearerToken, idSiswa).enqueue(object : Callback<AumHasilSiswa> {
                override fun onResponse(call: Call<AumHasilSiswa>, response: Response<AumHasilSiswa>) {
                    if (isAdded) {
                        if (response.isSuccessful && response.body() != null) {
                            progressBar.visibility = View.GONE
                            displayData(response.body()!!)
                        } else {
                            // Jika ID gagal, coba fallback ke NISN
                            fetchByNisn(bearerToken)
                        }
                    }
                }

                override fun onFailure(call: Call<AumHasilSiswa>, t: Throwable) {
                    if (isAdded) fetchByNisn(bearerToken)
                }
            })
        } else {
            fetchByNisn(bearerToken)
        }
    }

    private fun fetchByNisn(bearerToken: String) {
        val nisn = nisnSiswa
        if (!nisn.isNullOrBlank()) {
            Aktor.aum.getHasilAumByNisn(bearerToken, nisn).enqueue(object : Callback<AumHasilSiswa> {
                override fun onResponse(call: Call<AumHasilSiswa>, response: Response<AumHasilSiswa>) {
                    if (isAdded) {
                        if (response.isSuccessful && response.body() != null) {
                            progressBar.visibility = View.GONE
                            displayData(response.body()!!)
                        } else {
                            fetchDetailAumFallback(bearerToken)
                        }
                    }
                }

                override fun onFailure(call: Call<AumHasilSiswa>, t: Throwable) {
                    if (isAdded) fetchDetailAumFallback(bearerToken)
                }
            })
        } else {
            fetchDetailAumFallback(bearerToken)
        }
    }

    private fun fetchDetailAumFallback(bearerToken: String) {
        val idGuru = authController.getUserId()

        Aktor.aum.getHasilAumByGuru(bearerToken, idGuru).enqueue(object : Callback<List<AumHasilSiswa>> {
            override fun onResponse(call: Call<List<AumHasilSiswa>>, response: Response<List<AumHasilSiswa>>) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val listHasil = response.body()
                        val currentNisn = nisnSiswa

                        // Urutkan berdasarkan waktu mengisi terbaru
                        val sortedList = listHasil?.sortedByDescending { it.waktuMengisi }

                        // Pengecekan ID Siswa secara eksklusif (TIDAK ADA FUZZY NAME SEARCH)
                        val result = sortedList?.find { 
                            (idSiswa != -1 && it.idSiswa == idSiswa) || 
                            (!currentNisn.isNullOrBlank() && it.nisn.trim() == currentNisn.trim())
                        }
                        
                        if (result != null) {
                            displayData(result)
                        } else {
                            tvEmpty.visibility = View.VISIBLE
                            tvEmpty.text = "Siswa ini belum mengisi instrumen AUM"
                        }
                    } else {
                        tvEmpty.visibility = View.VISIBLE
                        tvEmpty.text = "Gagal memuat data AUM (${response.code()})"
                    }
                }
            }

            override fun onFailure(call: Call<List<AumHasilSiswa>>, t: Throwable) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                    tvEmpty.text = "Kesalahan koneksi: ${t.message}"
                }
            }
        })
    }

    private fun displayData(data: AumHasilSiswa) {
        currentData = data
        toolbar.title = "AUM ${data.nama}"
        tvNama.text = data.nama
        tvKelas.text = "Kelas: ${data.tingkat} ${data.kelas}"
        tvNis.text = "${data.nis} / ${data.nisn}"
        tvWaktu.text = data.waktuMengisi
        
        // Setup Detail Bidang
        val adapterBidang = AumHasilBidangAdapter(data.bidang)
        rvBidang.adapter = adapterBidang

        // Setup Statistik
        val adapterStatistik = AumStatistikAdapter(data.bidang)
        rvStatistik.adapter = adapterStatistik

        // Update Charts
        setupDataTable(data.bidang)
        setupDonutChart(data.bidang)
        setupBarChart(data.bidang)
        setupLineChart(data.bidang)
        
        animateViews()
    }

    private fun animateViews() {
        val viewsToAnimate = listOf(
            sectionVisualTitle,
            cardTable,
            cardDonut,
            cardBar,
            cardLine,
            sectionStatsTitle,
            rvStatistik,
            sectionDetailTitle,
            rvBidang
        )

        viewsToAnimate.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 50f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(index * 100L)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    private fun setupDataTable(data: List<AumBidangHasil>) {
        // Remove all rows except header (index 0)
        val childCount = tableDataAum.childCount
        if (childCount > 1) {
            tableDataAum.removeViews(1, childCount - 1)
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
            
            tableDataAum.addView(row)
        }
    }

    private fun setupDonutChart(data: List<AumBidangHasil>) {
        val entries = data.filter { it.pilihan.isNotEmpty() }.map {
            PieEntry(it.pilihan.size.toFloat(), it.kodeBidang)
        }

        if (entries.isEmpty()) {
            donutChart.setNoDataText("Tidak ada data masalah")
            donutChart.data = null
            donutChart.invalidate()
            legendContainer.removeAllViews()
            return
        }

        val dataSet = PieDataSet(entries, "")
        val chartColors = entries.map { entry ->
            colorMap[entry.label] ?: Color.GRAY
        }

        dataSet.colors = chartColors
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.WHITE

        val pieData = PieData(dataSet)
        
        donutChart.apply {
            this.data = pieData
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setCenterText("Total\nMasalah")
            setCenterTextSize(14f)
            animateY(1000)
            legend.isEnabled = false
            setExtraOffsets(10f, 0f, 10f, 0f)
            invalidate()
        }

        setupCustomLegend(entries)
    }

    private fun setupCustomLegend(entries: List<PieEntry>) {
        legendContainer.removeAllViews()

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

            val colorView = View(requireContext()).apply {
                val size = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
                ).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size)
                setBackgroundColor(color)
            }

            val textView = TextView(requireContext()).apply {
                text = entry.label
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
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
            legendContainer.addView(itemLayout)
        }
    }

    private fun setupBarChart(data: List<AumBidangHasil>) {
        val entries = data.mapIndexed { index, bidang ->
            BarEntry(index.toFloat(), bidang.pilihan.size.toFloat())
        }

        val dataSet = BarDataSet(entries, "Jumlah Masalah")
        dataSet.color = Color.parseColor("#5561F4")
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f

        barChart.apply {
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
            setExtraOffsets(0f, 0f, 0f, 10f)
            animateY(1000)
            invalidate()
        }
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

        lineChart.apply {
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
            setExtraOffsets(0f, 0f, 0f, 10f)
            animateX(1000)
            invalidate()
        }
    }

    companion object {
        fun newInstance(idSiswa: Int, namaSiswa: String, nisnSiswa: String? = null) =
            DetailAumSiswaFragment().apply {
                arguments = Bundle().apply {
                    putInt("id_siswa", idSiswa)
                    putString("nama_siswa", namaSiswa)
                    putString("nisn_siswa", nisnSiswa)
                }
            }
    }
}