package com.arogyasahaya.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.arogyasahaya.R
import com.arogyasahaya.databinding.ActivityOnboardingBinding
import com.arogyasahaya.ui.MainActivity
import com.arogyasahaya.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Simple 3-step onboarding:
 *   Step 1: Welcome screen
 *   Step 2: Fill in medical profile (name, age, conditions, emergency contact)
 *   Step 3: Success + go to MainActivity
 */
@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveProfile.setOnClickListener {
            val name  = binding.etName.text.toString().trim()
            val ageStr = binding.etAge.text.toString().trim()
            val conditions = binding.etConditions.text.toString().trim()
            val emergencyName  = binding.etEmergencyName.text.toString().trim()
            val emergencyPhone = binding.etEmergencyPhone.text.toString().trim()

            // Simple validation
            if (name.isEmpty()) {
                binding.etName.error = getString(R.string.name_required)
                return@setOnClickListener
            }
            if (ageStr.isEmpty()) {
                binding.etAge.error = getString(R.string.age_required)
                return@setOnClickListener
            }
            if (emergencyPhone.isEmpty()) {
                binding.etEmergencyPhone.error = getString(R.string.required)
                return@setOnClickListener
            }

            profileViewModel.saveProfile(
                name                 = name,
                age                  = ageStr.toIntOrNull() ?: 0,
                gender               = binding.spinnerGender.selectedItem.toString(),
                chronicConditions    = conditions,
                emergencyContactName = emergencyName,
                emergencyContactPhone = emergencyPhone,
                bloodGroup           = binding.spinnerBloodGroup.selectedItem.toString()
            )

            Toast.makeText(this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
