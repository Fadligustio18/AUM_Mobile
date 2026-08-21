package com.example.bknova.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.bknova.R
import com.example.bknova.fragment.homeFragment
import com.example.bknova.fragment.profilFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class halaman_siswa_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_halaman_siswa)
        
        val mainView = findViewById<View>(R.id.main)
        val bottomNavContainer = findViewById<View>(R.id.bottom_nav_container)
        
        // Initial bottom margin from layout
        val initialBottomMargin = (bottomNavContainer.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin

        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Pad the root for status bar and side notches
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            
            // Apply bottom inset to the floating bottom nav container
            val params = bottomNavContainer.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = initialBottomMargin + systemBars.bottom
            bottomNavContainer.layoutParams = params

            insets
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set fragment default saat pertama kali dibuka
        replaceFragment(homeFragment())

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(homeFragment())
                    true
                }
                R.id.nav_profil -> {
                    replaceFragment(profilFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun setBottomNavigationVisibility(isVisible: Boolean) {
        val bottomNavContainer = findViewById<View>(R.id.bottom_nav_container)
        if (bottomNavContainer != null) {
            bottomNavContainer.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}