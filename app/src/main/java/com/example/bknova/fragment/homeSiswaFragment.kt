package com.example.bknova.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.content.Context
import android.view.animation.DecelerateInterpolator
import android.transition.TransitionManager
import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import com.example.bknova.R
import com.example.bknova.controller.AumController
import com.example.bknova.controller.AuthController
import com.example.bknova.activity.halaman_siswa_Activity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [homeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class homeFragment : Fragment() {
    private lateinit var aumController: AumController
    private lateinit var authController: AuthController
    private var isAumFinished = false

    // TODO: Rename parameter arguments, choose names that match
    private var param1: String? = null
    private var param2: String? = null

    override fun onResume() {
        super.onResume()
        // Pastikan Bottom Navigation muncul di halaman utama
        (activity as? halaman_siswa_Activity)?.setBottomNavigationVisibility(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_siswa, container, false)
        
        aumController = AumController()
        authController = AuthController(requireContext())

        val tvName = view.findViewById<TextView>(R.id.tv_name_home)
        val cardAum = view.findViewById<MaterialCardView>(R.id.card_aum)
        val cardSosio = view.findViewById<MaterialCardView>(R.id.card_sosio)
        val cardGaya = view.findViewById<MaterialCardView>(R.id.card_gaya)

        // Staggered Animation for Grid Items
        animateGridItems(cardAum, cardSosio, cardGaya)
        
        // Handle Window Insets for bottom padding
        val scrollView = view.findViewById<NestedScrollView>(R.id.scroll_view_home_siswa)
        val initialPaddingBottom = scrollView.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, initialPaddingBottom + systemBars.bottom)
            insets
        }
        
        // Ambil nama dari SharedPreferences
        val name = authController.getName()
        tvName.text = name

        checkAumStatus()

        cardAum.setOnClickListener {
            if (isAumFinished) {
                Toast.makeText(context, "Anda sudah mengisi instrumen ini", Toast.LENGTH_SHORT).show()
            } else {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FormAumFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        cardSosio.setOnClickListener {
            Toast.makeText(context, "Fitur Sosiografik akan segera hadir", Toast.LENGTH_SHORT).show()
        }


        return view 
    }

    private fun checkAumStatus() {
        val token = authController.getToken()
        val userId = authController.getUserId()

        if (token != null && userId != -1) {
            aumController.checkAumStatus(token, userId, object : AumController.AumCallback<Boolean> {
                override fun onSuccess(data: Boolean) {
                    if (isAdded) {
                        isAumFinished = data
                        if (data) {
                            updateAumUiFinished()
                        }
                    }
                }

                override fun onError(message: String) {
                    // Silent error for status check
                }
            })
        }
    }

    private fun updateAumUiFinished() {
        view?.let { root ->
            val ivIcon = root.findViewById<ImageView>(R.id.iv_icon_aum)
            val tvTitle = root.findViewById<TextView>(R.id.tv_title_aum)

            // Mengubah warna menjadi abu-abu
            val grayColor = ContextCompat.getColor(requireContext(), R.color.text_color_secondary)
            ivIcon.imageTintList = ColorStateList.valueOf(grayColor)
            tvTitle.setTextColor(grayColor)
        }
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



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment homeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            homeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
