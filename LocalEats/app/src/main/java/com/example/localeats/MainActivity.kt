package com.example.localeats

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.localeats.ui.FeedFragment
import com.example.localeats.ui.MapFragment
import com.example.localeats.ui.ProfileFragment
import com.example.localeats.ui.SearchFragment
import com.example.localeats.utils.LoginActivity
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }

    private val mapFragment = MapFragment()
    private val feedFragment = FeedFragment()
    private val profileFragment = ProfileFragment()
    private val searchFragment = SearchFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_map -> loadFragment(mapFragment)
                R.id.nav_feed -> loadFragment(feedFragment)
                R.id.nav_profile -> loadFragment(profileFragment)
            }
            true
        }

        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                getString(R.string.google_maps_key)
            )
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .commit()

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, searchFragment)
                .commit()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}