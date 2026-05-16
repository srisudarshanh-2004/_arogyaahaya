package com.arogyasahaya.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.arogyasahaya.R
import com.arogyasahaya.databinding.ActivityMainBinding
import com.arogyasahaya.notifications.RescheduleWorker
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity that hosts the bottom navigation bar and all 5 screens:
 * Home · Medicines · Vitals · ASHA Calendar · Profile
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Connect Navigation Component to bottom nav bar
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // Schedule the daily alarm reschedule worker (keeps alarms alive through Doze)
        RescheduleWorker.schedule(this)
    }
}
