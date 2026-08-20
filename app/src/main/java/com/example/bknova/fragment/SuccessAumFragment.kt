package com.example.bknova.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.bknova.R

class SuccessAumFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_success_aum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sembunyikan Bottom Navigation
        activity?.findViewById<View>(R.id.bottom_nav_container)?.visibility = View.GONE

        view.findViewById<Button>(R.id.btn_continue).setOnClickListener {
            // Kembali ke halaman utama atau profil
            activity?.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Tampilkan kembali Bottom Navigation saat keluar
        activity?.findViewById<View>(R.id.bottom_nav_container)?.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance() = SuccessAumFragment()
    }
}
