package com.example.bknova.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.view.animation.DecelerateInterpolator
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bknova.R
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.controller.AuthController
import com.example.bknova.model.Tiket
import com.example.bknova.service.Aktor
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class homeBkFragment : Fragment() {
    private lateinit var authController: AuthController
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var tvBadgeTiket: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController(requireContext())
    }

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(true)
        fetchTiketBadge()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_bk, container, false)

        val tvName = view.findViewById<TextView>(R.id.tv_name_bk)
        val cardSiswa = view.findViewById<MaterialCardView>(R.id.card_data_siswa)
        val cardAum = view.findViewById<MaterialCardView>(R.id.card_data_aum)
        val cardSosio = view.findViewById<MaterialCardView>(R.id.card_sosio_bk)
        val cardTiket = view.findViewById<MaterialCardView>(R.id.card_tiket_bk)
        val containerTiket = view.findViewById<View>(R.id.container_card_tiket)
        tvBadgeTiket = view.findViewById(R.id.tv_badge_tiket)
        swipeRefresh = view.findViewById(R.id.swipe_refresh_home_bk)

        swipeRefresh.setOnRefreshListener {
            fetchTiketBadge()
        }

        // Staggered Animation for Grid Items (Animate the container for Tiket so the badge follows)
        animateGridItems(cardSosio, cardAum, containerTiket, cardSiswa)
        
        // Handle Window Insets for bottom padding
        val scrollView = view.findViewById<NestedScrollView>(R.id.scroll_view_home_bk)
        val initialPaddingBottom = scrollView.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, initialPaddingBottom + systemBars.bottom)
            insets
        }

        // Set name from session
        tvName.text = authController.getName()

        // Set click listeners
        cardSiswa.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragment_container_bk, DaftarKelasBkFragment())
                .addToBackStack(null)
                .commit()
        }
        cardAum.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragment_container_bk, DaftarKelasAumBkFragment())
                .addToBackStack(null)
                .commit()
        }
        cardSosio.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragment_container_bk, DaftarKuesionerBkFragment())
                .addToBackStack(null)
                .commit()
        }
        cardTiket.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragment_container_bk, DaftarTiketFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun fetchTiketBadge() {
        val token = authController.getToken() ?: return
        val idUser = authController.getUserId()
        if (idUser == -1) return

        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        Aktor.tiket.getTiketBk(bearerToken, idUser).enqueue(object : Callback<List<Tiket>> {
            override fun onResponse(call: Call<List<Tiket>>, response: Response<List<Tiket>>) {
                if (isAdded) {
                    swipeRefresh.isRefreshing = false
                    if (response.isSuccessful) {
                        val listTiket = response.body() ?: emptyList()
                        // Hitung tiket dengan status "Dikirim" (Pending)
                        val pendingCount = listTiket.count { it.status.equals("Dikirim", ignoreCase = true) }
                        
                        if (pendingCount > 0) {
                            tvBadgeTiket.text = if (pendingCount > 99) "99+" else pendingCount.toString()
                            tvBadgeTiket.visibility = View.VISIBLE
                        } else {
                            tvBadgeTiket.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Tiket>>, t: Throwable) {
                if (isAdded) {
                    swipeRefresh.isRefreshing = false
                }
            }
        })
    }

    private fun animateGridItems(vararg cards: View) {
        cards.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 100f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(index * 150L)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }
}
