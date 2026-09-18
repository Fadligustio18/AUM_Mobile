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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bknova.R
import com.example.bknova.activity.halaman_siswa_Activity
import com.example.bknova.adapter.TiketAdapter
import com.example.bknova.model.PaginatedResponse
import com.example.bknova.model.Tiket
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarTiketSiswaFragment : Fragment() {
    private lateinit var rvTiket: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: TiketAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var btnBack: ImageView
    private lateinit var searchView: SearchView
    private lateinit var chipGroup: com.google.android.material.chip.ChipGroup
    private var listTiketFull = listOf<Tiket>()
    private var listTiketFiltered = listOf<Tiket>()
    private var searchQuery: String? = null
    private var selectedStatus: String = "Semua"
    private var currentPage = 1
    private val pageSize = 10
    private var totalPages = 1

    override fun onResume() {
        super.onResume()
        (activity as? halaman_siswa_Activity)?.setBottomNavigationVisibility(false)
        loadTiket()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daftar_tiket_siswa, container, false)
        
        sessionManager = SessionManager(requireContext())
        rvTiket = view.findViewById(R.id.rv_tiket_siswa)
        swipeRefresh = view.findViewById(R.id.swipe_refresh_tiket_siswa)
        fabAdd = view.findViewById(R.id.fab_add_tiket)
        btnBack = view.findViewById(R.id.btn_back_tiket_siswa)
        searchView = view.findViewById(R.id.search_view_tiket_siswa)
        chipGroup = view.findViewById(R.id.cg_status_filter_siswa)
        
        rvTiket.layoutManager = LinearLayoutManager(context)
        
        val paginationBar = view.findViewById<View>(R.id.pagination_bar)
        ViewCompat.setOnApplyWindowInsetsListener(paginationBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }

        setupSearch()
        setupStatusFilter()

        swipeRefresh.setOnRefreshListener {
            loadTiket()
        }
        
        adapter = TiketAdapter(emptyList()) { tiket ->
            // Siswa can also see their own ticket detail
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DetailTiketFragment.newInstance(tiket))
                .addToBackStack(null)
                .commit()
        }
        rvTiket.adapter = adapter
        
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fabAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PengajuanTiketFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        
        loadTiket()
        
        return view
    }

    private fun setupStatusFilter() {
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull()
            if (checkedId != null) {
                val chip = group.findViewById<com.google.android.material.chip.Chip>(checkedId)
                selectedStatus = chip.text.toString()
                applyFilters()
            }
        }
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText
                applyFilters()
                return true
            }
        })
    }

    private fun applyFilters() {
        currentPage = 1
        loadTiket()
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
                loadTiket()
            }
        }
        btnNext?.setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++
                loadTiket()
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
                    loadTiket()
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

    private fun loadTiket() {
        swipeRefresh.isRefreshing = true
        val token = "Bearer ${sessionManager.getToken()}"
        val idUser = sessionManager.getUserId()
        
        Aktor.tiket.getTiketSiswaPaged(token, idUser, currentPage, pageSize).enqueue(object : Callback<PaginatedResponse<Tiket>> {
            override fun onResponse(call: Call<PaginatedResponse<Tiket>>, response: Response<PaginatedResponse<Tiket>>) {
                if (isAdded) {
                    swipeRefresh.isRefreshing = false
                    if (response.isSuccessful) {
                        val paginatedResponse = response.body()
                        val items = paginatedResponse?.data ?: emptyList()
                        totalPages = paginatedResponse?.totalPages ?: 1
                        
                        listTiketFull = items
                        listTiketFiltered = items.filter { tiket ->
                            val matchesSearch = if (searchQuery.isNullOrEmpty()) {
                                true
                            } else {
                                (tiket.bk?.contains(searchQuery!!, ignoreCase = true) ?: false) ||
                                tiket.judul.contains(searchQuery!!, ignoreCase = true)
                            }
                            
                            val matchesStatus = if (selectedStatus == "Semua") {
                                true
                            } else {
                                tiket.status.equals(selectedStatus, ignoreCase = true)
                            }
                            
                            matchesSearch && matchesStatus
                        }
                        if (adapter == null || rvTiket.adapter == null) {
                            adapter = TiketAdapter(listTiketFiltered) { tiket ->
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, DetailTiketFragment.newInstance(tiket))
                                    .addToBackStack(null)
                                    .commit()
                            }
                            rvTiket.adapter = adapter
                        } else {
                            adapter.updateData(listTiketFiltered)
                            rvTiket.adapter = adapter
                        }

                        // Mengontrol visibilitas Empty State View
                        val emptyStateLayout = view?.findViewById<LinearLayout>(R.id.layout_empty_state_tiket_siswa)
                        if (listTiketFiltered.isEmpty()) {
                            rvTiket.visibility = View.GONE
                            emptyStateLayout?.visibility = View.VISIBLE
                        } else {
                            rvTiket.visibility = View.VISIBLE
                            emptyStateLayout?.visibility = View.GONE
                        }

                        rvTiket.scrollToPosition(0)
                        updatePaginationUI()
                    } else {
                        Toast.makeText(context, "Gagal memuat status tiket", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<PaginatedResponse<Tiket>>, t: Throwable) {
                if (isAdded) {
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
