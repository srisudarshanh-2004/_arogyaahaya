package com.arogyasahaya.ui.asha

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.arogyasahaya.databinding.FragmentAshaBinding
import com.arogyasahaya.viewmodel.AshaViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

/**
 * ASHA Connect screen.
 * Shows a CalendarView and a list of upcoming health camp events below it.
 */
@AndroidEntryPoint
class AshaFragment : Fragment() {

    private var _binding: FragmentAshaBinding? = null
    private val binding get() = _binding!!

    private val ashaViewModel: AshaViewModel by viewModels()
    private lateinit var adapter: AshaEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAshaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AshaEventAdapter()
        binding.rvAshaEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAshaEvents.adapter = adapter

        // Observe upcoming events
        ashaViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)

            // Highlight event dates on the CalendarView
            // (Android's built-in CalendarView doesn't support multi-date highlighting,
            //  so we show the first upcoming event date)
            events.firstOrNull()?.let { firstEvent ->
                binding.calendarView.date = firstEvent.eventDate
            }

            binding.tvNoEvents.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
        }

        // When a calendar date is tapped, scroll to that day's events
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
            }
            // Filter events for the selected date in the adapter
            val filtered = ashaViewModel.allEvents.value?.filter { event ->
                val eventCal = Calendar.getInstance().apply { timeInMillis = event.eventDate }
                eventCal.get(Calendar.YEAR)         == year &&
                eventCal.get(Calendar.MONTH)        == month &&
                eventCal.get(Calendar.DAY_OF_MONTH) == dayOfMonth
            }
            if (!filtered.isNullOrEmpty()) {
                adapter.submitList(filtered)
            } else {
                // No events for that day — show all upcoming
                adapter.submitList(ashaViewModel.upcomingEvents.value)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
