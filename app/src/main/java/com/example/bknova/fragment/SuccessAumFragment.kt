package com.example.bknova.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.bknova.R
import com.example.bknova.activity.guruBkActivity
import com.example.bknova.activity.halaman_siswa_Activity
import com.example.bknova.controller.AuthController
import java.text.SimpleDateFormat
import java.util.*

class SuccessAumFragment : Fragment() {

    private lateinit var authController: AuthController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_success_aum, container, false)
    }

    override fun onResume() {
        super.onResume()
        // Sembunyikan Bottom Navigation secara paksa saat fragment aktif
        (activity as? halaman_siswa_Activity)?.setBottomNavigationVisibility(false)
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authController = AuthController(requireContext())

        // Set Data
        val tvDate = view.findViewById<TextView>(R.id.tv_date_success)
        
        val sdf = SimpleDateFormat("MMM dd yyyy EEEE", Locale.ENGLISH)
        tvDate?.text = sdf.format(Date())

        // Listeners
        view.findViewById<Button>(R.id.btn_continue_success)?.setOnClickListener {
            // Kembali ke halaman sebelumnya (Daftar AUM/Home)
            parentFragmentManager.popBackStack()
        }

        // Animasi Centang (Pop-in effect)
        val ivCheck = view.findViewById<ImageView>(R.id.iv_check_success)
        ivCheck?.apply {
            scaleX = 0f
            scaleY = 0f
            alpha = 0f
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(300)
                .setInterpolator(OvershootInterpolator(2f))
                .start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Tampilkan kembali Bottom Navigation saat keluar
        (activity as? halaman_siswa_Activity)?.setBottomNavigationVisibility(true)
        (activity as? guruBkActivity)?.setBottomNavigationVisibility(true)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SuccessAumFragment()
    }
}
