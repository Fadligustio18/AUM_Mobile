package com.example.bknova.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.content.Context
import android.transition.TransitionManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.bknova.R

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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        
        val tvName = view.findViewById<TextView>(R.id.tv_name_home)
        val btnMulaiAum = view.findViewById<Button>(R.id.btn_mulai_aum)
        val btnLihatSosio = view.findViewById<Button>(R.id.btn_lihat_sosio)
        val btnMulaiGaya = view.findViewById<Button>(R.id.btn_mulai_gaya)
        
        setupExpandableCards(view)
        
        // Ambil nama dari SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", "User")
        tvName.text = name

        btnMulaiAum.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FormAumFragment())
                .addToBackStack(null)
                .commit()
        }

        btnLihatSosio.setOnClickListener {
            Toast.makeText(context, "Fitur Sosiografik akan segera hadir", Toast.LENGTH_SHORT).show()
        }

        btnMulaiGaya.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, formGayaBelajarFragment())
                .addToBackStack(null)
                .commit()
        }

        return view 
    }

    private fun setupExpandableCards(view: View) {
        val rootLayout = view.findViewById<ViewGroup>(R.id.linear_container_home)

        // Card AUM
        val headerAum = view.findViewById<RelativeLayout>(R.id.header_aum)
        val expandableAum = view.findViewById<LinearLayout>(R.id.expandable_aum)
        val arrowAum = view.findViewById<ImageView>(R.id.arrow_aum)
        
        headerAum.setOnClickListener {
            toggleCard(expandableAum, arrowAum, rootLayout)
        }

        // Card Sosio
        val headerSosio = view.findViewById<RelativeLayout>(R.id.header_sosio)
        val expandableSosio = view.findViewById<LinearLayout>(R.id.expandable_sosio)
        val arrowSosio = view.findViewById<ImageView>(R.id.arrow_sosio)

        headerSosio.setOnClickListener {
            toggleCard(expandableSosio, arrowSosio, rootLayout)
        }

        // Card Gaya
        val headerGaya = view.findViewById<RelativeLayout>(R.id.header_gaya)
        val expandableGaya = view.findViewById<LinearLayout>(R.id.expandable_gaya)
        val arrowGaya = view.findViewById<ImageView>(R.id.arrow_gaya)

        headerGaya.setOnClickListener {
            toggleCard(expandableGaya, arrowGaya, rootLayout)
        }
    }

    private fun toggleCard(expandableLayout: View, arrow: ImageView, root: ViewGroup) {
        val isVisible = expandableLayout.visibility == View.VISIBLE
        
        TransitionManager.beginDelayedTransition(root)
        
        if (isVisible) {
            expandableLayout.visibility = View.GONE
            arrow.animate().rotation(90f).start()
        } else {
            expandableLayout.visibility = View.VISIBLE
            arrow.animate().rotation(270f).start()
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
