package com.example.bknova.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.bknova.R
import com.example.bknova.databinding.FragmentCreateKuesionerBinding
import com.example.bknova.databinding.ItemCreateSoalBinding
import com.example.bknova.databinding.ItemCreateOpsiBinding
import com.example.bknova.model.*
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateKuesionerFragment : Fragment() {
    private var _binding: FragmentCreateKuesionerBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    
    private var listKelas = listOf<BkTask>()
    private var listTahun = listOf<TahunAjaran>()
    private var selectedKelasId: Int = -1
    private var selectedTahunId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateKuesionerBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())

        binding.btnBackCreate.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.btnAddSoal.setOnClickListener { addQuestionView() }
        binding.btnSaveKuesioner.setOnClickListener { saveKuesioner() }

        loadFormData()
        addQuestionView()

        return binding.root
    }

    private fun loadFormData() {
        val token = "Bearer ${sessionManager.getToken()}"
        
        // Load Kelas
        Aktor.bk.getMyBkTasks(token).enqueue(object : Callback<List<BkTask>> {
            override fun onResponse(call: Call<List<BkTask>>, response: Response<List<BkTask>>) {
                if (response.isSuccessful) {
                    listKelas = response.body() ?: emptyList()
                    binding.tilPilihKelas.editText?.setOnClickListener {
                        val names = listKelas.map { "${it.tingkat} ${it.namaKelas}" }
                        showSelectionBottomSheet("Pilih Kelas Sasaran", names) { index ->
                            selectedKelasId = listKelas[index].idKelas
                            binding.tilPilihKelas.editText?.setText(names[index])
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<BkTask>>, t: Throwable) {}
        })

        // Load Tahun Ajaran
        Aktor.academic.getTahunAjaran().enqueue(object : Callback<List<TahunAjaran>> {
            override fun onResponse(call: Call<List<TahunAjaran>>, response: Response<List<TahunAjaran>>) {
                if (response.isSuccessful) {
                    listTahun = response.body() ?: emptyList()
                    binding.tilPilihTahun.editText?.setOnClickListener {
                        val years = listTahun.map { "${it.tahun ?: "Tahun -"} (${it.semester ?: "-"})" }
                        showSelectionBottomSheet("Pilih Tahun Ajaran", years) { index ->
                            selectedTahunId = listTahun[index].id
                            binding.tilPilihTahun.editText?.setText(years[index])
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<TahunAjaran>>, t: Throwable) {
                Toast.makeText(context, "Gagal memuat tahun ajaran", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showSelectionBottomSheet(title: String, items: List<String>, onSelected: (Int) -> Unit) {
        val bottomSheet = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.layout_popup_selection, null)
        
        val tvTitle = view.findViewById<TextView>(R.id.tv_selection_title)
        val listView = view.findViewById<ListView>(R.id.lv_selection)
        
        tvTitle.text = title
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter
        
        listView.setOnItemClickListener { _, _, position, _ ->
            onSelected(position)
            bottomSheet.dismiss()
        }
        
        bottomSheet.setContentView(view)
        bottomSheet.show()
    }

    private fun addQuestionView() {
        val soalBinding = ItemCreateSoalBinding.inflate(layoutInflater, binding.containerSoal, true)
        val types = arrayOf("Pilihan Ganda", "Esai")
        
        soalBinding.tilTipe.editText?.setOnClickListener {
            showSelectionBottomSheet("Pilih Tipe Jawaban", types.toList()) { index ->
                val type = types[index]
                soalBinding.tilTipe.editText?.setText(type)
                
                if (type == "Pilihan Ganda") {
                    soalBinding.containerOpsi.visibility = View.VISIBLE
                    soalBinding.btnAddOpsi.visibility = View.VISIBLE
                    if (soalBinding.containerOpsi.childCount == 0) addOptionView(soalBinding.containerOpsi)
                } else {
                    soalBinding.containerOpsi.visibility = View.GONE
                    soalBinding.btnAddOpsi.visibility = View.GONE
                }
            }
        }
        
        soalBinding.btnAddOpsi.setOnClickListener { addOptionView(soalBinding.containerOpsi) }
        soalBinding.btnDeleteSoal.setOnClickListener { binding.containerSoal.removeView(soalBinding.root) }
    }

    private fun addOptionView(container: LinearLayout) {
        val opsiBinding = ItemCreateOpsiBinding.inflate(layoutInflater, container, true)
        opsiBinding.btnDeleteOpsi.setOnClickListener { container.removeView(opsiBinding.root) }
    }

    private fun saveKuesioner() {
        val judul = binding.tilJudul.editText?.text.toString()
        val deskripsi = binding.tilDeskripsi.editText?.text.toString()

        if (selectedKelasId == -1 || selectedTahunId == -1) {
            Toast.makeText(context, "Pilih Kelas dan Tahun Ajaran terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        if (judul.isEmpty()) {
            Toast.makeText(context, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val listSoal = mutableListOf<SoalKuesioner>()
        for (i in 0 until binding.containerSoal.childCount) {
            val view = binding.containerSoal.getChildAt(i)
            val tilPertanyaan = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.til_pertanyaan)
            val tilTipe = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.til_tipe)
            val containerOpsi = view.findViewById<LinearLayout>(R.id.container_opsi)
            
            val pertanyaan = tilPertanyaan.editText?.text.toString()
            val tipe = tilTipe.editText?.text.toString()
            if (pertanyaan.isEmpty()) continue
            
            val listOpsi = mutableListOf<OpsiKuesioner>()
            if (tipe == "Pilihan Ganda") {
                for (j in 0 until containerOpsi.childCount) {
                    val opsiView = containerOpsi.getChildAt(j)
                    val tilOpsi = opsiView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.til_opsi)
                    val teks = tilOpsi.editText?.text.toString()
                    if (teks.isNotEmpty()) listOpsi.add(OpsiKuesioner(teks = teks, urutan = j + 1))
                }
            }
            listSoal.add(SoalKuesioner(pertanyaan = pertanyaan, tipe = tipe, urutan = i + 1, opsi = listOpsi))
        }

        if (listSoal.isEmpty()) {
            Toast.makeText(context, "Tambahkan minimal satu pertanyaan", Toast.LENGTH_SHORT).show()
            return
        }

        val token = "Bearer ${sessionManager.getToken()}"
        val idUser = sessionManager.getUserId()
        val request = KuesionerCreateRequest(judul, deskripsi, selectedKelasId, selectedTahunId, listSoal)

        Aktor.kuesioner.createKuesioner(token, idUser, request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Kuesioner berhasil dipublikasikan ke kelas", Toast.LENGTH_SHORT).show()
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
