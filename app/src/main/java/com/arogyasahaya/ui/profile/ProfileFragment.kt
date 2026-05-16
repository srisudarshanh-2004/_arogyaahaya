package com.arogyasahaya.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.arogyasahaya.R
import com.arogyasahaya.databinding.FragmentProfileBinding
import com.arogyasahaya.ui.auth.LoginActivity
import com.arogyasahaya.viewmodel.AuthViewModel
import com.arogyasahaya.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var isEditing = false
    private var currentLangCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLanguageSpinner()

        profileViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            if (_binding == null || profile == null) return@observe

            binding.etName.setText(profile.name)
            binding.etAge.setText(if (profile.age > 0) profile.age.toString() else "")
            binding.etConditions.setText(profile.chronicConditions)
            binding.etEmergencyName.setText(profile.emergencyContactName)
            binding.etEmergencyPhone.setText(profile.emergencyContactPhone)
            binding.tvBloodGroup.text = getString(R.string.blood_group_label, profile.bloodGroup.ifBlank { "—" })
            
            // Set switch state without triggering listener
            binding.switchDarkMode.setOnCheckedChangeListener(null)
            binding.switchDarkMode.isChecked = profile.isDarkMode
            setupDarkModeListener()

            // Handle Language Selection
            val langIndex = when(profile.languageCode) {
                "hi" -> 1
                "kn" -> 2
                else -> 0
            }
            
            if (currentLangCode == null) {
                currentLangCode = profile.languageCode
                binding.spinnerLanguage.setSelection(langIndex, false)
            }

            // Show login info
            val loginInfo = when {
                profile.email.isNotBlank()  -> profile.email
                profile.mobile.isNotBlank() -> profile.mobile
                else -> ""
            }
            if (loginInfo.isNotEmpty()) {
                binding.tvLoginInfo.text = getString(R.string.logged_in_with, loginInfo)
                binding.tvLoginInfo.visibility = View.VISIBLE
            } else {
                binding.tvLoginInfo.visibility = View.GONE
            }
        }

        binding.btnEditSave.setOnClickListener {
            if (!isEditing) {
                isEditing = true
                binding.btnEditSave.text = getString(R.string.save_profile)
                enableEditing(true)
            } else {
                saveProfile()
            }
        }

        binding.btnLogout.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.logout_confirm_title))
                .setMessage(getString(R.string.logout_confirm_message))
                .setPositiveButton(getString(R.string.logout)) { _, _ ->
                    authViewModel.logout()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }

        enableEditing(false)
        setupDarkModeListener()
    }

    private fun setupLanguageSpinner() {
        val languages = arrayOf("English", "Hindi (हिन्दी)", "Kannada (ಕನ್ನಡ)")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)
        binding.spinnerLanguage.adapter = adapter

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLangCode = when (position) {
                    1 -> "hi"
                    2 -> "kn"
                    else -> "en"
                }

                if (currentLangCode != null && selectedLangCode != currentLangCode) {
                    currentLangCode = selectedLangCode
                    profileViewModel.updateLanguage(selectedLangCode)
                    // Use AppCompatDelegate for stable switching
                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(selectedLangCode)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupDarkModeListener() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            profileViewModel.updateDarkMode(isChecked)
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    private fun enableEditing(enabled: Boolean) {
        binding.etName.isEnabled           = enabled
        binding.etAge.isEnabled            = enabled
        binding.etConditions.isEnabled     = enabled
        binding.etEmergencyName.isEnabled  = enabled
        binding.etEmergencyPhone.isEnabled = enabled
    }

    private fun saveProfile() {
        val name  = binding.etName.text.toString().trim()
        val age   = binding.etAge.text.toString().trim()
        val phone = binding.etEmergencyPhone.text.toString().trim()

        if (name.isEmpty()) { binding.etName.error = getString(R.string.name_required); return }
        if (age.isEmpty())  { binding.etAge.error  = getString(R.string.age_required);  return }
        if (phone.isEmpty()) { binding.etEmergencyPhone.error = getString(R.string.required); return }

        profileViewModel.saveProfile(
            name                  = name,
            age                   = age.toIntOrNull() ?: 0,
            gender                = "",
            chronicConditions     = binding.etConditions.text.toString().trim(),
            emergencyContactName  = binding.etEmergencyName.text.toString().trim(),
            emergencyContactPhone = phone,
            bloodGroup            = ""
        )
        isEditing = false
        binding.btnEditSave.text = getString(R.string.edit_profile)
        enableEditing(false)
        Toast.makeText(requireContext(), getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
