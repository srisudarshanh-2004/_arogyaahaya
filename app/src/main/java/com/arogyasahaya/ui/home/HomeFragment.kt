package com.arogyasahaya.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.arogyasahaya.R
import com.arogyasahaya.databinding.FragmentHomeBinding
import com.arogyasahaya.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Home screen — shows:
 *   • Greeting with user's name
 *   • Today's medicine summary (how many taken / total)
 *   • Large RED SOS emergency button
 *   • Quick links to other screens
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show user's name in greeting
        homeViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            if (_binding == null) return@observe
            profile?.let {
                binding.tvGreeting.text = getString(R.string.greeting_format, it.name)
                if (it.emergencyContactName.isNotBlank() && it.emergencyContactPhone.isNotBlank()) {
                    binding.tvEmergencyContact.text = getString(
                        R.string.emergency_contact_format,
                        it.emergencyContactName,
                        it.emergencyContactPhone
                    )
                } else {
                    binding.tvEmergencyContact.text = getString(R.string.emergency_contact_none)
                }
            }
        }

        // Show how many medicines are active today
        homeViewModel.medicines.observe(viewLifecycleOwner) { medicines ->
            if (_binding == null) return@observe
            binding.tvMedicineCount.text = getString(R.string.medicine_count_format, medicines.size)
        }

        // Show adherence rate
        homeViewModel.adherenceRate.observe(viewLifecycleOwner) { rate ->
            if (_binding == null) return@observe
            binding.tvAdherenceRate.text = getString(R.string.adherence_rate_format, rate.toInt())
        }

        // Trigger adherence rate calculation
        homeViewModel.loadAdherenceRate()

        // ── SOS BUTTON ──────────────────────────────────────────────────────────
        binding.btnSos.setOnClickListener {
            triggerSOS()
        }
    }

    /**
     * Simulates an emergency SOS: shows the number and tries to dial it.
     */
    private fun triggerSOS() {
        val profile = homeViewModel.userProfile.value
        val phone = profile?.emergencyContactPhone

        if (phone.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.sos_no_contact),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Show the SOS dialog
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.sos_dialog_title))
            .setMessage(getString(R.string.sos_dialog_message, profile.emergencyContactName, phone))
            .setPositiveButton(getString(R.string.sos_call_now)) { _, _ ->
                // Try to make a phone call (requires CALL_PHONE permission)
                if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val callIntent = Intent(Intent.ACTION_CALL).apply {
                        data = Uri.parse("tel:$phone")
                    }
                    startActivity(callIntent)
                } else {
                    // Fallback: open dialler with number pre-filled
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$phone")
                    }
                    startActivity(dialIntent)
                }
            }
            .setNegativeButton(getString(R.string.sos_cancel), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
