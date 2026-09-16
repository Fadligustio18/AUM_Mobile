package com.example.bknova.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bknova.R
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.adapter.DaftarSiswaBkAdapter
import com.example.bknova.controller.AuthController
import com.example.bknova.model.PaginatedResponse
import com.example.bknova.model.Siswa
import com.example.bknova.service.Aktor
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaftarSiswaBkFragment : Fragment() {
    private lateinit var authController: AuthController
    private lateinit var rvSiswa: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvEmpty: TextView
    private lateinit var searchView: SearchView
    
    private var adapter: DaftarSiswaBkAdapter? = null
    private var listSiswaFull = listOf<Siswa>()
    private var listSiswaFiltered = listOf<Siswa>()
    private var searchQuery: String? = null
    private var currentPage = 1
    private val pageSize = 10
    private var totalPages = 1
    private var idKelas: Int = -1
    private var namaKelas: String = ""
    private var isAumMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController(requireContext())
        arguments?.let {
            idKelas = it.getInt("id_kelas", -1)
            namaKelas = it.getString("nama_kelas", "")
            isAumMode = it.getBoolean("is_aum_mode", false)
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
        return inflater.inflate(R.layout.fragment_daftar_siswa_bk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar_siswa)
        rvSiswa = view.findViewById(R.id.rv_daftar_siswa)
        swipeRefresh = view.findViewById(R.id.swipe_refresh_siswa)
        progressBar = view.findViewById(R.id.pb_loading_siswa)
        tvEmpty = view.findViewById(R.id.tv_empty_siswa)
        searchView = view.findViewById(R.id.search_view_siswa)

        rvSiswa.layoutManager = LinearLayoutManager(context)

        val paginationBar = view.findViewById<View>(R.id.pagination_bar)
        ViewCompat.setOnApplyWindowInsetsListener(paginationBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }

        if (namaKelas.isNotEmpty()) {
            toolbar.title = "Siswa $namaKelas"
        }

        setupSearch()

        swipeRefresh.setOnRefreshListener {
            fetchStudents()
        }

        // Handle Window Insets for bottom padding
        val initialPaddingBottom = rvSiswa.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(rvSiswa) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, initialPaddingBottom + systemBars.bottom)
            insets
        }

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        if (idKelas != -1) {
            fetchStudents()
        }
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
        fetchStudents()
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
                fetchStudents()
            }
        }
        btnNext?.setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++
                fetchStudents()
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
                    fetchStudents()
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

    private fun fetchStudents() {
        if (!swipeRefresh.isRefreshing) {
            progressBar.visibility = View.VISIBLE
        }
        tvEmpty.visibility = View.GONE
        
        val token = authController.getToken() ?: return
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        Aktor.dynamics.getSiswaByKelasPaged(bearerToken, idKelas, currentPage, pageSize).enqueue(object : Callback<PaginatedResponse<Siswa>> {
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
                        }
                        
                        rvSiswa.scrollToPosition(0)
                        updatePaginationUI()
                        tvEmpty.visibility = if (listSiswaFiltered.isEmpty()) View.VISIBLE else View.GONE
                    } else {
                        Toast.makeText(context, "Gagal memuat data: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<PaginatedResponse<Siswa>>, t: Throwable) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(context, "Kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupRecyclerView(listSiswa: List<Siswa>) {
        adapter = DaftarSiswaBkAdapter(emptyList(), isAumMode) { siswa ->
            if (isAumMode) {
                // Gunakan idSiswa untuk memanggil API AUM per siswa
                siswa.idSiswa?.let { id ->
                    val fragment = DetailAumSiswaFragment.newInstance(id, siswa.nama, siswa.nisn, namaKelas)
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
            } else {
                // Copy NISN to clipboard only if NOT in AUM mode
                copyToClipboard(siswa.nisn)
            }
        }
        rvSiswa.adapter = adapter
        rvSiswa.scheduleLayoutAnimation()
    }

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("NISN", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "NISN $text disalin ke clipboard", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(idKelas: Int, namaKelas: String, isAumMode: Boolean = false) =
            DaftarSiswaBkFragment().apply {
                arguments = Bundle().apply {
                    putInt("id_kelas", idKelas)
                    putString("nama_kelas", namaKelas)
                    putBoolean("is_aum_mode", isAumMode)
                }
            }
    }
}
