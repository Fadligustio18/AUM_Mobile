package com.example.bknova.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.adapter.KuesionerAdapter
import com.example.bknova.model.PaginatedResponse
import com.example.bknova.model.BkTask
import com.example.bknova.model.KuesionerSummary
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarKuesionerBkFragment : Fragment() {
    private lateinit var rv: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: KuesionerAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var btnBack: ImageView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var searchView: SearchView
    private var listKelasBk = listOf<BkTask>()
    private var listKuesionerFull = listOf<KuesionerSummary>()
    private var listKuesionerFiltered = listOf<KuesionerSummary>()
    private var searchQuery: String? = null
    private var currentPage = 1
    private val pageSize = 10
    private var totalPages = 1

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daftar_kuesioner_bk, container, false)
        
        sessionManager = SessionManager(requireContext())
        rv = view.findViewById(R.id.rv_kuesioner)
        swipeRefresh = view.findViewById(R.id.swipe_refresh_kuesioner)
        btnBack = view.findViewById(R.id.btn_back_kuesioner)
        fabAdd = view.findViewById(R.id.fab_add_kuesioner)
        searchView = view.findViewById(R.id.search_view_kuesioner)
        
        rv.layoutManager = LinearLayoutManager(context)

        val paginationBar = view.findViewById<View>(R.id.pagination_bar)
        ViewCompat.setOnApplyWindowInsetsListener(paginationBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }
        
        setupSearch()

        swipeRefresh.setOnRefreshListener {
            loadKelasAndData()
        }
        adapter = KuesionerAdapter(
            list = emptyList(),
            onItemClick = { kuesioner ->
                handleKuesionerClick(kuesioner)
            }
        )
        rv.adapter = adapter
        
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        
        fabAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_bk, CreateKuesionerFragment())
                .addToBackStack(null)
                .commit()
        }
        
        loadKelasAndData()
        
        return view
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterKuesioner(newText)
                return true
            }
        })
    }

    private fun filterKuesioner(query: String?) {
        searchQuery = query
        currentPage = 1
        loadKuesionerList()
    }

    private fun updatePaginationUI() {
        val layoutPagination = view?.findViewById<View>(R.id.pagination_bar)
        val layoutPageNumbers = view?.findViewById<LinearLayout>(R.id.layout_page_numbers)
        val btnPrev = view?.findViewById<ImageButton>(R.id.btn_prev_page)
        val btnNext = view?.findViewById<ImageButton>(R.id.btn_next_page)

        if (totalPages <= 1) {
            layoutPagination?.visibility = View.GONE
            return
        }

        layoutPagination?.visibility = View.VISIBLE
        layoutPageNumbers?.removeAllViews()

        btnPrev?.isEnabled = currentPage > 1
        btnNext?.isEnabled = currentPage < totalPages
        btnPrev?.alpha = if (currentPage > 1) 1f else 0.5f
        btnNext?.alpha = if (currentPage < totalPages) 1f else 0.5f

        for (i in 1..totalPages) {
            if (i == 1 || i == totalPages || (i >= currentPage - 1 && i <= currentPage + 1)) {
                addPageNumber(i)
            } else if (i == currentPage - 2 || i == currentPage + 2) {
                addEllipsis()
            }
        }

        btnPrev?.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                loadKuesionerList()
            }
        }
        btnNext?.setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++
                loadKuesionerList()
            }
        }
    }

    private fun addPageNumber(page: Int) {
        val layoutPageNumbers = view?.findViewById<LinearLayout>(R.id.layout_page_numbers) ?: return
        val textView = TextView(requireContext()).apply {
            text = page.toString()
            val paddingSide = (12 * resources.displayMetrics.density).toInt()
            val paddingVert = (8 * resources.displayMetrics.density).toInt()
            setPadding(paddingSide, paddingVert, paddingSide, paddingVert)
            gravity = Gravity.CENTER
            textSize = 14f
            setTextColor(if (page == currentPage) Color.WHITE else Color.BLACK)
            if (page == currentPage) {
                setBackgroundResource(R.drawable.bg_page_active)
            }
            setOnClickListener {
                if (currentPage != page) {
                    currentPage = page
                    loadKuesionerList()
                }
            }
        }
        layoutPageNumbers.addView(textView)
    }

    private fun addEllipsis() {
        val layoutPageNumbers = view?.findViewById<LinearLayout>(R.id.layout_page_numbers) ?: return
        if (layoutPageNumbers.childCount > 0) {
            val lastChild = layoutPageNumbers.getChildAt(layoutPageNumbers.childCount - 1) as? TextView
            if (lastChild?.text == "...") return
        }
        
        val textView = TextView(requireContext()).apply {
            text = "..."
            val padding = (8 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
            gravity = Gravity.CENTER
            textSize = 14f
            setTextColor(Color.BLACK)
        }
        layoutPageNumbers.addView(textView)
    }

    private fun loadKelasAndData() {
        val token = "Bearer ${sessionManager.getToken()}"
        
        // Pertama, ambil daftar kelas Guru BK untuk referensi ID
        Aktor.bk.getMyBkTasks(token).enqueue(object : Callback<List<BkTask>> {
            override fun onResponse(call: Call<List<BkTask>>, response: Response<List<BkTask>>) {
                if (response.isSuccessful) {
                    listKelasBk = response.body() ?: emptyList()
                }
                // Setelah daftar kelas siap, baru ambil daftar kuesioner
                loadKuesionerList()
            }
            override fun onFailure(call: Call<List<BkTask>>, t: Throwable) {
                loadKuesionerList()
            }
        })
    }

    private fun loadKuesionerList() {
        val token = "Bearer ${sessionManager.getToken()}"
        val idUser = sessionManager.getUserId()
        
        Aktor.kuesioner.getKuesionerBkPaged(token, idUser, currentPage, pageSize).enqueue(object : Callback<PaginatedResponse<KuesionerSummary>> {
            override fun onResponse(call: Call<PaginatedResponse<KuesionerSummary>>, response: Response<PaginatedResponse<KuesionerSummary>>) {
                if (isAdded) {
                    swipeRefresh.isRefreshing = false
                    if (response.isSuccessful) {
                        val paginatedResponse = response.body()
                        val items = paginatedResponse?.data ?: emptyList()
                        totalPages = paginatedResponse?.totalPages ?: 1
                        
                        listKuesionerFull = items
                        listKuesionerFiltered = if (searchQuery.isNullOrEmpty()) {
                            items
                        } else {
                            items.filter {
                                it.judul.contains(searchQuery!!, ignoreCase = true) || 
                                it.kelas.contains(searchQuery!!, ignoreCase = true)
                            }
                        }
                        
                        adapter.updateData(listKuesionerFiltered)

                        // Mengontrol visibilitas Empty State View
                        val emptyStateLayout = view?.findViewById<LinearLayout>(R.id.layout_empty_state_kuesioner)
                        if (listKuesionerFiltered.isEmpty()) {
                            rv.visibility = View.GONE
                            emptyStateLayout?.visibility = View.VISIBLE
                        } else {
                            rv.visibility = View.VISIBLE
                            emptyStateLayout?.visibility = View.GONE
                        }

                        rv.scrollToPosition(0)
                        updatePaginationUI()
                    }
                }
            }
            override fun onFailure(call: Call<PaginatedResponse<KuesionerSummary>>, t: Throwable) {
                if (isAdded) {
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun handleKuesionerClick(kuesioner: KuesionerSummary) {
        if (listKelasBk.isEmpty()) {
            Toast.makeText(context, "Data kelas tugas belum siap, silakan refresh.", Toast.LENGTH_SHORT).show()
            return
        }

        // Parsing daftar nama kelas sasaran dari string 'kelas' di kuesioner (misal: "XII PPLG 1, XII PPLG 2")
        val targetClassNames = kuesioner.kelas.split(",").map { it.trim() }
        
        // Filter listKelasBk agar hanya menampilkan kelas yang memang menjadi sasaran kuesioner tersebut
        val filteredKelas = listKelasBk.filter { task ->
            val fullName = "${task.tingkat} ${task.namaKelas}"
            targetClassNames.any { it.equals(fullName, ignoreCase = true) }
        }

        if (filteredKelas.isEmpty()) {
            Toast.makeText(context, "Tidak ada data kelas sasaran yang cocok di tugas Anda.", Toast.LENGTH_SHORT).show()
            return
        }

        // Tampilkan bottom sheet untuk memilih salah satu kelas sasaran
        val bottomSheet = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.layout_popup_selection, null)
        
        val tvTitle = view.findViewById<TextView>(R.id.tv_selection_title)
        val listView = android.widget.ListView(requireContext()).apply {
            dividerHeight = (1 * resources.displayMetrics.density).toInt()
        }
        
        val container = view as LinearLayout
        container.removeView(view.findViewById(R.id.lv_selection))
        
        tvTitle.text = "Pilih Kelas Sasaran"
        
        val displayNames = filteredKelas.map { "${it.tingkat} ${it.namaKelas}" }
        val adapterSelection = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, displayNames)
        listView.adapter = adapterSelection
        
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedTask = filteredKelas[position]
            val namaKelasSelected = "${selectedTask.tingkat} ${selectedTask.namaKelas}"
            bottomSheet.dismiss()
            
            // Berpindah ke halaman responden berdasarkan kuesioner dan kelas yang dipilih secara spesifik
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_bk, RespondenKuesionerFragment.newInstance(kuesioner.id, selectedTask.idKelas, namaKelasSelected))
                .addToBackStack(null)
                .commit()
        }
        
        container.addView(listView)
        bottomSheet.setContentView(view)
        bottomSheet.show()
    }
}
