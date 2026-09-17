package com.example.bknova.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageButton
import android.widget.LinearLayout
import android.graphics.Color
import android.view.Gravity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import androidx.appcompat.widget.SearchView
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.adapter.DaftarKelasBkAdapter
import com.example.bknova.controller.AuthController
import com.example.bknova.model.PaginatedResponse
import com.example.bknova.model.BkTask
import com.example.bknova.service.Aktor
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarKelasAumBkFragment : Fragment() {
    private lateinit var authController: AuthController
    private lateinit var rvKelas: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvEmpty: TextView
    private lateinit var searchView: SearchView
    private var adapter: DaftarKelasBkAdapter? = null
    private var listKelasFull = listOf<BkTask>()
    private var listKelasFiltered = listOf<BkTask>()
    private var searchQuery: String? = null
    private var currentPage = 1
    private val pageSize = 10
    private var totalPages = 1
    private var idKelas: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController(requireContext())
    }

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daftar_kelas_aum_bk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar_kelas_aum)
        rvKelas = view.findViewById(R.id.rv_daftar_kelas_aum)
        swipeRefresh = view.findViewById(R.id.swipe_refresh_kelas_aum)
        progressBar = view.findViewById(R.id.pb_loading_aum)
        tvEmpty = view.findViewById(R.id.tv_empty_aum)
        searchView = view.findViewById(R.id.search_view_kelas_aum)

        rvKelas.layoutManager = LinearLayoutManager(context)

        val paginationBar = view.findViewById<View>(R.id.pagination_bar)
        ViewCompat.setOnApplyWindowInsetsListener(paginationBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }

        setupSearch()

        swipeRefresh.setOnRefreshListener {
            fetchClasses()
        }

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fetchClasses()
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterKelas(newText)
                return true
            }
        })
    }

    private fun filterKelas(query: String?) {
        searchQuery = query
        currentPage = 1
        fetchClasses()
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
                fetchClasses()
            }
        }
        btnNext?.setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++
                fetchClasses()
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
                    fetchClasses()
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

    private fun fetchClasses() {
        if (!swipeRefresh.isRefreshing) {
            progressBar.visibility = View.VISIBLE
        }
        tvEmpty.visibility = View.GONE
        
        val token = authController.getToken() ?: return
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        Aktor.bk.getMyBkTasksPaged(bearerToken, currentPage, pageSize).enqueue(object : Callback<PaginatedResponse<BkTask>> {
            override fun onResponse(call: Call<PaginatedResponse<BkTask>>, response: Response<PaginatedResponse<BkTask>>) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    if (response.isSuccessful) {
                        val paginatedResponse = response.body()
                        val tasks = paginatedResponse?.data ?: emptyList()
                        totalPages = paginatedResponse?.totalPages ?: 1
                        
                        listKelasFull = tasks
                        listKelasFiltered = if (searchQuery.isNullOrEmpty()) {
                            tasks
                        } else {
                            tasks.filter {
                                it.namaKelas.contains(searchQuery!!, ignoreCase = true) || 
                                it.tingkat.contains(searchQuery!!, ignoreCase = true)
                            }
                        }

                        if (adapter == null) {
                            setupRecyclerView(listKelasFiltered)
                        } else {
                            adapter?.updateData(listKelasFiltered)
                            rvKelas.adapter = adapter
                        }
                        
                        rvKelas.scrollToPosition(0)
                        updatePaginationUI()
                        tvEmpty.visibility = if (listKelasFiltered.isEmpty()) View.VISIBLE else View.GONE
                    } else {
                        Toast.makeText(context, "Gagal memuat data: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<PaginatedResponse<BkTask>>, t: Throwable) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(context, "Kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupRecyclerView(tasks: List<BkTask>) {
        adapter = DaftarKelasBkAdapter(tasks) { _, task ->
            showOptionsBottomSheet(task)
        }
        rvKelas.adapter = adapter
    }

    private fun showOptionsBottomSheet(task: BkTask) {
        val bottomSheet = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.layout_popup_aum_options, null)

        view.findViewById<View>(R.id.btn_popup_statistik).setOnClickListener {
            bottomSheet.dismiss()
            navigateToStatistik(task)
        }

        view.findViewById<View>(R.id.btn_popup_siswa).setOnClickListener {
            bottomSheet.dismiss()
            navigateToDaftarSiswa(task)
        }

        bottomSheet.setContentView(view)
        bottomSheet.show()
    }

    private fun navigateToStatistik(task: BkTask) {
        val fullClassName = "${task.tingkat} ${task.namaKelas}"
        val fragment = StatistikAumFragment.newInstance(fullClassName, task.idKelas)
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_container_bk, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToDaftarSiswa(task: BkTask) {
        val fullClassName = "${task.tingkat} ${task.namaKelas}"
        val fragment = DaftarSiswaBkFragment.newInstance(task.idKelas, fullClassName, true)
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_container_bk, fragment)
            .addToBackStack(null)
            .commit()
    }
}
