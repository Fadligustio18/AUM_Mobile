package com.example.bknova.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.example.bknova.R
import com.example.bknova.controller.AumController
import com.example.bknova.model.SoalMasalah

class FormAumFragment : Fragment() {
    private lateinit var aumController: AumController
    private var listSoal: List<SoalMasalah> = listOf()
    
    // Map untuk menyimpan jawaban siswa (Soal ID -> Boolean)
    private val selectedAnswers = mutableMapOf<Int, Boolean>()
    
    // Variabel untuk pagination
    private var currentBidangId = 1
    private val maxBidangId = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_form_aum_siswa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        aumController = AumController()
        
        val btnClose = view.findViewById<ImageView>(R.id.btn_close)
        val containerOptions = view.findViewById<LinearLayout>(R.id.container_options)
        val tvQuestionTitle = view.findViewById<TextView>(R.id.tv_question_title)
        val btnNext = view.findViewById<Button>(R.id.btn_next)
        val btnBack = view.findViewById<Button>(R.id.btn_back)
        val scrollView = view.findViewById<NestedScrollView>(R.id.scroll_view_content)
        
        val progressIndicator = view.findViewById<LinearLayout>(R.id.container_progress_bars)
        val tvProgressLabel = view.findViewById<TextView>(R.id.tv_progress_label)
        val tvProgressPercent = view.findViewById<TextView>(R.id.tv_progress_percent)

        // Sembunyikan Bottom Navigation secara aman
        activity?.findViewById<View>(R.id.bottom_nav_container)?.visibility = View.GONE

        btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnNext.setOnClickListener {
            if (currentBidangId < maxBidangId) {
                currentBidangId++
                updateSegmentedProgress(view, tvProgressLabel, tvProgressPercent)
                loadSoal(containerOptions, tvQuestionTitle)
                scrollView.scrollTo(0, 0)
                
                if (currentBidangId == maxBidangId) {
                    btnNext.text = "Submit"
                }
            } else {
                Toast.makeText(context, "Berhasil Submit AUM!", Toast.LENGTH_LONG).show()
                parentFragmentManager.popBackStack()
            }
        }

        btnBack.setOnClickListener {
            if (currentBidangId > 1) {
                currentBidangId--
                btnNext.text = getString(R.string.btn_selanjutnya)
                updateSegmentedProgress(view, tvProgressLabel, tvProgressPercent)
                loadSoal(containerOptions, tvQuestionTitle)
                scrollView.scrollTo(0, 0)
            } else {
                Toast.makeText(context, "Ini adalah halaman pertama", Toast.LENGTH_SHORT).show()
            }
        }

        updateSegmentedProgress(view, tvProgressLabel, tvProgressPercent)
        loadSoal(containerOptions, tvQuestionTitle)
    }

    private fun updateSegmentedProgress(root: View, label: TextView, percent: TextView) {
        val stepIds = listOf(
            R.id.step_1, R.id.step_2, R.id.step_3, R.id.step_4, R.id.step_5,
            R.id.step_6, R.id.step_7, R.id.step_8, R.id.step_9, R.id.step_10
        )

        for (i in stepIds.indices) {
            val stepView = root.findViewById<View>(stepIds[i])
            val colorRes = if (i < currentBidangId) R.color.blue_primary else R.color.blue_light
            stepView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorRes))
        }
        
        // Update Teks Label (Bisa disesuaikan dengan nama Bidang nanti)
        percent.text = "$currentBidangId/$maxBidangId"
        label.text = "Mengisi Bidang $currentBidangId"
    }

    private fun loadSoal(container: LinearLayout, tvTitle: TextView) {
        Log.d("AUM_DEBUG", "--- Loading Bidang ID: $currentBidangId ---")
        
        aumController.fetchSoalByBidang(currentBidangId, object : AumController.AumCallback<List<SoalMasalah>> {
            override fun onSuccess(data: List<SoalMasalah>) {
                if (isAdded && view != null) {
                    listSoal = data
                    Log.d("AUM_DEBUG", "Data Berhasil: ${data.size} soal")
                    
                    if (listSoal.isNotEmpty()) {
                        tvTitle.text = listSoal[0].bidangMasalah
                        renderSoal(container)
                    } else {
                        Log.w("AUM_DEBUG", "Data soal kosong dari server")
                        Toast.makeText(context, "Tidak ada data untuk bidang ini", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onError(message: String) {
                if (isAdded && context != null) {
                    Log.e("AUM_DEBUG", "Error Fetch: $message")
                    Toast.makeText(context, "Gagal memuat soal: $message", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun renderSoal(container: LinearLayout) {
        // Hapus semua checkbox sebelumnya
        container.removeAllViews()

        for (soal in listSoal) {
            val checkBox = CheckBox(requireContext())
            
            // Atur Properti CheckBox
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 16) // Tambah jarak antar item
            checkBox.layoutParams = params
            
            checkBox.setPadding(48, 48, 48, 48)
            checkBox.text = soal.pertanyaan
            checkBox.textSize = 16f
            checkBox.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_primary))
            checkBox.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue_primary))
            
            // Set Background Selector
            checkBox.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_item_aum)
            
            // Kembalikan status centang jika sebelumnya sudah diisi
            checkBox.isChecked = selectedAnswers[soal.id] ?: false

            // Simpan jawaban setiap kali dicentang
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                selectedAnswers[soal.id] = isChecked
                Log.d("AUM_DEBUG", "Soal ${soal.id} diubah ke: $isChecked")
            }

            // Tambahkan ke container
            container.addView(checkBox)
        }
        Log.d("AUM_DEBUG", "Berhasil render ${listSoal.size} CheckBox secara dinamis")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Tampilkan kembali container saat keluar
        activity?.findViewById<View>(R.id.bottom_nav_container)?.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FormAumFragment().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}
