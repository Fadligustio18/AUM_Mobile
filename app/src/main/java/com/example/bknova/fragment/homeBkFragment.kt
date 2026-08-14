package com.example.bknova.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.bknova.R
import com.example.bknova.controller.AuthController
import com.google.android.material.card.MaterialCardView

class homeBkFragment : Fragment() {
    private lateinit var authController: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authController = AuthController(requireContext())
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
        val cardGaya = view.findViewById<MaterialCardView>(R.id.card_gaya_belajar_bk)

        // Set name from session
        tvName.text = authController.getName()

        // Set click listeners
        cardSiswa.setOnClickListener {
            Toast.makeText(context, "Membuka Kelola Data Siswa", Toast.LENGTH_SHORT).show()
        }
        cardAum.setOnClickListener {
            Toast.makeText(context, "Membuka Kelola Data AUM", Toast.LENGTH_SHORT).show()
        }
        cardSosio.setOnClickListener {
            Toast.makeText(context, "Membuka CRUD Sosiografik", Toast.LENGTH_SHORT).show()
        }
        cardGaya.setOnClickListener {
            Toast.makeText(context, "Membuka Kelola Gaya Belajar", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
