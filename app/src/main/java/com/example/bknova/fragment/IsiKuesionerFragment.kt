package com.example.bknova.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bknova.R
import com.example.bknova.databinding.FragmentIsiKuesionerBinding
import com.example.bknova.model.*
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IsiKuesionerFragment : Fragment() {
    private var _binding: FragmentIsiKuesionerBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private var kuesionerId: Int = -1
    private var kuesionerDetail: KuesionerDetail? = null

    companion object {
        fun newInstance(id: Int) = IsiKuesionerFragment().apply {
            arguments = Bundle().apply { putInt("kuesioner_id", id) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kuesionerId = arguments?.getInt("kuesioner_id") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIsiKuesionerBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())

        binding.btnBackIsi.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.btnSubmitKuesioner.setOnClickListener { submitJawaban() }

        loadDetail()

        return binding.root
    }

    private fun loadDetail() {
        val token = "Bearer ${sessionManager.getToken()}"
        Aktor.kuesioner.getKuesionerDetailSiswa(token, kuesionerId).enqueue(object : Callback<KuesionerDetail> {
            override fun onResponse(call: Call<KuesionerDetail>, response: Response<KuesionerDetail>) {
                if (response.isSuccessful) {
                    kuesionerDetail = response.body()
                    renderQuestions()
                }
            }
            override fun onFailure(call: Call<KuesionerDetail>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun renderQuestions() {
        val detail = kuesionerDetail ?: return
        binding.tvDetailJudul.text = detail.judul
        binding.tvDetailDeskripsi.text = detail.deskripsi

        binding.containerPertanyaan.removeAllViews()
        
        detail.soal.forEachIndexed { index, soal ->
            val soalView = layoutInflater.inflate(R.layout.item_isi_soal, binding.containerPertanyaan, false)
            val tvNo = soalView.findViewById<TextView>(R.id.tv_no_soal)
            val tvPertanyaan = soalView.findViewById<TextView>(R.id.tv_pertanyaan)
            val containerJawaban = soalView.findViewById<LinearLayout>(R.id.container_jawaban)

            tvNo.text = "${index + 1}."
            tvPertanyaan.text = soal.pertanyaan

            val tipeSoal = soal.tipe.trim()

            if (tipeSoal.equals("Pilihan Ganda", ignoreCase = true)) {
                if (soal.opsi.isEmpty()) {
                    val tvError = TextView(requireContext())
                    tvError.text = "(Opsi kosong dari server)"
                    tvError.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_text))
                    containerJawaban.addView(tvError)
                } else {
                    val radioGroup = RadioGroup(requireContext())
                    radioGroup.orientation = RadioGroup.VERTICAL
                    radioGroup.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    
                    soal.opsi.forEach { opsi ->
                        val rb = RadioButton(requireContext())
                        rb.text = opsi.teks
                        rb.id = View.generateViewId()
                        rb.tag = opsi.id
                        rb.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_primary))
                        rb.layoutParams = RadioGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        radioGroup.addView(rb)
                    }
                    containerJawaban.addView(radioGroup)
                    containerJawaban.tag = radioGroup 
                }
            } else {
                val editText = EditText(requireContext())
                editText.hint = "Ketik jawaban di sini..."
                editText.setBackgroundResource(R.drawable.bg_input_essay)
                editText.setPadding(32, 32, 32, 32)
                editText.gravity = android.view.Gravity.TOP
                editText.minLines = 3
                containerJawaban.addView(editText)
                containerJawaban.tag = editText // Store reference
            }

            binding.containerPertanyaan.addView(soalView)
        }
    }

    private fun submitJawaban() {
        val detail = kuesionerDetail ?: return
        val listJawaban = mutableListOf<JawabanSubmitRequest>()

        for (i in 0 until binding.containerPertanyaan.childCount) {
            val soalView = binding.containerPertanyaan.getChildAt(i)
            val containerJawaban = soalView.findViewById<LinearLayout>(R.id.container_jawaban)
            val soal = detail.soal[i]

            if (soal.tipe.trim().equals("Pilihan Ganda", ignoreCase = true)) {
                val rg = containerJawaban.tag as RadioGroup
                val selectedId = rg.checkedRadioButtonId
                if (selectedId != -1) {
                    val rb = rg.findViewById<RadioButton>(selectedId)
                    val opsiId = rb.tag as Int
                    listJawaban.add(JawabanSubmitRequest(idSoal = soal.id!!, idOpsi = opsiId))
                }
            } else {
                val et = containerJawaban.tag as EditText
                val teks = et.text.toString()
                if (teks.isNotEmpty()) {
                    listJawaban.add(JawabanSubmitRequest(idSoal = soal.id!!, teksJawaban = teks))
                }
            }
        }

        if (listJawaban.size < detail.soal.size) {
            Toast.makeText(context, "Harap isi semua pertanyaan", Toast.LENGTH_SHORT).show()
            return
        }

        val token = "Bearer ${sessionManager.getToken()}"
        val idUser = sessionManager.getUserId()

        Aktor.kuesioner.submitKuesioner(token, idUser, kuesionerId, listJawaban).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Kuesioner berhasil dikirim", Toast.LENGTH_SHORT).show()
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
