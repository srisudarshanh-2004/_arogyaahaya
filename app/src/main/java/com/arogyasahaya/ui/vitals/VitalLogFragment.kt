package com.arogyasahaya.ui.vitals

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.arogyasahaya.R
import com.arogyasahaya.data.entity.VitalLogEntity
import com.arogyasahaya.databinding.FragmentVitalLogBinding
import com.arogyasahaya.viewmodel.VitalViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Vital Log screen:
 *  - Input form for BP systolic/diastolic, Heart Rate, Blood Glucose
 *  - MPAndroidChart line graph showing last 7 days of vitals
 */
@AndroidEntryPoint
class VitalLogFragment : Fragment() {

    private var _binding: FragmentVitalLogBinding? = null
    private val binding get() = _binding!!

    private val vitalViewModel: VitalViewModel by viewModels()
    private val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVitalLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChart()

        // Observe 7-day vital logs and update chart
        vitalViewModel.last7DaysLogs.observe(viewLifecycleOwner) { logs ->
            if (logs.isNotEmpty()) {
                updateChart(logs)
            }
        }

        // Save button
        binding.btnSaveVitals.setOnClickListener { saveVitals() }
    }

    private fun saveVitals() {
        val sysStr      = binding.etBpSystolic.text.toString().trim()
        val diaStr      = binding.etBpDiastolic.text.toString().trim()
        val hrStr       = binding.etHeartRate.text.toString().trim()
        val glucoseStr  = binding.etGlucose.text.toString().trim()

        if (sysStr.isEmpty() && hrStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter at least BP or Heart Rate.", Toast.LENGTH_SHORT).show()
            return
        }

        vitalViewModel.saveVitalLog(
            bpSystolic   = sysStr.toIntOrNull() ?: 0,
            bpDiastolic  = diaStr.toIntOrNull() ?: 0,
            heartRate    = hrStr.toIntOrNull() ?: 0,
            bloodGlucose = glucoseStr.toFloatOrNull() ?: 0f,
            notes        = binding.etVitalNotes.text.toString().trim()
        )

        // Clear fields
        binding.etBpSystolic.text?.clear()
        binding.etBpDiastolic.text?.clear()
        binding.etHeartRate.text?.clear()
        binding.etGlucose.text?.clear()
        binding.etVitalNotes.text?.clear()

        Toast.makeText(requireContext(), getString(R.string.vitals_saved), Toast.LENGTH_SHORT).show()
    }

    /**
     * Configure the MPAndroidChart LineChart appearance.
     */
    private fun setupChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            legend.isEnabled = true
            setNoDataText(getString(R.string.chart_no_data))

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textSize = 12f
            }
            axisLeft.apply {
                textSize = 12f
                setDrawGridLines(true)
            }
            axisRight.isEnabled = false
        }
    }

    /**
     * Fill the chart with real data from Room DB.
     * Shows 3 lines: BP Systolic, Heart Rate, Blood Glucose.
     */
    private fun updateChart(logs: List<VitalLogEntity>) {
        val labels = logs.map { dateFormat.format(Date(it.loggedAt)) }

        // Line 1: BP Systolic (red)
        val bpEntries = logs.mapIndexed { i, log ->
            Entry(i.toFloat(), log.bpSystolic.toFloat())
        }

        // Line 2: Heart Rate (blue)
        val hrEntries = logs.mapIndexed { i, log ->
            Entry(i.toFloat(), log.heartRate.toFloat())
        }

        // Line 3: Blood Glucose (green)
        val glucoseEntries = logs.mapIndexed { i, log ->
            Entry(i.toFloat(), log.bloodGlucose)
        }

        fun makeDataSet(entries: List<Entry>, label: String, color: Int): LineDataSet {
            return LineDataSet(entries, label).apply {
                this.color = color
                setCircleColor(color)
                lineWidth = 2.5f
                circleRadius = 4f
                setDrawValues(true)
                valueTextSize = 11f
            }
        }

        val dataSets = listOf(
            makeDataSet(bpEntries,      "BP Systolic",   Color.parseColor("#E53935")),
            makeDataSet(hrEntries,      "Heart Rate",    Color.parseColor("#1976D2")),
            makeDataSet(glucoseEntries, "Blood Glucose", Color.parseColor("#388E3C"))
        )

        binding.lineChart.apply {
            data = LineData(dataSets)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.labelCount = labels.size
            notifyDataSetChanged()
            invalidate()   // Refresh the chart
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
