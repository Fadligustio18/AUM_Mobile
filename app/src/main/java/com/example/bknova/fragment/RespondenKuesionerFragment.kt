package com.example.bknova.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageButton
import android.widget.LinearLayout
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
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.adapter.DaftarSiswaBkAdapter
import com.example.bknova.model.PaginatedResponse
import com.example.bknova.model.Siswa
import com.example.bknova.service.Aktor
import com.example.bknova.service.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RespondenKuesionerFragment : Fragment() {
    private lateinit var rv: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var sessionManager: SessionManager
    private lateinit var searchView: SearchView
    
    private var adapter: DaftarSiswaBkAdapter? = null
    private var listSiswaFull = listOf<Siswa>()
    private var listSiswaFiltered = listOf<Siswa>()
    private var searchQuery: String? = null
    private var currentPage = 1
    private val pageSize = 10
    private var totalPages = 1
    private var kuesionerId: Int = -1
    private var idKelas: Int = -1

    companion object {
        fun newInstance(kuesionerId: Int, idKelas: Int) = RespondenKuesionerFragment().apply {
            arguments = Bundle().apply {
                putInt("kuesioner_id", kuesionerId)
                putInt("id_kelas", idKelas)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kuesionerId = arguments?.getInt("kuesioner_id") ?: -1
        idKelas = arguments?.getInt("id_kelas") ?: -1
    }

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_responden_kuesioner, container, false)
        
        sessionManager = SessionManager(requireContext())
        rv = view.findViewById(R.id.rv_responden)
        swipeRefresh = view.findViewById(R.id.swipe_refresh_responden)
        progressBar = view.findViewById(R.id.pb_loading_responden)
        tvEmpty = view.findViewById(R.id.tv_empty_responden)
        searchView = view.findViewById(R.id.search_view_responden)
        val btnBack = view.findViewById<ImageView>(R.id.btn_back_responden)
        
        rv.layoutManager = LinearLayoutManager(context)

        val paginationBar = view.findViewById<View>(R.id.pagination_bar)
        ViewCompat.setOnApplyWindowInsetsListener(paginationBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }
        
        setupSearch()

        swipeRefresh.setOnRefreshListener {
            loadStudents()
        }
        
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        
        if (idKelas != -1) {
            loadStudents()
        } else {
            Toast.makeText(context, "ID Kelas tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
        
        return view
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterSiswa(newText)
                return true
            }
        })
    }

    private fun filterSiswa(query: String?) {
        searchQuery = query
        currentPage = 1
        loadStudents()
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
                loadStudents()
            }
        }
        btnNext?.setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++
                loadStudents()
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
                    loadStudents()
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

    private fun loadStudents() {
        if (!swipeRefresh.isRefreshing) {
            progressBar.visibility = View.VISIBLE
        }
        tvEmpty.visibility = View.GONE
        
        val token = "Bearer ${sessionManager.getToken()}"
        Aktor.dynamics.getSiswaByKelasPaged(token, idKelas, currentPage, pageSize).enqueue(object : Callback<PaginatedResponse<Siswa>> {
            override fun onResponse(call: Call<PaginatedResponse<Siswa>>, response: Response<PaginatedResponse<Siswa>>) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    if (response.isSuccessful) {
                        val paginatedResponse = response.body()
                        val listSiswa = paginatedResponse?.data ?: emptyList()
                        totalPages = paginatedResponse?.totalPages ?: 1
                        
                        listSiswaFull = listSiswa
                        listSiswaFiltered = if (searchQuery.isNullOrEmpty()) {
                            listSiswa
                        } else {
                            listSiswa.filter {
                                it.nama.contains(searchQuery!!, ignoreCase = true) || 
                                it.nisn.contains(searchQuery!!, ignoreCase = true)
                            }
                        }

                        if (adapter == null) {
                            setupRecyclerView(listSiswaFiltered)
                        } else {
                            adapter?.updateData(listSiswaFiltered)
                            rv.adapter = adapter
                        }
                        
                        rv.scrollToPosition(0)
                        updatePaginationUI()
                        tvEmpty.visibility = if (listSiswaFiltered.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<PaginatedResponse<Siswa>>, t: Throwable) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupRecyclerView(listSiswa: List<Siswa>) {
        adapter = DaftarSiswaBkAdapter(listSiswa, false) { siswa ->
            // Gunakan 'idSiswa' (ID Tabel Siswa) sesuai spesifikasi API monitoring Guru BK
            val idTarget = siswa.idSiswa ?: -1 
            
            if (idTarget != -1) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_bk, JawabanKuesionerSiswaFragment.newInstance(kuesionerId, idTarget))
                    .addToBackStack(null)
                    .commit()
            } else {
                Toast.makeText(context, "Data ID untuk ${siswa.nama} tidak valid", Toast.LENGTH_SHORT).show()
            }
        }
        rv.adapter = adapter
        rv.scheduleLayoutAnimation()
    }
}
