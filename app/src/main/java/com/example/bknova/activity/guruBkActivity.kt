package com.example.bknova.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bknova.R

import androidx.fragment.app.Fragment
import com.example.bknova.fragment.homeBkFragment
import com.example.bknova.fragment.profilBkFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class guruBkActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_guru_bk)
        
        bottomNavigation = findViewById(R.id.bottom_navigation_bk)
        val bottomNavContainer = findViewById<View>(R.id.bottom_nav_container_bk)
        
        // Initial bottom margin from layout (20dp)
        val initialBottomMargin = (bottomNavContainer.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Pad the root for status bar and side notches
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            
            // Apply bottom inset to the floating container margin
            val params = bottomNavContainer.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = initialBottomMargin + systemBars.bottom
            bottomNavContainer.layoutParams = params
            
            insets
        }

        // Set fragment default
        replaceFragment(homeBkFragment())

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHomeBk -> {
                    replaceFragment(homeBkFragment())
                    true
                }
                R.id.navProfilBk -> {
                    replaceFragment(profilBkFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun setBottomNavigationVisibility(isVisible: Boolean) {
        if (::bottomNavigation.isInitialized) {
            bottomNavigation.visibility = if (isVisible) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_bk, fragment)
            .commit()
    }
}
