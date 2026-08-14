package com.example.bknova.activity

import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_guru_bk)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation_bk)

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
                // Handle navSiswaBk later
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_bk, fragment)
            .commit()
    }
}
