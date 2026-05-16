package com.arogyasahaya.ui.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arogyasahaya.R
import com.arogyasahaya.databinding.FragmentMedicineBinding
import com.arogyasahaya.viewmodel.MedicineViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Shows a list of all medicines with their schedule.
 * FAB opens AddEditMedicineFragment to add a new medicine.
 */
@AndroidEntryPoint
class MedicineFragment : Fragment() {

    private var _binding: FragmentMedicineBinding? = null
    private val binding get() = _binding!!

    private val medicineViewModel: MedicineViewModel by viewModels()
    private lateinit var adapter: MedicineAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        adapter = MedicineAdapter(
            onEdit   = { medicine -> findNavController().navigate(
                R.id.action_medicineFragment_to_addEditMedicineFragment,
                Bundle().apply { putInt("medicineId", medicine.id) }
            )},
            onDelete = { medicine -> medicineViewModel.deleteMedicine(medicine) }
        )
        binding.rvMedicines.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMedicines.adapter = adapter

        // Observe medicine list
        medicineViewModel.medicines.observe(viewLifecycleOwner) { medicines ->
            adapter.submitList(medicines)
            // Show empty state if no medicines
            binding.layoutEmpty.visibility = if (medicines.isEmpty()) View.VISIBLE else View.GONE
            binding.rvMedicines.visibility  = if (medicines.isEmpty()) View.GONE else View.VISIBLE
        }

        // FAB to add new medicine
        binding.fabAddMedicine.setOnClickListener {
            findNavController().navigate(R.id.action_medicineFragment_to_addEditMedicineFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
