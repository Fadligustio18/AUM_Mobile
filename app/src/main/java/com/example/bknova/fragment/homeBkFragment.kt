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
import com.example.bknova.R
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.controller.AuthController
import com.google.android.material.card.MaterialCardView

class homeBkFragment : Fragment() {
    private lateinit var authController: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController(requireContext())
    }

    override fun onResume() {
        super.onResume()
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(true)
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

        // Staggered Animation for Grid Items
        animateGridItems(cardSosio, cardAum, cardTiket, cardSiswa)
        
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
            Toast.makeText(context, "Membuka CRUD Sosiografik", Toast.LENGTH_SHORT).show()
        }
        cardTiket.setOnClickListener {
            Toast.makeText(context, "Membuka Tiket Konseling", Toast.LENGTH_SHORT).show()
        }

        return view
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
